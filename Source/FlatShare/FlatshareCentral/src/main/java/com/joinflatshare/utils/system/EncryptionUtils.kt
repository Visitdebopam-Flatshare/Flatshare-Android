package com.joinflatshare.utils.system

import android.util.Base64
import com.joinflatshare.FlatshareCentral.BuildConfig
import java.io.UnsupportedEncodingException
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


/**
 * Created by debopam on 29/12/22
 */
object EncryptionUtils {
    private val ITERATION_COUNT = 1000
    private val KEY_LENGTH = 256
    private val PBKDF2_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA1"
    private val CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"
    private val PKCS5_SALT_LENGTH = 32
    private val DELIMITER = "]"
    private val random: SecureRandom = SecureRandom()

    fun encrypt(plaintext: String): String {
        val salt = generateSalt()
        val key = deriveKey(BuildConfig.APPLICATION_ID, salt)
        return try {
            val cipher: Cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            val iv = generateIv(cipher.blockSize)
            val ivParams = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParams)
            val cipherText: ByteArray = cipher.doFinal(plaintext.toByteArray(charset("UTF-8")))
            String.format(
                "%s%s%s%s%s",
                toBase64(salt),
                DELIMITER,
                toBase64(iv),
                DELIMITER,
                toBase64(cipherText)
            )
        } catch (e: GeneralSecurityException) {
            throw java.lang.RuntimeException(e)
        } catch (e: UnsupportedEncodingException) {
            throw java.lang.RuntimeException(e)
        }
    }

    fun decrypt(ciphertext: String): String {
        val fields = ciphertext.split(DELIMITER).toTypedArray()
        require(fields.size == 3) { "Invalid encrypted text format" }
        val salt = fromBase64(fields[0])!!
        val iv = fromBase64(fields[1])!!
        val cipherBytes = fromBase64(fields[2])!!
        val key = deriveKey(BuildConfig.APPLICATION_ID, salt)
        return try {
            val cipher: Cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            val ivParams = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, key, ivParams)
            val plaintext: ByteArray = cipher.doFinal(cipherBytes)
            String(plaintext, charset("UTF-8"))
        } catch (e: GeneralSecurityException) {
            throw java.lang.RuntimeException(e)
        } catch (e: UnsupportedEncodingException) {
            throw java.lang.RuntimeException(e)
        }
    }

    private fun generateSalt(): ByteArray {
        val b = ByteArray(PKCS5_SALT_LENGTH)
        random.nextBytes(b)
        return b
    }

    private fun generateIv(length: Int): ByteArray {
        val b = ByteArray(length)
        random.nextBytes(b)
        return b
    }

    private fun deriveKey(password: String, salt: ByteArray): SecretKey? {
        return try {
            val keySpec: KeySpec =
                PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
            val keyFactory: SecretKeyFactory =
                SecretKeyFactory.getInstance(PBKDF2_DERIVATION_ALGORITHM)
            val keyBytes: ByteArray = keyFactory.generateSecret(keySpec).getEncoded()
            SecretKeySpec(keyBytes, "AES")
        } catch (e: GeneralSecurityException) {
            throw RuntimeException(e)
        }
    }

    private fun toBase64(bytes: ByteArray): String? {
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    private fun fromBase64(base64: String): ByteArray? {
        return Base64.decode(base64, Base64.NO_WRAP)
    }
}