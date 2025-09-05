package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class QRCodeChargeResponse(
    val expirationDate: String?,
    val key: String?,
    val merchantDocument: String?,
    val merchantName: String?,
    val merchantNumber: String?,
    val message: String?,
    val nsuPix: String?,
    val originalAmount: Double?,
    val payloadJws: String?,
    val pixId: String?,
    val qrCode: String?,
    val qrCodeString: String?,
    val txId: String?
) : Parcelable