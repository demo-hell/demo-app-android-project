package br.com.mobicare.cielo.coil.domain

data class MerchantAddress(
        val addressTypes: ArrayList<String?>? = arrayListOf(),
        val streetAddress: String? = "",
        val neighborhood: String? = "",
        val number: String? = "",
        val city: String? = "",
        val state: String? = "",
        val zipCode: String? = ""
)