package br.com.mobicare.cielo.pagamentoLink.presentation.ui.linkPaymentCreated

import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLink
import br.com.mobicare.cielo.pagamentoLink.domains.PaymentLinkDTO
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics

class LinkPaymentCreatedPresenter : LinkPaymentCreatedContract.Presenter {

    private var linkType: LinkType? = null

    override var paymentLink: PaymentLink? = null
    override var paymentLinkDto: PaymentLinkDTO? = null

    override val isLinkCreate get() = linkType == LinkType.CREATE

    override val linkTypeTag get() = linkType?.let {
        if (isLinkCreate)
            SuperLinkAnalytics.GENERATED_LINK
        else
            SuperLinkAnalytics.LINK_FOR_PAYMENT
    } ?: EMPTY

    override fun setLinkType(value: String?) {
        linkType = LinkType.find(value)
    }

    enum class LinkType(val value: String) {
        ACTIVE("1"), CREATE("2");

        companion object {
            fun find(value: String?) = values().firstOrNull { it.value == value }
        }
    }

}