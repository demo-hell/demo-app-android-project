package br.com.mobicare.cielo.commons.data.filter

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPI
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.presentation.filter.model.FilterReceivableResponse
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import io.reactivex.Observable

class FilterRepositoryNetwork(val cieloAPIServices: CieloAPIServices) : FilterRepository {

    override fun avaiableFilters(quickFilter: QuickFilter): Observable<FilterReceivableResponse> {
        return cieloAPIServices.avaiableReceivableFilters(quickFilter)
    }

}