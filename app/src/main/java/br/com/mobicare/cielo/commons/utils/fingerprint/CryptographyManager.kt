package br.com.mobicare.cielo.commons.utils.fingerprint

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import br.com.mobicare.cielo.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.nio.charset.Charset
import java.security.KeyStore
import java.security.UnrecoverableKeyException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

interface CryptographyManager {

    fun getInitializedCipherForEncryption(keyName: String): Cipher
    fun getInitializedCipherForDecryption(keyName: String, initializationVector: ByteArray): Cipher
    fun encryptData(plaintext: String, cipher: Cipher): EncryptedData
    fun decryptData(cipherText: ByteArray, cipher: Cipher): String
}

fun cryptographyManager(): CryptographyManager = CryptographyManagerImpl()

data class EncryptedData(val cipherText: ByteArray, val initializationVector: ByteArray)

private class CryptographyManagerImpl : CryptographyManager {

    private val KEY_SIZE: Int = 256
    private val KEY_CIPHER: Int = 128
    private val ANDROID_KEYSTORE = "AndroidKeyStore"
    private val CHARSET_NAME = "UTF-8"
    private val BIOMETRIC_ENCRYPTION_KEY = "biometric_encryption_key"
    private val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES


    override fun getInitializedCipherForEncryption(keyName: String): Cipher {
        val cipher = getCipher()
        val secretKey = getOrCreateSecretKey(keyName)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return cipher
    }


    override fun getInitializedCipherForDecryption(
        keyName: String,
        initializationVector: ByteArray
    ): Cipher {
        val cipher = getCipher()
        val secretKey = getOrCreateSecretKey(keyName)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(KEY_CIPHER, initializationVector))
        return cipher
    }

    override fun encryptData(plaintext: String, cipher: Cipher): EncryptedData {
        val cipherText = cipher.doFinal(plaintext.toByteArray(Charset.forName(CHARSET_NAME)))
        return EncryptedData(cipherText, cipher.iv)
    }

    override fun decryptData(cipherText: ByteArray, cipher: Cipher): String {
        val plaintext = cipher.doFinal(cipherText)
        return String(plaintext, Charset.forName(CHARSET_NAME))
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance("$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING")
    }



    private fun getOrCreateSecretKey(keyName: String): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
        keyStore.load(null)
        try {
            keyStore.getKey(keyName, null)?.let { return it as SecretKey }
        }catch (ex: Exception) {
            when(ex) {
                is KeyPermanentlyInvalidatedException, is UnrecoverableKeyException -> {
                    keyStore.deleteEntry(BIOMETRIC_ENCRYPTION_KEY)
                }
                else -> {
                    keyStore.deleteEntry(BIOMETRIC_ENCRYPTION_KEY)
                    FirebaseCrashlytics.getInstance().log(ex.message.toString())
                }
            }
        }

        val paramsBuilder = KeyGenParameterSpec.Builder(
            keyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
        paramsBuilder.apply {
            setBlockModes(ENCRYPTION_BLOCK_MODE)
            setEncryptionPaddings(ENCRYPTION_PADDING)
            setKeySize(KEY_SIZE)
            setUserAuthenticationRequired(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setInvalidatedByBiometricEnrollment(true)
            }
        }

        val keyGenParams = paramsBuilder.build()
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        keyGenerator.init(keyGenParams)
        return keyGenerator.generateKey()
    }
}