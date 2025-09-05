package br.com.mobicare.cielo.simulator.simulation.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.simulator.simulation.domain.model.Installment
import br.com.mobicare.cielo.simulator.simulation.domain.model.Simulation
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class InstallmentResponse(
    val installmentNumber: Int? = null,
    val mdrTax: Double? = null,
    val finalMdrTax: Double? = null,
    val fastRepayTax: Double? = null,
    val totalValue: Double? = null,
    val saleAmount: Double? = null,
    val customerInstallmentValue: Double? = null,
    val shopkeeperInstallmentValue: Double? = null,
    val receivableValue: Double? = null
) : Parcelable

fun InstallmentResponse.toInstallment() = Installment(
    installmentNumber,
    mdrTax,
    finalMdrTax,
    fastRepayTax,
    totalValue,
    saleAmount,
    customerInstallmentValue,
    shopkeeperInstallmentValue,
    receivableValue
)

@Keep
@Parcelize
data class SimulationResponse(
    val cardBrandCode: Int? = null,
    val productCode: Int? = null,
    val customerTransferIndicator: String? = null,
    val installments: List<InstallmentResponse>? = null,
    val receivableRemainingDays: String? = null,
    val fastRepayPeriodicity: String? = null,
    val saleAmount: Double? = null,
    val flexibleTerm: Boolean? = null
) : Parcelable

fun SimulationResponse.toSimulation() = Simulation(
    cardBrandCode,
    productCode,
    customerTransferIndicator,
    installments?.map { it.toInstallment() },
    receivableRemainingDays,
    fastRepayPeriodicity,
    saleAmount,
    flexibleTerm
)