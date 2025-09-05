package br.com.mobicare.cielo.pagamentoLink.presentation.ui.linkPaymentCreated

import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLink
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO

interface LinkPaymentCreatedContract {

    interface Presenter {
        var paymentLink: PaymentLink?
        var paymentLinkDto: PaymentLinkDTO?
        val isLinkCreate: Boolean
        val linkTypeTag: String
        fun setLinkType(value: String?)
    }

}