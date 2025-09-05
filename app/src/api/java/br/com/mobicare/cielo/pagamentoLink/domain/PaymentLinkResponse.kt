package br.com.mobicare.cielo.pagamentoLink.domain

data class PaymentLinkResponse(
        val pagination: PaginationPaymentLink,
        val items : List<PaymentLink>
)