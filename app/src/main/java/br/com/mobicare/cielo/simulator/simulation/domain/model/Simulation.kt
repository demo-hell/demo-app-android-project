package br.com.mobicare.cielo.simulator.simulation.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Installment(
    val installmentNumber: Int? = null,
    val mdrTax: Double? = null,
    val finalMdrTax: Double? = null,
    val fastRepayTax: Double? = null,
    val totalValue: Double? = null,
    val saleAmount: Double? = null,
    val customerInstallmentValue: Double? = null,
    val shopkeeperInstallmentValue: Double? = null,
    val receivableValue: Double? = null
): Parcelable

@Parcelize
data class Simulation(
    val cardBrandCode: Int? = null,
    val productCode: Int? = null,
    val customerTransferIndicator: String? = null,
    val installments: List<Installment>? = null,
    val receivableRemainingDays: String? = null,
    val fastRepayPeriodicity: String? = null,
    val saleAmount: Double? = null,
    val flexibleTerm: Boolean? = null
): Parcelable