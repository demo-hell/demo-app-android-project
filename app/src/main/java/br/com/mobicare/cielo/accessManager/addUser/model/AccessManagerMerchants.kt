package br.com.mobicare.cielo.accessManager.addUser.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AccessManagerMerchants(
    val items: Array<Item>? = null,
    val summary: Summary? = null
) : Parcelable

@Parcelize
data class Item(
    val ec: String,
    val cnpj: String,
    val fantasia: String
) : Parcelable


@Parcelize
data class Summary(
    val totalQuantity: Long,
    val totalAmount: Long
) : Parcelable