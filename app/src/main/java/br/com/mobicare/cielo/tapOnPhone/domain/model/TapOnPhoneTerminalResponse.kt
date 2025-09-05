package br.com.mobicare.cielo.tapOnPhone.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class TapOnPhoneTerminalResponse(
    val changeToken: Boolean?,
    val deviceId: String,
    val registerDeviceRequired: Boolean?,
    val status: String?,
    val token: String?
) : Parcelable