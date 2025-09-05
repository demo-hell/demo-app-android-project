package br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Parcelize
data class PrepaidPaymentRequest(
        @SerializedName("amount")
        var amount: BigDecimal,
        @SerializedName("beneficiaryName")
        var beneficiaryName: String,
        @SerializedName("code")
        var code: String,
        @SerializedName("discount")
        var discount: BigDecimal,
        @SerializedName("isBarcode")
        var isBarcode: Boolean,
        @SerializedName("paymentType")
        var paymentType: String,
        @SerializedName("penalty")
        var penalty: BigDecimal
) : Parcelable