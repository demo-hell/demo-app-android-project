package br.com.mobicare.cielo.minhasVendas.fragments.common

import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.CardBrands
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

class QuickFilter private constructor(builder: Builder) : Serializable {
    val initialDate: String?
    val finalDate: String?
    val cardBrand: List<Int>?
    var listBrandSales:MutableList<CardBrands>? = null
    val paymentType: List<Int>?
    val terminal: List<String>?
    val status: List<Int>?
    val cardNumber: Int?
    val nsu: String?
    val tid: String?
    val authorizationCode: String?
    val initialAmount: Double?
    val finalAmount: Double?
    val customId: String?
    val saleCode: String?
    val truncatedCardNumber: String?
    val saleGrossAmount: Double?
    val grossAmount: Double?
    val transactionTypeCode: Int?
    val merchantId: String?
    val roNumber: String?
    val operationNumber: String?
    val frequency: String?
    val sku: String?
    val expiredDate: String?
    val maximumInstallment: Int?
    val softDescriptor: String?
    val finalRecurrentExpiration: String?
    val quantity: Int?
    val identificationNumber: String?
    val statusType: String?

    init {
        this.initialDate = builder.initialDate
        this.finalDate = builder.finalDate
        this.cardBrand = builder.cardBrand
        this.listBrandSales = builder.listBrandSeles
        this.paymentType = builder.paymentType
        this.terminal = builder.terminal
        this.status = builder.status
        this.cardNumber = builder.cardNumber
        this.nsu = builder.nsu
        this.tid = builder.tid
        this.authorizationCode = builder.authorizationCode
        this.initialAmount = builder.initialAmount
        this.finalAmount = builder.finalAmount
        this.customId = builder.customId
        this.saleCode = builder.saleCode
        this.truncatedCardNumber = builder.truncatedCardNumber
        this.saleGrossAmount = builder.saleGrossAmount
        this.grossAmount = builder.grossAmount
        this.transactionTypeCode = builder.transactionTypeCode
        this.merchantId = builder.merchantId
        this.roNumber = builder.roNumber
        this.operationNumber = builder.operationNumber
        this.frequency = builder.frequency
        this.sku = builder.sku
        this.expiredDate = builder.expiredDate
        this.maximumInstallment = builder.maximumInstallment
        this.softDescriptor = builder.softDescriptor
        this.finalRecurrentExpiration = builder.finalRecurrentExpiration
        this.quantity = builder.quantity
        this.identificationNumber = builder.identificationNumber
        this.statusType = builder.statusType
    }

    class Builder {
        var initialDate: String? = null
            private set

        var finalDate: String? = null
            private set

        var cardBrand: List<Int>? = null
            private set

        var listBrandSeles: MutableList<CardBrands>? = null
            private set

        var paymentType: List<Int>? = null
            private set

        var terminal: List<String>? = null
            private set

        var status: List<Int>? = null
            private set

        var cardNumber: Int? = null
            private set

        var nsu: String? = null
            private set

        var tid: String? = null
            private set

        var authorizationCode: String? = null
            private set

        var initialAmount: Double? = null
            private set

        var finalAmount: Double? = null
            private set

        var customId: String? = null
            private set

        var saleCode: String? = null
            private set

        var truncatedCardNumber: String? = null
            private set

        var saleGrossAmount: Double? = null
            private set

        var grossAmount: Double? = null
            private set

        var transactionTypeCode: Int? = null
            private set

        var merchantId: String? = null
            private set

        var roNumber: String? = null
            private set
        var operationNumber: String? = null
            private set
        var frequency: String? = null
            private set
        var sku: String? = null
            private set
        var expiredDate: String? = null
            private set
        var maximumInstallment: Int? = null
            private set
        var softDescriptor: String? = null
            private set
        var finalRecurrentExpiration: String? = null
            private set
        var quantity: Int? = null
            private set
        var identificationNumber: String? = null
            private set
        var statusType: String? = null
            private set

        fun initialDate(date: String?) = apply { this.initialDate = date }
        fun initialDate(date: Date) = apply {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            this.initialDate = sdf.format(date)
        }

        fun finalDate(date: String?) = apply { this.finalDate = date }
        fun finalDate(date: Date) = apply {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            this.finalDate = sdf.format(date)
        }

        fun cardBrand(brands: List<Int>?) = apply { this.cardBrand = brands }
        fun listBrandSales(brands: MutableList<CardBrands>?) = apply { this.listBrandSeles = brands }

        fun paymentType(types: List<Int>?) = apply { this.paymentType = types }
        fun terminal(terminals: List<String>?) = apply { this.terminal = terminals }
        fun status(status: List<Int>) = apply { this.status = status }
        fun cardNumber(number: Int?) = apply { this.cardNumber = number }
        fun nsu(number: String?) = apply { this.nsu = number }
        fun tid(number: String?) = apply { this.tid = number }
        fun authorizationCode(code: String?) = apply { this.authorizationCode = code }
        fun initialAmount(amount: Double?) = apply { this.initialAmount = amount }
        fun finalAmount(amount: Double?) = apply { this.finalAmount = amount }
        fun customId(id: String?) = apply { this.customId = id }
        fun saleCode(code: String?) = apply { this.saleCode = code }
        fun truncatedCardNumber(number: String?) = apply { this.truncatedCardNumber = number }
        fun grossAmount(grossAmount: Double?) = apply {
            this.grossAmount = grossAmount
        }

        fun saleGrossAmount(saleGrossAmount: Double?) = apply {
            this.saleGrossAmount = saleGrossAmount
        }

        fun transactionTypeCode(code: Int?) = apply { this.transactionTypeCode = code }
        fun merchantId(id: String?) = apply { this.merchantId = id }
        fun roNumber(number: String?) = apply { this.roNumber = number }
        fun operationNumber(number: String?) = apply { this.operationNumber = number }
        fun frequency(frequency: String) = apply { this.frequency = frequency }
        fun sku(sku: String?) = apply { this.sku = sku }
        fun expiredDate(expiredDate: String?) = apply { this.expiredDate = expiredDate }
        fun maximumInstallment(maximumInstallment: Int?) = apply { this.maximumInstallment = maximumInstallment }
        fun softDescriptor(softDescriptor: String?) = apply { this.softDescriptor = softDescriptor }
        fun finalRecurrentExpiration(finalRecurrentExpiration: String) = apply { this.finalRecurrentExpiration = finalRecurrentExpiration }
        fun quantity(quantity: Int?) = apply { this.quantity = quantity }
        fun identificationNumber(identificationNumber: String?) = apply { this.identificationNumber = identificationNumber }
        fun statusType(statusType: String?) = apply { this.statusType = statusType }

        fun from(quickFilter: QuickFilter) = apply {
            this.initialDate = quickFilter.initialDate
            this.finalDate = quickFilter.finalDate
            this.cardBrand = quickFilter.cardBrand
            this.listBrandSeles = quickFilter.listBrandSales
            this.paymentType = quickFilter.paymentType
            this.terminal = quickFilter.terminal
            this.status = quickFilter.status
            this.cardNumber = quickFilter.cardNumber
            this.nsu = quickFilter.nsu
            this.tid = quickFilter.tid
            this.authorizationCode = quickFilter.authorizationCode
            this.saleGrossAmount = quickFilter.saleGrossAmount
            this.grossAmount = quickFilter.grossAmount
            this.transactionTypeCode = quickFilter.transactionTypeCode
            this.merchantId = quickFilter.merchantId
            this.roNumber = quickFilter.roNumber
            this.operationNumber = quickFilter.operationNumber
            this.sku = quickFilter.sku
            this.expiredDate = quickFilter.expiredDate
            this.maximumInstallment = quickFilter.maximumInstallment
            this.softDescriptor = quickFilter.softDescriptor
            this.finalRecurrentExpiration = quickFilter.finalRecurrentExpiration
            this.frequency = quickFilter.frequency
            this.quantity = quickFilter.quantity
            this.identificationNumber = quickFilter.identificationNumber
            this.statusType = quickFilter.statusType
        }

        fun build() = QuickFilter(this)
    }
}