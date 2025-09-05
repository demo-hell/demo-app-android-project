package br.com.mobicare.cielo.component.impersonate.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ImpersonateRequest(
    val fingerprint: String
) : Parcelable
