package br.com.mobicare.cielo.recebaMais.domain


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Simulation(
        @SerializedName("requestedAmount")
        val amount: Double,
        @SerializedName("installmentAmount")
        val installmentAmount: Double,
        @SerializedName("installments")
        val installments: Int,
        @SerializedName("iof")
        val iof: Double,
        @SerializedName("iofRate")
        val iofRate: Double,
        @SerializedName("monthlyInterestRate")
        val monthlyInterestRate: Double,
        @SerializedName("debitAmount")
        val totalAmount: Double,
        @SerializedName("annualEffectiveCostRate") val annualEffectiveCostRate: Double,
        @SerializedName("annualInterestRate") val annualInterestRate: Double,
        @SerializedName("monthlyEffectiveCostRate") val monthlyEffectiveCostRate: Double,
        @SerializedName("registrationFee") val registrationFee: Double,
        @SerializedName("avaiableAmount") val avaiableAmount: Double,
        @SerializedName("insuranceAmount") val insuranceAmount: Double,
        @SerializedName("boletoAmount") val boletoAmount: Double) : Parcelable