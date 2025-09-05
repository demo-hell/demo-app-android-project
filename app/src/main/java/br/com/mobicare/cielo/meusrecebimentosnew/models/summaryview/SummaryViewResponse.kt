package br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview

import android.os.Parcelable
import br.com.mobicare.cielo.meuCadastroNovo.domain.Brand
import br.com.mobicare.cielo.meusrecebimentosnew.models.Link
import kotlinx.android.parcel.Parcelize


data class SummaryViewResponse(
   val summary: Summary,
   val pagination: Pagination,
   val items: List<Item>
)

data class Summary (
        val totalQuantity: Long,
        val totalAmount: Double?,
        val totalNetAmount: Double?
)

data class Pagination (
        val pageNumber: Long,
        val pageSize: Long,
        val totalElements: Long,
        val firstPage: Boolean,
        val lastPage: Boolean,
        val numPages: Long
)

@Parcelize
data class Item(
        val merchantId: String,
        val paymentNode: Int?,
        val paymentDate: String?,
        val saleDate: String?,
        val paymentScheduleDate: String?,
        val operationNumber: String?,
        val roNumber: String?,
        val status: String?,
        val cardBrand: String?,
        val cardBrandCode: Int?,
        val paymentType: String?,
        val paymentTypeCode: Int?,
        val productType: String?,
        val productTypeCode: Int?,
        val transactionTypeCode: Int?,
        val transactionType: String,
        val terminal: String?,
        val description: String?,
        val netAmount: Double?,
        val grossAmount: Double?,
        val mdrFeeAmount: Double?,
        val anticipationFee: Double?,
        val anticipationDiscountAmount: Double?,
        val quantity: Int?,
        val adjustmentDescription: String?,
        val initialDate: String?,
        val finalDate: String?,
        val links: List<Link>?,
        val code: Int?,
        val type: String?,
        val name:String?,
        val agency:String?,
        val account:String?,
        val accountDigit:String?,
        val bank : Bank?,
) : Parcelable

@Parcelize
data class Bank(
        var account: String?,
        var agencyDigit: String?,
        var name: String?,
        val accountDigit: String?,
        val accountNumber: String,
        val agency: String,
        val brands: List<Brand>?,
        val code: String,
        val digitalAccount: Boolean,
        val imgSource: String,
        val savingsAccount: Boolean
) : Parcelable