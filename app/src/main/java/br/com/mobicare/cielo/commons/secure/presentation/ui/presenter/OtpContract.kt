package br.com.mobicare.cielo.commons.secure.presentation.ui.presenter

import androidx.annotation.StringRes
import br.com.mobicare.cielo.commons.presentation.CommonPresenter

interface OtpContract {

    interface Presenter : CommonPresenter {
        fun showOtpCodeForFirstTime()
    }

    interface View {
        fun showOtp(otpGenerated: String)
        fun updateCountdownAnimation(elapsedSlicePercent: Double)
        fun errorOnOtpGeneration(@StringRes errorCustomMessage: Int = 0)
    }

}