package br.com.mobicare.cielo.posVirtual.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PosVirtualCreateQRCodeResponse(
    val id: String? = null,
    val creationDate: String? = null,
    val nsuPix: String? = null,
    val qrCodeId: String? = null,
    val qrCodeData: String? = null,
    val qrCodeString: String? = null,
    val qrCodeBase64: String? = null,
    val bankCode: String? = null,
    val bankName: String? = null,
    val merchantName: String? = null,
    val merchantNumber: String? = null,
    val merchantDocument: String? = null,
    val amount: Double? = null
) : Parcelable
