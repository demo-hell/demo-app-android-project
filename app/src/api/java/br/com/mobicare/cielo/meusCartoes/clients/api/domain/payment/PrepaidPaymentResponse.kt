package br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class PrepaidPaymentResponse(
        @SerializedName("amount")
        val amount: BigDecimal,
        @SerializedName("applicationDate")
        val applicationDate: String,
        @SerializedName("beneficiaryName")
        val beneficiaryName: String,
        @SerializedName("code")
        val code: String,
        @SerializedName("discount")
        val discount: BigDecimal,
        @SerializedName("id")
        val id: String,
        @SerializedName("paymentType")
        val paymentType: String,
        @SerializedName("penalty")
        val penalty: BigDecimal,
        @SerializedName("requestDate")
        val requestDate: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("totalAmount")
        val totalAmount: BigDecimal
) : Parcelable