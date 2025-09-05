package br.com.mobicare.cielo.biometricNotification.ui

import androidx.lifecycle.ViewModel
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences

class BiometricNotificationBottomSheetViewModel(
    private val userPreferences: UserPreferences
): ViewModel() {

    fun saveShowBiometricNotification (value: Boolean){
        userPreferences.saveShowBiometricNotification(value)
    }
    fun saveFingerprintData(encryptedMessage: ByteArray?) {
        userPreferences.saveFingerprintData(encryptedMessage)
    }
    fun saveFingerprintRecorded(fingerprintStatus: Boolean) {
        userPreferences.saveFingerprintRecorded(fingerprintStatus)
    }
    fun getKeepUserPassword(): String {
       return userPreferences.keepUserPassword
    }
    fun saveCalledBiometricNotificationByLogin(calledByLogin: Boolean) {
        userPreferences.saveCalledBiometricNotificationByLogin(calledByLogin)
    }
}