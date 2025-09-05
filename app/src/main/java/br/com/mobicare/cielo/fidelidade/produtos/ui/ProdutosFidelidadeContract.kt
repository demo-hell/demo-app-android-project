package br.com.mobicare.cielo.fidelidade.produtos.ui

import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.fidelidade.domains.ProdutoFidelidadeObj

/**
 * Created by silvia.miranda on 21/08/2017.
 */
class ProdutosFidelidadeContract {

    interface View: IAttached {
        fun loadItensCard(produtos: ArrayList<ProdutoFidelidadeObj>?)
        fun showProgress()
        fun hideProgress()
    }

    interface Presenter {

        fun callAPI()
    }
}