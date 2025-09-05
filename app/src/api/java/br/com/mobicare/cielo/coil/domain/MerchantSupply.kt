package br.com.mobicare.cielo.coil.domain

data class MerchantSupply(
        val code: String,
        val description: String,
        val allowedQuantity: Boolean,
        val type: String)