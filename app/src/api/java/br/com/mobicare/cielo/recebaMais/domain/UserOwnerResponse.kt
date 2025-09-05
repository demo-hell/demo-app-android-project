package br.com.mobicare.cielo.recebaMais.domain


import com.google.gson.annotations.SerializedName

data class UserOwnerResponse(
        @SerializedName("addresses")
    val addresses: List<OwnerAddress>,
        @SerializedName("category")
    val category: String,
        @SerializedName("categoryCode")
    val categoryCode: String,
        @SerializedName("cnpj")
    val cnpj: String,
        @SerializedName("companyName")
    val companyName: String,
        @SerializedName("contacts")
    val ownerContacts: List<OwnerContact>,
        @SerializedName("debitPaymentBlock")
    val debitPaymentBlock: Boolean,
        @SerializedName("ecommerce")
    val ecommerce: Boolean,
        @SerializedName("lastSaleDate")
    val lastSaleDate: String,
        @SerializedName("mcc")
    val mcc: Int,
        @SerializedName("mccDescription")
    val mccDescription: String,
        @SerializedName("migrated")
    val migrated: Boolean,
        @SerializedName("mobile")
    val mobile: Boolean,
        @SerializedName("naturezaJuridicaCode")
    val naturezaJuridicaCode: String,
        @SerializedName("number")
    val number: Int,
        @SerializedName("openingDate")
    val openingDate: String,
        @SerializedName("owners")
    val userOwners: List<UserOwner>,
        @SerializedName("saleCustomer")
    val saleCustomer: Boolean,
        @SerializedName("segmentCode")
    val segmentCode: String,
        @SerializedName("status")
    val status: String,
        @SerializedName("tradingName")
    val tradingName: String,
        @SerializedName("blocks")
        val blocks: List<Block> = listOf()
)