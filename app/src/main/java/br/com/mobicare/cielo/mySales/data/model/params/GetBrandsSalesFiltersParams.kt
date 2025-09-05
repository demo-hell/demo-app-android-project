package br.com.mobicare.cielo.mySales.data.model.params

import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

data class GetBrandsSalesFiltersParams(
    val authorization: String,
    val accessToken: String,
    val quickFilter: QuickFilter?
)