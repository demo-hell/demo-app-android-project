package br.com.mobicare.cielo.pixMVVM.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PixCreateNotifyInfringement(
    var idEndToEnd: String? = null,
    var message: String? = null,
    var merchantId: String? = null,
    var situationType: String? = null,
    var situationDescription: String? = null,
    var reasonType: String? = null,
    var amount: Double? = null,
    var date: String? = null,
    var institution: String? = null,
) : Parcelable