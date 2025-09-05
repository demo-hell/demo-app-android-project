package br.com.mobicare.cielo.dirf.analytics

import br.com.mobicare.cielo.commons.analytics.*

class DirfAnalytics {

    fun logSelectedDocument(selectedYear: String, formatType: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, DIRF),
            action = listOf(MY_SALES, DIRF),
            label = listOf(YEAR, selectedYear, FORMAT, formatType, DOWNLOAD)
        )
    }

    companion object {
        const val DIRF = "dirf"
        const val YEAR = "ano"
        const val FORMAT = "formato"
        const val DOWNLOAD = "baixar"
        const val MY_SALES = "minhas vendas"
        const val PDF_FORMAT = "pdf"
        const val EXCEL_FORMAT = "excel"
        const val DEC_FORMAT = "dec"
    }
}