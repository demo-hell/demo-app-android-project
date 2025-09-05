package br.com.mobicare.cielo.centralDeAjuda.data.clients.domains

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class OmbudsmanRequest(
        var contactPerson: String? = null,
        val email: String? = null,
        val merchant: String? = null,
        var message: String? = null,
        val phone: String? = null,
        val previousContact: String = "S",
        var protocol: String? = null,
        var subject: String? = null
) : Parcelable