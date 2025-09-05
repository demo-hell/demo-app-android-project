package br.com.mobicare.cielo.mySales.data.model.responses

import br.com.mobicare.cielo.mySales.data.model.Pagination
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.Summary


data class SummarySalesResponse(
    val summary: Summary,
    val pagination: Pagination?,
    val items: List<Sale>
)