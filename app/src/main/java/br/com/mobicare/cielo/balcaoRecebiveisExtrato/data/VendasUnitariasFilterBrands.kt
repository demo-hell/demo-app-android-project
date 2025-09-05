package br.com.mobicare.cielo.balcaoRecebiveisExtrato.data
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VendasUnitariasFilterBrands(
    val cardBrands: List<CardBrands>? = null
): Parcelable

@Parcelize
data class CardBrands(
    val totalQuantity:Int?=0,
    val totalAmount:Double?=0.0,
    val name:String? = null,
    val value:String? = null,
    val totalNetAmount:Double?=0.0,
    val totalDiscountAmount:Double?=0.0
): Parcelable

@Parcelize
data class TransactionTypes(
    val totalQuantity:Int?=0,
    val totalAmount:Int?=0,
    val name:String?=null,
    val value:String?=null,
    val totalNetAmount:Int?=0,
    val totalDiscountAmount:Int?=0
): Parcelable

@Parcelize
data class Acquirers(
    val totalQuantity:Int?=0,
    val totalAmount:Int?=0,
    val name:String? = null,
    val value:String? = null,
    val totalNetAmount:Int?=0,
    val totalDiscountAmount:Int?=0
): Parcelable