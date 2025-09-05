package br.com.mobicare.cielo.login.firstAccess.data.model.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirstAccessRegistrationRequest(
        var cpf: String?,
        var email: String?,
        var password: String?,
        var passwordConfirmation: String?,
        var pid: FirstAccessPayIdRequest
) : Parcelable