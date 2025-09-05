package br.com.mobicare.cielo.meuCadastroDomicilio.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DomicilioBankVo(
        var accountDigit: String?,
        var accountNumber: String,
        var agency: String,
        var agencyDigit: String?,
        var brands: List<DomicilioFlagVo>?,
        var code: String,
        var imgSource: String,
        var name: String?,
        var savingsAccount: Boolean,
        var checked: Boolean) : Parcelable