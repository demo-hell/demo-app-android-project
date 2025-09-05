package br.com.mobicare.cielo.taxaPlanos.presentation.ui.taxasBandeiras


import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrandFees
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosSolutionResponse

interface TaxaPlanosBandeirasContract {

    interface Presenter  {
        fun loadBrandsFee()
        fun loadMarchine()
        fun onClieared()
    }

    interface View : BaseView, IAttached {
        fun showBrandsFee(fees: CardBrandFees)
        fun showMachine(response: TaxaPlanosSolutionResponse)
        fun showRgister()
    }
}