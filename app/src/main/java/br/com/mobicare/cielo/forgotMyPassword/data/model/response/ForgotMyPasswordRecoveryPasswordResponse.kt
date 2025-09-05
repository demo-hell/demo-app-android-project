package br.com.mobicare.cielo.forgotMyPassword.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ForgotMyPasswordRecoveryPasswordResponse(
    val tokenExpirationInMinutes: Int? = null,
    val email: String? = null,
    val nextStep: String? = null,
    val faceIdPartner: String? = null
) : Parcelable
