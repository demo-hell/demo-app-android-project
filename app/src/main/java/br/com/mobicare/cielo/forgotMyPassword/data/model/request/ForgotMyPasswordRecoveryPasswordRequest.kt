package br.com.mobicare.cielo.forgotMyPassword.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ForgotMyPasswordRecoveryPasswordRequest(
    var login: ForgotMyPasswordLogin? = null,
    var pid: ForgotMyPasswordPid? = null
) : Parcelable
