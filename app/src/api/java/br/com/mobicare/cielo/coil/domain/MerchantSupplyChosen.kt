package br.com.mobicare.cielo.coil.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MerchantSupplyChosen (
        val supplyCode: String,
        val quantity: Int,
        val deliveryAddressZipCode: String) : Parcelable