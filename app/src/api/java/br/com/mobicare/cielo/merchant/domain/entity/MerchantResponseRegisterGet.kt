package br.com.mobicare.cielo.merchant.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class MerchantResponseRegisterGet(
    val adquirers: List<Adquirer>?,
    val arrangements: List<Arrangement>?,
    val channel: String?,
    val cieloCustomer: Boolean?,
    val document: String?,
    val email: String?,
    val id: Int?,
    val name: String?,
    val optin: Boolean?,
    val optinDate: String?,
    val optoutChannel: String?,
    val optoutDate: String?,
    val optoutUser: String?,
    val phone: String?,
    val user: String?
): Parcelable

@Parcelize
data class Arrangement(
    val adcs: Int?,
    val id: Int?,
    val name: String?
): Parcelable

@Parcelize
data class Adquirer(
    val document: String?,
    val id: Int?,
    val name: String?
): Parcelable


data class BankMock(val name:String?, val agencia:String?, val conta:String?, val image: String?, val status:String?="Removido")

