package br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.cardActivateCateno

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.CardActivationCatenoRequest

interface CardNewPasswordContract {

    interface View : BaseView, IAttached, CardNewPasswordListener {
        fun passwordEmpty()
        fun passwordConfirmEmpty()
        fun passwordNotMatch()
        fun showSubmit(error: ErrorMessage)
        fun showSuccessActivation()
        fun showInvalidCardNumber()
    }

    interface Presenter {
        fun onCleared()
        fun isValidPassword(cardActivation: CardActivationCatenoRequest) : Boolean
        fun activateCard(proxy: String, cvv: String, dt: String, changePasswordCard: CardActivationCatenoRequest)
    }


}