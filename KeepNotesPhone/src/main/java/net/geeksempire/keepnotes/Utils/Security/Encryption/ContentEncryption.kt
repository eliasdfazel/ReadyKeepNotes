package net.geeksempire.keepnotes.Utils.Security.Encryption

import android.util.Base64
import java.nio.charset.Charset
import java.security.InvalidKeyException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class ContentEncryption {

    @Throws(Exception::class)
    private fun generateEncryptionKey(passwordKey: String): SecretKeySpec {

        return SecretKeySpec(passwordKey.toByteArray(), "AES")
    }

    private fun generatePasswordKey(rawPasswordString: String): String {
        val rawPasswordString = rawPasswordString + "0000000000000000"
        return rawPasswordString.substring(0, 16)
    }

    @Throws(InvalidKeyException::class)
    fun encryptEncodedData(plainText: String, rawPasswordString: String): ByteArray {

        //First Encode
        //Second Encrypt

        val encodedText: String = encodeStringBase64(plainText)

        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, generateEncryptionKey(generatePasswordKey(rawPasswordString)))

        return cipher.doFinal(encodedText.toByteArray(Charset.defaultCharset()))
    }

    @Throws(Exception::class)
    fun decryptEncodedData(encryptedByteArray: ByteArray, rawPasswordString: String): String? {

        //First Decrypt
        //Second Decode

        var plainText: String? = null

        try {
            var cipherD: Cipher? = null
            cipherD = Cipher.getInstance("AES")
            cipherD!!.init(Cipher.DECRYPT_MODE, generateEncryptionKey(generatePasswordKey(rawPasswordString)))
            val decryptString = String(cipherD.doFinal(encryptedByteArray), Charset.defaultCharset())

            plainText = decodeStringBase64(decryptString)
        } catch (e: Exception) {
            e.printStackTrace()

            plainText = encryptedByteArray.toString()
        }

        return plainText
    }

    @Throws(Exception::class)
    private fun encodeStringBase64(plainText: String): String {
        return Base64.encodeToString(plainText.toByteArray(), Base64.DEFAULT)
    }

    @Throws(Exception::class)
    private fun decodeStringBase64(encodedText: String): String {
        return String(Base64.decode(encodedText, Base64.DEFAULT))
    }

    fun plainTextToByteArray(plainText: String): ByteArray {
        val listOfRawString = plainText.replace("[", "").replace("]", "").split(",")

        val resultByteArray = ByteArray(listOfRawString.size)

        for (aByte in listOfRawString.withIndex()) {

            try {

                resultByteArray[aByte.index] = aByte.value.replace("\\s".toRegex(), "").toByte()

            } catch (e: Exception) {
                e.printStackTrace()

            }

        }

        return resultByteArray
    }

}