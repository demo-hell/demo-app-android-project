package br.com.mobicare.cielo.pagamentoLink.domains

import br.com.mobicare.cielo.superlink.analytics.PaymentLinkGA4

enum class TypeSaleEnum(val screenPath: String) {
    SEND_PRODUCT(PaymentLinkGA4.SCREEN_VIEW_PAYMENT_LINK_SEND_PRODUCT),
    CHARGE_AMOUNT(PaymentLinkGA4.SCREEN_VIEW_PAYMENT_LINK_CHARGE_AMOUNT),
    RECURRENT_SALE(PaymentLinkGA4.SCREEN_VIEW_PAYMENT_LINK_RECURRENT)
}

enum class TypeSalePeriodicEnum(val type: String) {
    RECURRENT_MONTHLY("Mensal"),
    RECURRENT_BIMONTHLY("Bimestral"),
    RECURRENT_QUARTELY("Trimestral"),
    RECURRENT_SEMIANNUAL("Semestral"),
    RECURRENT_ANNUAL("Anual")
}