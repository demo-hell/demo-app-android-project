package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.nomeFantasia

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached

interface EditarDadosNomeFantasiaContract {

    interface Presenter {
        fun resubmit()
        fun loadReceitaFederal()
        fun saveNameReceitaFederal()
        fun onCleared()
    }

    interface View : BaseView, IAttached {
        fun showUpdateError()
        fun showUpdateSuccess()
        fun showReceitaFederal(name: String)
    }

}