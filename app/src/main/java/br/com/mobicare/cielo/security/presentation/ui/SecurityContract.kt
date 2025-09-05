package br.com.mobicare.cielo.security.presentation.ui

import androidx.annotation.StringRes
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment

interface SecurityContract {

    interface View {
        fun showFingerprintCaptureSuccess(activate: Boolean)
        fun showFingerprintCaptureError(errorCustomMessage: Int)
        fun biometricPrompt(activate: Boolean,authenticationCallback:   BiometricPrompt.AuthenticationCallback
        )
    }

    interface Presenter {
        fun enableFingerPrint(enable: Boolean)
    }

}