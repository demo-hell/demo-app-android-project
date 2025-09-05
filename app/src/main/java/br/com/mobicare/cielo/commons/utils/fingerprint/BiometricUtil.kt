package br.com.mobicare.cielo.commons.utils.fingerprint

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.security.keystore.KeyPermanentlyInvalidatedException
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.ERROR_NO_BIOMETRICS
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.helpers.BiometricHelper
import br.com.mobicare.cielo.commons.utils.MainThreadExecutor
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.lang.reflect.Method
import java.security.KeyStore
import java.util.*


private lateinit var biometricPrompt: BiometricPrompt
private lateinit var biometricPromptAuthCallbackLocal: BiometricPrompt.AuthenticationCallback
private lateinit var promptInfo: BiometricPrompt.PromptInfo
private lateinit var cryptographyManager: CryptographyManager
private lateinit var secretKeyName: String

private fun handleBiometricPrompt(
    context: Context,
    title: String,
    subtitle: String? = null,
    description: String,
    biometricPromptAuthCallback: BiometricPrompt.AuthenticationCallback,
): BiometricPrompt {
    biometricPromptAuthCallbackLocal = biometricPromptAuthCallback
    val biometricPromptBuilder = BiometricPrompt.PromptInfo.Builder()
    subtitle?.run {
        biometricPromptBuilder.setSubtitle(this)
    }
    promptInfo = biometricPromptBuilder.setDescription(description)
        .setTitle(title)
        .setNegativeButtonText(context.getString(R.string.text_cancel_label))
        .build()
    secretKeyName = context.getString(R.string.secret_key_name)

    if (isAndroidVersionOorOMR1()) {
        biometricPrompt.authenticate(promptInfo)

    } else {
        cryptographyManager = cryptographyManager()
        authenticateToEncrypt(context)
    }
    return biometricPrompt
}

fun Fragment.createBiometricPrompt(
    title: String,
    subtitle: String? = null,
    description: String,
    biometricPromptAuthCallback: BiometricPrompt.AuthenticationCallback,
): BiometricPrompt {
    biometricPrompt = BiometricPrompt(
        this, MainThreadExecutor(),
        biometricPromptAuthCallback
    )
    return handleBiometricPrompt(
        requireContext(),
        title,
        subtitle,
        description,
        biometricPromptAuthCallback
    )
}


fun FragmentActivity.createBiometricPromptFragmentActivity(
    title: String,
    subtitle: String? = null,
    description: String,
    biometricPromptAuthCallback: BiometricPrompt.AuthenticationCallback,
): BiometricPrompt {
    biometricPrompt = BiometricPrompt(
        this, MainThreadExecutor(),
        biometricPromptAuthCallback
    )
    return handleBiometricPrompt(this, title, subtitle, description, biometricPromptAuthCallback)
}

private fun authenticateToEncrypt(context: Context) {
    if (BiometricManager.from(context).canAuthenticate() == BiometricManager
            .BIOMETRIC_SUCCESS
    ) {
        try {
            val cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
            biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
        } catch (e: KeyPermanentlyInvalidatedException) {
            val keyStore = KeyStore.getInstance(context.getString(R.string.androidkeystore))
            keyStore.load(null)
            keyStore.deleteEntry(context.getString(R.string.secret_key_name))
            biometricPromptAuthCallbackLocal.onAuthenticationError(
                ERROR_NO_BIOMETRICS,
                "FingerPrint Added"
            )

        } catch (e: Exception) {
            biometricPromptAuthCallbackLocal.onAuthenticationError(
                ERROR_NO_BIOMETRICS,
                "FingerPrint Added"
            )
        }
    }


}

fun validateNewFingerprintAdded(context: Context): Boolean {
    if (isAndroidVersionOorOMR1()) {
        return FingerPrintChanged().hasNewFingerPrint(context).not()
    } else {
        try {
            secretKeyName = context.getString(R.string.secret_key_name)
            cryptographyManager = cryptographyManager()
            cryptographyManager.getInitializedCipherForEncryption(secretKeyName)
        } catch (e: KeyPermanentlyInvalidatedException) {
            val keyStore = KeyStore.getInstance(context.getString(R.string.androidkeystore))
            keyStore.load(null)
            keyStore.deleteEntry(context.getString(R.string.secret_key_name))
            UserPreferences.getInstance().cleanFingerprintData()
            return false
        } catch (ex: Exception) {
            ex.addSuppressed(Exception("Biometric is strong ${BiometricHelper.isStrong(context)}"))
            FirebaseCrashlytics.getInstance()
                .recordException(ex)

            return false
        }
        return true
    }
}

@SuppressLint("PrivateApi", "DiscouragedPrivateApi")
@TargetApi(Build.VERSION_CODES.O or Build.VERSION_CODES.O_MR1)
fun getFingerPrintUniqueID(context: Context): Set<Int> {
    val setFingerId: MutableSet<Int> = HashSet()
    try {
        val fingerprintManager =
            context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        val method: Method =
            FingerprintManager::class.java.getDeclaredMethod("getEnrolledFingerprints")
        val obj: Any? = method.invoke(fingerprintManager)
        val clazz = Class.forName("android.hardware.fingerprint.Fingerprint")
        val getFingerId: Method = clazz.getDeclaredMethod("getFingerId")
        for (i in (obj as List<*>).indices) {
            val item = obj[i]
            if (item != null) {
                setFingerId.add(getFingerId.invoke(item) as Int)
            }
        }
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
    }
    return setFingerId
}


fun saveFingerPrints(context: Context?) {
    var setFingerPrintDeviceIds: Set<Int?> = java.util.HashSet()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setFingerPrintDeviceIds = context?.let { getFingerPrintUniqueID(it) }!!
    }
    UserPreferences.getInstance().setFingerprintIds(setFingerPrintDeviceIds)
}

fun isSequentialID(list: List<Int>): Boolean {
    Collections.sort(list)
    var prev: Int? = null
    var seq = 0
    for (i in list) {
        if (prev != null && prev + 1 == i) seq = if (seq == 0) 2 else seq + 1
        prev = i
    }
    return seq >= 3
}

fun isFingerprintSequentialID(context: Context): Boolean {
    val setFingerPrintDeviceIds: Set<Int?> = getFingerPrintUniqueID(context)
    val isSequential = isSequentialID(setFingerPrintDeviceIds.toList() as List<Int>)
    if (isSequential) {
        UserPreferences.getInstance().cleanFingerprintData()
    }
    return isSequential
}

fun isAndroidVersionOorOMR1(): Boolean {
    return Build.VERSION.SDK_INT == Build.VERSION_CODES.O || Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1
}