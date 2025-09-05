package br.com.mobicare.cielo.login.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class UserStatusPrepago(
    val description: String? = null,
    val documentsAccepted: String? = null,
    val allowDocumentUpload: String? = null,
    val type: String? = null
) : Parcelable



