package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.owner

import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.meuCadastroNovo.domain.Owner

interface EditarDadosProprietarioContract {

    interface Presenter : BasePresenter<View> {
        fun putOwnerData(owner: Owner)
        fun save(otpCode: String, email: String, phone1: String, phone2: String, phone3: String)
        fun getUserName(): String
    }

    interface View : BaseView, IAttached {
        fun showOwnerData(owner: Owner)
        fun showPhoneFillError(isShow: Boolean)
        fun showEmailFillError(isShow: Boolean)
        fun logout()
        fun showSaveSuccessful()
    }

}