package br.com.mobicare.cielo.recebaMais.domain


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Contract(
        @SerializedName("requestedAmount")
        val requestedAmount: Double,
        @SerializedName("annualEffectiveCostRate")
        val annualEffectiveCostRate: Double,
        @SerializedName("annualInterestRate")
        val annualInterestRate: Double,
        @SerializedName("id")
        val id: String,
        @SerializedName("installmentAmount")
        val installmentAmount: Double,
        @SerializedName("installments")
        val installments: Int,
        @SerializedName("iof")
        val iof: Double,
        @SerializedName("iofRate")
        val iofRate: Double,
        @SerializedName("monthlyEffectiveCostRate")
        val monthlyEffectiveCostRate: Double,
        @SerializedName("monthlyInterestRate")
        val monthlyInterestRate: Double,
        @SerializedName("pid")
        val pid: Pid,
        @SerializedName("partner")
        val partner: Partner,
        @SerializedName("registrationFee")
        val registrationFee: Double,
        @SerializedName("status")
        val status: String,
        @SerializedName("totalAmount")
        val totalAmount: Double,
        @SerializedName("paymentFirstInstallmentDate")
        val paymentFirstInstallmentDate: String,
        @SerializedName("contractDate")
        val contractDate: String

): Parcelable