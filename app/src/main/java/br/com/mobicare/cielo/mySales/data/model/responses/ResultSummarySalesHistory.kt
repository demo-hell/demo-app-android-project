package br.com.mobicare.cielo.mySales.data.model.responses

import br.com.mobicare.cielo.mySales.data.model.Pagination
import br.com.mobicare.cielo.mySales.data.model.SaleHistory
import br.com.mobicare.cielo.mySales.data.model.Summary

data class ResultSummarySalesHistory(
    val summary: Summary,
    val pagination: Pagination,
    val items: List<SaleHistory>?
)