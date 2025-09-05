package br.com.mobicare.cielo.pixMVVM.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PixAuthorizationStatusResponse(
    val beginTime: String?,
    val status: String?
): Parcelable