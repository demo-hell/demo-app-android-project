package br.com.mobicare.cielo.recebaMais.domain


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal


@Parcelize
data class Offer(
        @SerializedName("customerId")
        val customerId: String?,
        @SerializedName("customerType")
        val customerType: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("endDatePaymentFirstInstallment")
        val endDatePaymentFirstInstallment: String?,
        @SerializedName("id")
        val id: String?,
        @SerializedName("installmentAmount")
        val installmentAmount: BigDecimal?,
        @SerializedName("installments")
        val installments: Int?,
        @SerializedName("loanLimit")
        val loanLimit: BigDecimal?,
        @SerializedName("monthlyInterestRate")
        val monthlyInterestRate: Double?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("partner")
        val partner: Partner?,
        @SerializedName("startDatePaymentFirstInstallment")
        val startDatePaymentFirstInstallment: String?,
        @SerializedName("steps")
        val steps: List<String>?
) : Parcelable