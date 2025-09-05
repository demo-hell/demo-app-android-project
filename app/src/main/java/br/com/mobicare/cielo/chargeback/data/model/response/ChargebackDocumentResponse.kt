package br.com.mobicare.cielo.chargeback.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ChargebackDocumentResponse(
    val code: Int?,
    val message: String?,
    val fileName: String?,
    val inclusionDate: String?,
    val file: String?
) : Parcelable