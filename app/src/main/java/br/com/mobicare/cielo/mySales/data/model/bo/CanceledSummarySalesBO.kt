package br.com.mobicare.cielo.mySales.data.model.bo

import br.com.mobicare.cielo.mySales.data.model.CanceledSale
import br.com.mobicare.cielo.mySales.data.model.Pagination
import br.com.mobicare.cielo.mySales.data.model.Summary


data class CanceledSummarySalesBO(
    val summary: Summary? = null,
    val pagination: Pagination? = null,
    var items: MutableList<CanceledSale>? = null
)