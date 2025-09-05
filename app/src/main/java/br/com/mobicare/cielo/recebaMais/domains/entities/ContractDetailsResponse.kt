package br.com.mobicare.cielo.recebaMais.domains.entities

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Keep
@Parcelize
data class ContractDetailsResponse(
        val contracts: List<ContractDetails>?
) : Parcelable

@Keep
@Parcelize
data class ContractDetails(
        val annualEffectiveCostRate: Double,
        val annualInterestRate: Double,
        val bankAccount: BankAccountDetails,
        val contractCode: String,
        val contractDate: String,
        val customerId: String,
        val installmentAmount: Double,
        var installments: List<InstallmentDetails>,
        val interestRate: Double,
        val iofRate: Double,
        val mechantId: String,
        val monthlyEffectiveCostRate: Double,
        val partner: PartnerDetails,
        val paymentFirstInstallmentDate: String,
        val quantity: Int,
        val registrationFee: Double,
        val status: String,
        val statusCode: Int,
        val valueContract: Double
) : Parcelable

@Keep
@Parcelize
data class BankAccountDetails(
        val account: String,
        val accountDigit: String?,
        val agency: String,
        val agencyDigit: String?,
        val code: Int,
        val name: String
) : Parcelable

@Keep
@Parcelize
data class InstallmentDetails(
        val amountOwed: Double,
        val dueDate: String,
        val installmentAmount: Double,
        val installmentNumber: Int,
        val lastPayment: String?,
        val status: String,
        val statusCode: Int
) : Parcelable

@Keep
@Parcelize
data class PartnerDetails(
        val code: String,
        val merchantId: String?,
        val name: String,
        val score: Double?
) : Parcelable