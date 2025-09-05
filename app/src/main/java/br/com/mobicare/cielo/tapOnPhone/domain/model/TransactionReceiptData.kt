package br.com.mobicare.cielo.tapOnPhone.domain.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionReceiptData(
    val date: String? = null,
    val hour: String?= null,
    val doc: String? = null,
    val cardNumber: String? = null,
    val brand: String? = null,
    val transactionType: String? = null,
    val applicationId: String? = null,
    val authorizationCode: String? = null,
    val value: String? = null,
    val transactionDesc: String? = null,
    val installments: String? = null,
    val interest: String? = null,
    val receiptInfo: ReceiptInfo? = null
) : Parcelable

@Parcelize
data class ReceiptInfo(
    @SerializedName("MERCHANT_NAME")
    val merchantName: String? = null,
    @SerializedName("MERCHANT_ADDRESS")
    val merchantAddress: String? = null,
    @SerializedName("MERCHANT_CITY")
    val merchantCity: String? = null,
    @SerializedName("MERCHANT_STATE")
    val merchantState: String? = null,
    @SerializedName("MERCHANT_CODE")
    val merchantCode: String? = null,
    @SerializedName("DATE")
    val date: String? = null,
    @SerializedName("CARD_NUMBER")
    val cardNumber: String? = null,
    @SerializedName("BRAND")
    val brand: String? = null,
    @SerializedName("TRANSACTION_TYPE")
    val transactionType: String? = null,
    @SerializedName("AUTHORIZATION_CODE")
    val authorizationCode: String? = null,
    @SerializedName("VALUE")
    val value: String? = null,
    @SerializedName("INSTALLMENTS")
    val installments: String? = null,
    @SerializedName("PAYMENTDATETIME")
    val paymentDate: String? = null
) : Parcelable

@Parcelize
data class DynamicBankData(
    val issuerScriptResult: String? = null,
    val paymentId: String? = null,
    val receiptInfo: String? = null
) : Parcelable