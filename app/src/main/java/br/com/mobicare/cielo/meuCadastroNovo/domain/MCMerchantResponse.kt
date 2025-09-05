package br.com.mobicare.cielo.meuCadastroNovo.domain

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MCMerchantResponse(
        val addresses: List<Address>,
        val category: String?,
        val categoryCode: String,
        val cnpj: String?,
        val companyName: String,
        val contacts: List<Contact>,
        val debitPaymentBlock: Boolean,
        val debitPaymentBlockReason: String?,
        val lastSaleDate: String?,
        val number: Long,
        val openingDate: String?,
        val owners: List<Owner>?,
        val segmentCode: String,
        val status: String,
        val tradingName: String?,
        val updateRequiredOwner: Boolean?,
        val updateRequiredCommercial: Boolean?,
        val updateRequiredMerchant: Boolean?,
        val updateRequiredChargeback: Boolean?,
        val blocks: List<Block>?
): Parcelable


@Parcelize
data class Contact(
        val id: Int?,
        var email: String?,
        var name: String,
        var phones: List<PhoneContato>,
        val types: List<String>
): Parcelable

@Parcelize
data class Address(
        val id: String?,
        val addressTypes: List<String>,
        val city: String?,
        val neighborhood: String?,
        val number: String?,
        val state: String?,
        val streetAddress: String?,
        @SerializedName("streetAddress2")
        val complementAddress: String?,
        val types: List<String?>,
        val zipCode: String?
): Parcelable

@Parcelize
data class Owner(
        val birthDate: String?,
        val cpf: String,
        val name: String,
        var phones: List<Phone>,
        var email: String? = null
): Parcelable

@Parcelize
data class Phone(
        val areaCode: String? = null,
        val number: String? = null,
        val type: String? = null
): Parcelable

@Parcelize
data class PhoneContato(
        val id: Int?,
        val areaCode: String?,
        val number: String,
        val type: String
): Parcelable

@Parcelize
data class Block(
        @SerializedName("codeType")
        val codeType: Int? = null,
        @SerializedName("nameType")
        val nameType: String? = null,
        @SerializedName("codeReason")
        val codeReason: Int? = null,
        @SerializedName("descriptionReason")
        val descriptionReason: String? = null,
        @SerializedName("dateBeginBlocked")
        val dateBeginBlocked: String? = null,
        @SerializedName("nameRequestorBlocked")
        val nameRequestorBlocked: String? = null

): Parcelable