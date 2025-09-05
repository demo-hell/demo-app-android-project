package br.com.mobicare.cielo.pix.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ResponsePixDataQuery(
    val beginTime: String?,
    val status: String
): Parcelable