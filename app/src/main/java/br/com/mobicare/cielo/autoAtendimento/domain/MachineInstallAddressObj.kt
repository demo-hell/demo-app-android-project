package br.com.mobicare.cielo.autoAtendimento.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MachineInstallAddressObj(
        var streetAddress: String,
        var numberAddress: String,
        val addressType: String,
        var zipcode: String,
        var referencePoint: String,
        var city: String,
        var neighborhood: String,
        var state: String) : Parcelable