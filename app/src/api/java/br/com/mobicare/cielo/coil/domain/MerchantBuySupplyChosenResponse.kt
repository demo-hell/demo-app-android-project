package br.com.mobicare.cielo.coil.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MerchantBuySupplyChosenResponse  (
        val supplies : ArrayList<MerchantBuySupply>
) : Parcelable