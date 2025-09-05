package br.com.mobicare.cielo.contactCielo.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class SegmentCodeResponse(
    val segmentCode: String? = null
) : Parcelable
