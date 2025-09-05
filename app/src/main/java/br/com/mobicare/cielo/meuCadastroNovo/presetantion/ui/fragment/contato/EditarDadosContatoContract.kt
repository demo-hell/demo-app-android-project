package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.contato

import androidx.annotation.StringRes
import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.meuCadastroNovo.domain.Contact

interface EditarDadosContatoContract {

    interface Presenter : BasePresenter<View> {
        fun putContactData(contact: Contact)
        fun save(
            nome: String,
            email: String,
            telefone1: String,
            telefone2: String,
            telefone3: String,
            otpCode: String
        )
        fun getUserName(): String
    }

    interface View : BaseView, IAttached {
        fun showContactData(contact: Contact)
        fun showPhoneFillError(idx: Int, @StringRes stringResId: Int? = null)
        fun showEmailFillError(isShow: Boolean)
        fun showNameFillError(isShow: Boolean)
        fun showInvalidEmail(isShow: Boolean)
        fun logout()
        fun showSaveSuccessful()
    }

}