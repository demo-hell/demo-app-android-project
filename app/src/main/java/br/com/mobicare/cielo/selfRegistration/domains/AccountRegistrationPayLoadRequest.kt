package br.com.mobicare.cielo.selfRegistration.domains

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AccountRegistrationPayLoadRequest(

        var login: String? = null,
        var fullName: String,
        var cpf: String?,
        var email: String,
        var password: String,
        var passwordConfirmation: String,
        @SerializedName("pid") var pid: PayIdRequest

) : Parcelable