package br.com.mobicare.cielo.login.firstAccess.data.model.request

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirstAccessPayIdRequest (
    var merchantId: String?
) : Parcelable