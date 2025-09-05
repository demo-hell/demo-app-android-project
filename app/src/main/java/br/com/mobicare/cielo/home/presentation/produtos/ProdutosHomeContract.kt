package br.com.mobicare.cielo.home.presentation.produtos

import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.home.presentation.produtos.domain.entities.ProdutoObj

/**
 * Created by david on 04/08/17.
 */

class ProdutosHomeContract {

    interface View : IAttached {
        fun showProgress()
        fun hideProgress()
        fun showError(error: String)
        fun loadData(transactions: List<ProdutoObj>?)
    }

    interface Presenter {
        fun callAPI()
    }
}
