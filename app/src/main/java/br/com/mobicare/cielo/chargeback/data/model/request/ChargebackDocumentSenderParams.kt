package br.com.mobicare.cielo.chargeback.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ChargebackDocumentSenderParams(
    val merchantId: Long,
    val documentId: Int
) : Parcelable
