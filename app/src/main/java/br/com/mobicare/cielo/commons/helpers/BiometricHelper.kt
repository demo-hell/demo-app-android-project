package br.com.mobicare.cielo.commons.helpers

import android.content.Context
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.core.hardware.fingerprint.FingerprintManagerCompat

class BiometricHelper {

    companion object {
        fun canAuthenticateWithBiometrics(context: Context): Boolean {
            val fingerprintManagerCompat = FingerprintManagerCompat.from(context)
            return fingerprintManagerCompat.isHardwareDetected &&
                    fingerprintManagerCompat.hasEnrolledFingerprints()
        }

        fun isHardwareDetected(context: Context): Boolean {
            val fingerprintManagerCompat = FingerprintManagerCompat.from(context)
            return fingerprintManagerCompat.isHardwareDetected
        }

        fun hasEnrolledFingerprints(context: Context): Boolean {
            val fingerprintManagerCompat = FingerprintManagerCompat.from(context)
            return fingerprintManagerCompat.hasEnrolledFingerprints()
        }

        fun isStrong(context: Context): Boolean {
            return try {
                val manager = BiometricManager.from(context)
                val canAuthenticate = manager.canAuthenticate(BIOMETRIC_STRONG)
                canAuthenticate == BIOMETRIC_SUCCESS
            } catch (ex: Exception){
                false
            }
        }

    }
}