package com.example.springkotlinmongodbsecure.function

import org.jasypt.util.text.BasicTextEncryptor
import org.springframework.stereotype.Component

@Component
class EncryptionUtil {

    var textEncryptor: BasicTextEncryptor? = null

    fun encryptionUtil() {
        textEncryptor = BasicTextEncryptor()
        textEncryptor!!.setPassword("random-password")
    }

    fun encrypt(text: String): String {
        return textEncryptor!!.encrypt(text)
    }

    fun decrypt(text: String): String {
        return textEncryptor!!.decrypt(text)
    }

}
