package br.com.mobicare.cielo.pix.domain

import br.com.mobicare.cielo.commons.constants.FORMAT_DATE_AMERICAN
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class FilterExtract private constructor(builder: Builder) : Serializable {
    var startDate: String?
    var endDate: String?
    val transferType: String?
    val cashFlowType: String?
    val period: String?
    val qtdFilters: Int?

    init {
        this.startDate = builder.startDate
        this.endDate = builder.endDate
        this.transferType = builder.transferType
        this.cashFlowType = builder.cashFlowType
        this.qtdFilters = builder.qtdFilters
        this.period = builder.period
    }

    class Builder {
        var startDate: String? = null
            private set

        var endDate: String? = null
            private set

        var transferType: String? = null
            private set

        var cashFlowType: String? = null
            private set

        var qtdFilters: Int? = null

        var period: String? = null

        fun initialDate(date: String?) = apply { this.startDate = date }

        fun initialDate(date: Date) = apply {
            val sdf = SimpleDateFormat(FORMAT_DATE_AMERICAN)
            this.startDate = sdf.format(date)
        }

        fun finalDate(date: String?) = apply { this.endDate = date }

        fun finalDate(date: Date) = apply {
            val sdf = SimpleDateFormat(FORMAT_DATE_AMERICAN)
            this.endDate = sdf.format(date)
        }

        fun qtdFilters(qtd: Int?) = apply { this.qtdFilters = qtd }

        fun cashFlowType(cashFlowType: String?) = apply { this.cashFlowType = cashFlowType }

        fun transferType(transferType: String?) = apply { this.transferType = transferType }

        fun period(period: String?) = apply { this.period = period }

        fun from(filter: FilterExtract?) = apply {
            this.startDate = filter?.startDate
            this.endDate = filter?.endDate
            this.transferType = filter?.transferType
            this.cashFlowType = filter?.cashFlowType
            this.period = filter?.period
        }

        fun build() = FilterExtract(this)
    }
}