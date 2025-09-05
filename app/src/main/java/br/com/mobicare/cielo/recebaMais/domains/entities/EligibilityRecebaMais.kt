package br.com.mobicare.cielo.recebaMais.domains.entities

data class EligibilityRecebaMaisResponse(
        val products: List<Product>
)


data class Product(
        val code: Int,
        val name: String
)