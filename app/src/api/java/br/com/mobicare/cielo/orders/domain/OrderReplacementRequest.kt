package br.com.mobicare.cielo.orders.domain

import br.com.mobicare.cielo.meuCadastroNovo.domain.Phone

data class OrderReplacementRequest(
        val address: AddressOrderRequest? = null,
        val phones: List<Phone>? = null,
        val contactName: String? = null,
        val openingHourCode: String? = null,
        val logicalNumber: String? = null,
        val logicalNumberDigit: String? = null,
        val version: String? = null,
        val technologyType: String? = null,
        val serialNumber: String? = null,
        val orderId: String? = null)