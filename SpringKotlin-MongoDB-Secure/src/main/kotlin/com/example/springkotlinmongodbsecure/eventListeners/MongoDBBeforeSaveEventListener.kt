package com.example.springkotlinmongodbsecure.eventListeners

import com.example.springkotlinmongodbsecure.function.EncryptionUtil
import com.google.gson.Gson
import org.apache.logging.log4j.LogManager
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent
import java.util.*


class MongoDBBeforeSaveEventListener : AbstractMongoEventListener<Any>() {

    private val logger = LogManager.getLogger(
        MongoDBBeforeSaveEventListener::class.java
    )

    @Autowired
    private val encryptionUtil: EncryptionUtil? = null

    override fun onBeforeSave(event: BeforeSaveEvent<Any?>) {
        val eventObject: Document? = event.document

        val keysNotToEncrypt: List<String> = listOf("_class", "_id")
        for (key in eventObject?.keys!!) {
            if (!keysNotToEncrypt.contains(key)) {
                eventObject[key] = encryptionUtil!!.encrypt(eventObject[key].toString())
            }
        }
        logger.info("DB Object: " + Gson().toJson(eventObject))
        super.onBeforeSave(event)
    }
}
