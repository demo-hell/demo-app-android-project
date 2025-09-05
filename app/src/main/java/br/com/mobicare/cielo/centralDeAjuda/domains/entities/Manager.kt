package br.com.mobicare.cielo.centralDeAjuda.domains.entities

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class Manager (
    var name: String? = null,
    var email: String? = null,
    var phone: String? = null
): Parcelable {
    val emailFormatted get() = emailFormat.format(email)
    val phoneFormatted get() = phoneFormat.format(phone)

    private companion object {
        const val emailFormat = "E-mail: %s"
        const val phoneFormat = "Celular: %s"
    }
}