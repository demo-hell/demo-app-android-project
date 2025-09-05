package br.com.mobicare.cielo.tapOnPhone.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class TapOnPhoneEligibilityResponse(
    val impersonateRequired: Boolean?,
    val merchant: String?,
    val status: String?
) : Parcelable