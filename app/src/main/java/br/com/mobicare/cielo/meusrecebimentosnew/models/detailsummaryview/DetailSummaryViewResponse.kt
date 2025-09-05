package br.com.mobicare.cielo.meusrecebimentosnew.models.detailsummaryview

import android.os.Parcelable
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.Bank
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailSummaryViewResponse(
    val summary: ReceivableSummary? = null,
    val pagination: Pagination? = null,
    val items: List<Receivable>? = null
) : Parcelable

@Parcelize
data class ReceivableSummary(
   val totalQuantity: Int? = null,
   val totalAmount: Double? = null,
   val totalNetAmount: Double? = null,
   val totalAverageAmount: Double? = null
) : Parcelable

@Parcelize
data class Pagination (
        val pageNumber: Long,
        val pageSize: Long,
        val totalElements: Long,
        val firstPage: Boolean,
        val lastPage: Boolean,
        val numPages: Long
) : Parcelable

@Parcelize
data class Receivable(
        val id: String? = null,
        val transactionPixId: String?,
        val merchantId: String? = null,
        val paymentNode: Long? = null,
        val data: String? = null,
        val paymentDate: String? = null,
        val saleDate: String? = null,
        val truncatedCardNumber: String? = null,
        val transactionId: String? = null,
        val authorizationCode: String? = null,
        val authorizationDate: String? = null,
        val operationDate: String? = null,
        val operationNumber: String? = null,
        val roNumber: String? = null,
        val type:String? = null,
        val nsu: String? = null,
        val status: String? = null,
        val statusCode: Int? = null,
        val cardBrand: String? = null,
        val cardBrandCode: Int? = null,
        val netAmount: Double? = null,
        val grossAmount: Double? = null,
        val paymentType: String? = null,
        val paymentTypeCode: Int? = null,
        val productTypeCode: Long? = null,
        val productType: String? = null,
        val transactionTypeCode: Int? = null,
        val transactionType: String? = null,
        val installments: Int? = null,
        val installment: Int? = null,
        val terminal: String? = null,
        val saleCode: String? = null,
        val anticipationCode: String? = null,
        val initialPaymentDate: String? = null,
        val anticipationDays: Int? = null,
        val quantity: Int? = null,
        val channelCode: Long? = null,
        val channel: String? = null,
        val orderNumber: String? = null,
        val mdrFee: Double? = null,
        val mdrFeeAmount: Double? = null,
        val cieloPromo: Boolean? = null,
        val promoAmount: Double? = null,
        val getFast: Boolean? = null,
        val rejectedSale: Boolean? = null,
        val availableDate: String? = null,
        val shipmentFee: Double? = null,
        val withdrawAmount: Double? = null,
        val downPaymentAmount: Double? = null,
        val description: String? = null,
        val invoiceNumber: String? = null,
        val paymentDescription: String? = null,
        val date : String?,
        val pendingAmount : Double?,
        val chargedAmount : Double?,
        val totalDebtAmount : Double?,
        val anticipationFee : Int,
        val entryModeCode : Int,
        val entryMode : String,
        val bank : Bank?,
        val initialDate : String?,
        val finalDate : String?,
        val installmentDescription : String?,
        val codeFarol: Int?,
        val descriptionFarol: String?,
        val dateTransferAccountPix: String?,
        val typeAccountPix: Int?
) : Parcelable
