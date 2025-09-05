package br.com.mobicare.cielo.posVirtual.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.pix.constants.EMPTY
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PosVirtualBrandsResponse(
    val banks: List<BankResponse>? = null,
    val name: String? = null
) : Parcelable

@Keep
@Parcelize
data class BankResponse(
    val accountId: String? = null,
    val agencyDigit: String? = null,
    val name: String? = null,
    val accountDigit: String? = null,
    val accountNumber: String? = null,
    val agency: String? = null,
    val brands: List<BrandResponse>? = null,
    val code: String? = null,
    val digitalAccount: Boolean? = null,
    val imgSource: String? = null,
    val savingsAccount: Boolean? = null
) : Parcelable

@Keep
@Parcelize
data class BrandResponse(
    val code: Int? = null,
    val imgSource: String? = null,
    val name: String? = null,
    val products: List<ProductResponse>? = null
) : Parcelable

@Keep
@Parcelize
data class ProductResponse(
    val conditions: List<ConditionResponse>? = null,
    val name: String? = null,
    val prazoFlexivel: Boolean? = false,
    val pixType: String? = EMPTY,
    val productCode: Int? = ZERO
) : Parcelable

@Keep
@Parcelize
data class ConditionResponse(
    val anticipationAllowed: Boolean? = null,
    val flexibleTerm: Boolean? = false,
    val flexibleTermPayment: FlexibleTermPaymentResponse? = null,
    val flexibleTermPaymentFactor: Double? = null,
    val flexibleTermPaymentMDR: Double? = null,
    val maximumInstallments: Int? = null,
    val mdr: Double? = null,
    val minimumInstallments: Int? = null,
    val minimumMDR: Boolean? = null,
    val minimumMDRAmmount: Double? = null,
    val settlementTerm: Int? = null,
    val mdrContracted: Double? = null,
    val rateContractedRR: Double? = null,
    val contractedMdrCommissionRate: Double? = null
) : Parcelable

@Keep
@Parcelize
data class FlexibleTermPaymentResponse(
    val contractedPeriod: String? = null,
    val factor: Double? = null,
    val frequency: List<Int>? = null,
    val mdr: Double? = null
) : Parcelable