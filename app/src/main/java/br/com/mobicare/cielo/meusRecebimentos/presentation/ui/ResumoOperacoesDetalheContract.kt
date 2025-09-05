package br.com.mobicare.cielo.meusRecebimentos.presentation.ui

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.PostingOfDetailDetailObject

/**
 * Created by silvia.miranda on 12/07/2017.
 */


interface ResumoOperacoesDetalheContract {


    interface View : IAttached {
        fun showProgress()
        fun hideProgress()
        fun loadDetalhes(objSystemMessage: ArrayList<PostingOfDetailDetailObject>)
        fun appendDetalhes(objSystemMessage: ArrayList<PostingOfDetailDetailObject>)
        fun logout(error: String)
        fun error(error: ErrorMessage)
        fun loadDados(valorTotal: Double, roNumber: String?)
        fun addScrollEvent()
        fun removeScrollEvent()
    }

    interface Presenter{
        fun callAPI(id: String?, cvsQty: String?, payDay: String?, uniqueKeyROPart1: String?, uniqueKeyROPart2: String?, uniqueKeyROPart3: String?, finalDate: String?, merchantId: String?)
        fun onStart()
        fun onFinish()
        fun onError(error: ErrorMessage)
    }
}