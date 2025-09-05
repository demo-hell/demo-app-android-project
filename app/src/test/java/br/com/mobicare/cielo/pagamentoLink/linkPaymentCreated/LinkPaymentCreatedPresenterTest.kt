package br.com.mobicare.cielo.pagamentoLink.linkPaymentCreated

import br.com.mobicare.cielo.pagamentoLink.presentation.ui.linkPaymentCreated.LinkPaymentCreatedPresenter
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations

class LinkPaymentCreatedPresenterTest {

    private lateinit var presenter: LinkPaymentCreatedPresenter

    private val linkTypeCreateValue = LinkPaymentCreatedPresenter.LinkType.CREATE.value
    private val linkTypeActiveValue = LinkPaymentCreatedPresenter.LinkType.ACTIVE.value

    private val generatedLinkTag = SuperLinkAnalytics.GENERATED_LINK
    private val linkForPaymentTag = SuperLinkAnalytics.LINK_FOR_PAYMENT

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = LinkPaymentCreatedPresenter()
    }

    @Test
    fun `it should set the create link type and configure correctly`() = presenter.run {
        setLinkType(linkTypeCreateValue)

        assertTrue(isLinkCreate)
        assertEquals(generatedLinkTag, linkTypeTag)
    }

    @Test
    fun `it should set the active link type and configure correctly`() = presenter.run {
        setLinkType(linkTypeActiveValue)

        assertFalse(isLinkCreate)
        assertEquals(linkForPaymentTag, linkTypeTag)
    }

    @Test
    fun `it should configure to default when the link type is not set`() = presenter.run {
        assertFalse(isLinkCreate)
        assertEquals(EMPTY, linkTypeTag)
    }

}