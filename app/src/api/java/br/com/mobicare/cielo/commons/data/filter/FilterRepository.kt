package br.com.mobicare.cielo.commons.data.filter

import br.com.mobicare.cielo.commons.presentation.filter.model.FilterReceivableResponse
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import io.reactivex.Observable

interface FilterRepository {

    fun avaiableFilters(quickFilter: QuickFilter): Observable<FilterReceivableResponse>

}