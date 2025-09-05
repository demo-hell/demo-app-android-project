package br.com.mobicare.cielo.mySales.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class CanceledSale(
        val merchantId: String? = null,
        val paymentNode: Int? = null,
        val date: String? = null,
        val saleDate: String? = null,
        val saleAmount: Double? = null,
        val cardBrandCode: Int? = null,
        val cardBrand: String? = null,
        val paymentTypeCode: Int? = null,
        val paymentType: String? = null,
        val refundAmount: Double? = null,
        val truncatedCardNumber: String? = null,
        val authorizationCode: String? = null,
        val nsu: String? = null,
        val transactionId: String? = null,
        val channelCode: Int? = null,
        val channel: String? = null,
        val situation: String? = null,
        val status: String? = null,
        val statusCode: Int? = null,
        val lotId: String? = null
) : Parcelable