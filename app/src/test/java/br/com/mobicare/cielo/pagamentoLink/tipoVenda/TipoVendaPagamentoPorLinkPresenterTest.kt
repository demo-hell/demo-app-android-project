package br.com.mobicare.cielo.pagamentoLink.tipoVenda

import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.tipoVenda.TipoVendaPagamentoPorLinkContract
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.tipoVenda.TipoVendaPagamentoPorLinkPresenter
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class TipoVendaPagamentoPorLinkPresenterTest {

    @Mock
    lateinit var view: TipoVendaPagamentoPorLinkContract.View

    @Mock
    lateinit var featureTogglePreference: FeatureTogglePreference

    private lateinit var presenter: TipoVendaPagamentoPorLinkPresenter

    private val featureToggleValueCaptor = argumentCaptor<String>()
    private val showResultCaptor = argumentCaptor<Boolean>()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = TipoVendaPagamentoPorLinkPresenter(
            view,
            featureTogglePreference
        )
    }

    @Test
    fun `it should set true to onShowProductDeliveryOption param when getFeatureTogle result is true`() {
        // given
        doReturn(true)
            .whenever(featureTogglePreference)
            .getFeatureTogle(featureToggleValueCaptor.capture())

        // when
        presenter.verifyProductDeliveryFeature()

        // then
        verify(view).onShowProductDeliveryOption(showResultCaptor.capture())

        assertTrue(showResultCaptor.firstValue)
    }

    @Test
    fun `it should set false to onShowProductDeliveryOption param when getFeatureTogle result is false`() {
        // given
        doReturn(false)
            .whenever(featureTogglePreference)
            .getFeatureTogle(featureToggleValueCaptor.capture())

        // when
        presenter.verifyProductDeliveryFeature()

        // then
        verify(view).onShowProductDeliveryOption(showResultCaptor.capture())

        assertFalse(showResultCaptor.firstValue)
    }

}