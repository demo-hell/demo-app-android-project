package br.com.mobicare.cielo.commons.utils.dialog

import androidx.fragment.app.FragmentManager
import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.mfa.token.CieloMfaTokenGenerator
import br.com.mobicare.cielo.pix.constants.DEFAULT_OTP
import java.lang.ref.WeakReference

class BottomSheetValidationTokenWrapper(supportFragmentManager: FragmentManager) {
    private val bottomSheetValidationTokenFragment = BottomSheetValidationTokenFragment()
    private val userPreferences = UserPreferences.getInstance()
    private val featureTogglePreferences = FeatureTogglePreference.instance

    private var seed = MfaUserInformation(userPreferences).getMfaUser(userPreferences.userName)?.mfaSeed
    private val cieloMfaTokenGenerator = CieloMfaTokenGenerator(userPreferences, featureTogglePreferences)
    private val fragmentManagerRef = WeakReference(supportFragmentManager)

    private fun showValidationTokenBottomSheet() {
        fragmentManagerRef.get()?.let { fragmentManager ->
            val fragment = fragmentManager.findFragmentByTag(BottomSheetValidationTokenWrapper::class.java.simpleName)
            if (fragment != null) fragmentManager.beginTransaction().remove(fragment).commit()

            bottomSheetValidationTokenFragment.show(fragmentManager, this::class.java.simpleName)
        }
    }

    fun playAnimationSuccess(callbackValidateToken: CallbackValidateToken?) {
        bottomSheetValidationTokenFragment.playAnimationSuccess(callbackValidateToken)
    }

    fun playAnimationError(
        error: ErrorMessage? = null,
        callbackValidateToken: CallbackValidateToken?
    ) {
        bottomSheetValidationTokenFragment.playAnimationError(error, callbackValidateToken)
    }

    fun generateOtp(showAnimation: Boolean = false, onResult: (otpCode: String) -> Unit) {
        if (showAnimation) {
            generateOtCodeWithAnimation(onResult)
        } else {
            generateOtpCodeWithoutAnimation(onResult)
        }
    }

    private fun generateOtCodeWithAnimation(onResult: (otpCode: String) -> Unit) {
        showValidationTokenBottomSheet()

        bottomSheetValidationTokenFragment.generateOtp(object : MfaOtp {
            override fun onResult(otpCode: String) {
                onResult.invoke(otpCode)
            }
        })
    }

    private fun generateOtpCodeWithoutAnimation(onResult: (otpCode: String) -> Unit) {
        onResult(cieloMfaTokenGenerator.getOtpCode(seed) ?: DEFAULT_OTP)
    }

    interface CallbackValidateToken {
        fun callbackTokenSuccess() {}
        fun callbackTokenError() {}
    }

    interface MfaOtp {
        fun onResult(otpCode: String)
    }
}