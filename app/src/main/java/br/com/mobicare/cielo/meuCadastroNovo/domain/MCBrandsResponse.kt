package br.com.mobicare.cielo.meuCadastroNovo.domain

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.pix.constants.EMPTY
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Solution(
        val banks: List<Bank>,
        val name: String?
) : Parcelable

@Keep
@Parcelize
data class Bank(
        var accountId: String?,
        var agencyDigit: String?,
        var name: String?,
        val accountDigit: String?,
        val accountNumber: String?,
        val agency: String?,
        val brands: List<Brand>?,
        val code: String,
        val digitalAccount: Boolean,
        val imgSource: String,
        val savingsAccount: Boolean
) : Parcelable

@Keep
@Parcelize
data class Brand(
        val code: Int,
        val imgSource: String,
        val name: String,
        val products: List<Product>
) : Parcelable

@Keep
@Parcelize
data class Product(
        val conditions: List<Condition>,
        val name: String,
        val prazoFlexivel: Boolean = false,
        val pixType: String? = EMPTY,
        val productCode: Int = ZERO,
        val pixRateTax: Double?
) : Parcelable

@Keep
@Parcelize
data class Condition(
        val anticipationAllowed: Boolean?,
        val flexibleTerm: Boolean = false,
        val flexibleTermPayment: FlexibleTermPayment?,
        val flexibleTermPaymentFactor: Double?,
        val flexibleTermPaymentMDR: Double?,
        val maximumInstallments: Int?,
        val mdr: Double?,
        val minimumInstallments: Int?,
        val minimumMDR: Boolean,
        val minimumMDRAmmount: Double?,
        val settlementTerm: Int?,
        val mdrContracted: Double?,
        val rateContractedRR: Double?,
        val contractedMdrCommissionRate: Double?
) : Parcelable

@Keep
@Parcelize
data class FlexibleTermPayment(
        val contractedPeriod: String?,
        val factor: Double?,
        val frequency: List<Int>?,
        val mdr: Double?
) : Parcelable