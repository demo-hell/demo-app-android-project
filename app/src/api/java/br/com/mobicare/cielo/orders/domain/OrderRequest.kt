package br.com.mobicare.cielo.orders.domain

import br.com.mobicare.cielo.meuCadastroNovo.domain.Phone


data class OrderRequest(
        val address: AddressOrderRequest,
        val phones: List<Phone>,
        val contactName: String,
        val openingHourCode: String,
        val technologyType: String,
        val quantity: Int)





