package br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.addAccount.transferAccount

import android.os.Bundle
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.Origin


interface AddAccountContract {
    interface Presenter : BasePresenter<View> {
        fun resubmit()
    }

    interface View : BaseView, IAttached {
        fun transferSuccessWithToken()
        fun transferSuccess() {}
        fun transferInProcess()
        fun resubmit()
        fun showAddAccountErrorType(error: ErrorMessage){}

        fun errorOnOtpTemporaryBlocked(error: ErrorMessage) {}
        fun registerOtpSuccess() {}
        fun proceedToAddAccount(otpGenerated: String? = null) {}
        fun errorOnOtpGeneration(error: ErrorMessage) {}
        fun genericErrorOnOtpGeneration(error: ErrorMessage) {}
        fun genericBlockError(error: ErrorMessage) {}
        fun validateWithProcedure(
            bundle: Bundle? = null,
            transferAccountLambda: (origins: ArrayList<Origin>) -> Unit
        ) {}
    }
}