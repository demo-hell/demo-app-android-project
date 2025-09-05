package br.com.mobicare.cielo.commons.presentation.filter

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.commons.presentation.filter.model.FilterReceivableResponse
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

interface FilterContract {

    interface Presenter : CommonPresenter {

        fun avaiableFilters(quickFilter: QuickFilter)

    }

    interface View : BaseView {

        //TODO colocar o objeto de retorno que provavelmente será uma lista de branchs e formas de pagamento
        fun displayAvaiableFilters(filterReceivableResponse: FilterReceivableResponse)

    }


}