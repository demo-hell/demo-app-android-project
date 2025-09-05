package br.com.mobicare.cielo.centralDeAjuda.domains.entities

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class CentralAjudaDefaultObj (
    var title: String? = null,
    var description: String? = null,
    var value: String? = null
): Parcelable
