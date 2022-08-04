package com.example.springkotlinmongodbsecure.eventListeners

import com.example.springkotlinmongodbsecure.function.EncryptionUtil
import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.AfterLoadEvent

class MongoDBAfterLoadEventListener : AbstractMongoEventListener<Any>() {

    private val logger: Logger = LogManager.getLogger(MongoDBAfterLoadEventListener::class.java)


    @Autowired
    private var encryptionUtil: EncryptionUtil? = EncryptionUtil()


    override fun onAfterLoad(event: AfterLoadEvent<Any>) {
        val eventObj: Document? = event.document

        val keysNotToDecrypt: List<String> = listOf("_class", "_id")

        for (key in eventObj?.keys!!) {
            if (!keysNotToDecrypt.contains(key)) {
                eventObj[key] = encryptionUtil?.decrypt(eventObj[key].toString())
            }
        }
        logger.info(("DB Oject" + Gson().toJson(eventObj)))
        super.onAfterLoad(event)
    }
}
