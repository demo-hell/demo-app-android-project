package br.com.mobicare.cielo.coil.domain

import android.os.Parcelable
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ZERO
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MerchantBuySupply(
        val supplyCode: String,
        val quantity: Int,
        val status: String,
        val limitQuantity: Int,
        val code: Int,
        val message: String
) : Parcelable {
    var title: String = EMPTY
    var description: String = EMPTY
}

@Parcelize
data class SupplyDTO(
        val allowedQuantity: Boolean,
        val code: String,
        val description: String,
        val type: String
) : Parcelable {
    var quantidade: Int = ZERO
}