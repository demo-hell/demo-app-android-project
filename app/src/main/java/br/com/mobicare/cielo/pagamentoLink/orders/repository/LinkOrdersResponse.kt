package br.com.mobicare.cielo.pagamentoLink.orders.repository

import br.com.mobicare.cielo.pagamentoLink.orders.model.Order

data class LinkOrdersResponse(
        val linkId: String,
        val items: List<Order>
)