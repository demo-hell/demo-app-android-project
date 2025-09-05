package br.com.mobicare.cielo.meuCadastroNovo.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentAccountsDomicile(
    val items: List<Item>?= mutableListOf(),
    val pagination: Pagination?= Pagination()
) : Parcelable

@Parcelize
data class Item(
        val account: String?="",
        val accountType: String?="",
        val agency: String?="",
        val bankCode: String?="",
        val bankName: String?="",
        val brands: List<PaymentAccountsDomicileBrand>?,
        val brandsErro: List<String>?,
        val code: Int?=0,
        val digitAccount: String?="",
        val digitAgency: String?="",
        val messageReason: String?="",
        val protocol: String?="",
        val requestDate: String?="",
        val status: String?=""
) : Parcelable

@Parcelize
data class PaymentAccountsDomicileBrand(
    val codeBrand: Int?=0,
    val nameBrand: String? = null,
    val messageReason: List<String>?= mutableListOf(),
    val status: String?="",
    val statusCode: Int? = 0
) : Parcelable

@Parcelize
data class Pagination(
    val firstPage: Boolean?=true,
    val lastPage: Boolean?=true,
    val numPages: Int?=0,
    val pageNumber: Int?=0,
    val pageSize: Int?=0,
    val totalElements: Int?=0
) : Parcelable