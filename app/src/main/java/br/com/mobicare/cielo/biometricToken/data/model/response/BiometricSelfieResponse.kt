package br.com.mobicare.cielo.biometricToken.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class BiometricSelfieResponse(
    val token: String?,
    val expiresIn: Int?
) : Parcelable