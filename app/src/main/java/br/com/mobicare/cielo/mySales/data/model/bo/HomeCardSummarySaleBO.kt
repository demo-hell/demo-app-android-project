package br.com.mobicare.cielo.mySales.data.model.bo

import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.Summary

data class HomeCardSummarySaleBO(
    val summary: Summary,
    val lastSale: Sale
)
