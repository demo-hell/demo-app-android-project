package br.com.mobicare.cielo.mySales.data.model.bo

import br.com.mobicare.cielo.mySales.data.model.Pagination
import br.com.mobicare.cielo.mySales.data.model.SaleHistory
import br.com.mobicare.cielo.mySales.data.model.Summary


data class ResultSummarySalesHistoryBO(
    val summary: Summary,
    val pagination: Pagination?,
    val items: List<SaleHistory>?
)