package br.com.mobicare.cielo.newLogin.domain

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PosVirtualWhiteListResponse(val eligible: Boolean = false) : Parcelable