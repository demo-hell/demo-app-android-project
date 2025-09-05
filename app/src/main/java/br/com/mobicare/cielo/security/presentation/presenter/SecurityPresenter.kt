package br.com.mobicare.cielo.security.presentation.presenter

import androidx.biometric.BiometricPrompt.*
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.security.presentation.ui.SecurityContract
import com.google.firebase.crashlytics.FirebaseCrashlytics

class SecurityPresenter(private val mView: SecurityContract.View) : SecurityContract.Presenter {

    override fun enableFingerPrint(enable: Boolean) {
        mView.biometricPrompt(enable, callbackBiometric(enable))
    }

    private fun callbackBiometric(activate: Boolean): AuthenticationCallback {
        return object : AuthenticationCallback() {

            override fun onAuthenticationSucceeded(result: AuthenticationResult) {
                mView.showFingerprintCaptureSuccess(activate)
            }

            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)

                when (errorCode) {
                    in ERROR_HW_UNAVAILABLE..ERROR_LOCKOUT_PERMANENT -> {
                        mView.showFingerprintCaptureError(R.string.text_unavailable)

                    }
                    in ERROR_NO_BIOMETRICS..ERROR_HW_NOT_PRESENT -> {
                        mView.showFingerprintCaptureError(R.string.text_not_enrolled)
                    }
                    else -> {
                        mView.showFingerprintCaptureError(R.string.text_fingerprint_prompt_error_message_retry)
                        FirebaseCrashlytics.getInstance()
                            .log(errString.toString())
                    }
                }
            }
        }
    }
}