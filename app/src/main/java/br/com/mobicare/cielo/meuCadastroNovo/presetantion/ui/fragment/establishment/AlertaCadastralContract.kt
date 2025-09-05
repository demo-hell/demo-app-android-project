package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.meuCadastroNovo.domain.Owner

interface AlertaCadastralContract {

    interface Presenter {
        fun submitOwnerData(opCode: String, owner: Owner)
        fun getUserName(): String
    }

    interface View: BaseView {
        fun removeAlertMessage()
        fun addAlertMessage()
    }
}