package br.com.mobicare.cielo.posVirtual.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.pix.constants.EMPTY
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Solutions(
    val solutions: List<Solution>? = null
) : Parcelable

@Keep
@Parcelize
data class Solution(
    val banks: List<Bank>? = null,
    val name: String? = null
) : Parcelable

@Keep
@Parcelize
data class Bank(
    val accountId: String? = null,
    val name: String,
    val agencyDigit: String? = null,
    val agencyNumber: String? = null,
    val agencyExt: String,
    val accountDigit: String? = null,
    val accountNumber: String? = null,
    val accountExt: String,
    val brands: List<Brand>? = null,
    val code: String? = null,
    val digitalAccount: Boolean? = null,
    val imgSource: String? = null,
    val savingsAccount: Boolean? = null
) : Parcelable

@Keep
@Parcelize
data class Brand(
    val code: Int? = null,
    val imgSource: String? = null,
    val name: String? = null,
    val products: List<Product>? = null
) : Parcelable

@Keep
@Parcelize
data class Product(
    val conditions: List<Condition>? = null,
    val name: String? = null,
    val flexibleTerm: Boolean? = false,
    val pixType: String? = EMPTY,
    val productCode: Int? = ZERO
) : Parcelable

@Keep
@Parcelize
data class Condition(
    val anticipationAllowed: Boolean? = null,
    val flexibleTerm: Boolean? = false,
    val flexibleTermPayment: FlexibleTermPayment? = null,
    val flexibleTermPaymentFactor: Double? = null,
    val flexibleTermPaymentMDR: Double? = null,
    val maximumInstallments: Int? = null,
    val mdr: Double? = null,
    val minimumInstallments: Int? = null,
    val minimumMDR: Boolean? = null,
    val minimumMDRAmount: Double? = null,
    val settlementTerm: Int? = null,
    val mdrContracted: Double? = null,
    val rateContractedRR: Double? = null,
    val contractedMdrCommissionRate: Double? = null
) : Parcelable

@Keep
@Parcelize
data class FlexibleTermPayment(
    val contractedPeriod: String? = null,
    val factor: Double? = null,
    val frequency: List<Int>? = null,
    val mdr: Double? = null
) : Parcelable