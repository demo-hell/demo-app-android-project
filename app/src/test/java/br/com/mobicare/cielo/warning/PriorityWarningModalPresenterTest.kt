package br.com.mobicare.cielo.warning

import br.com.mobicare.cielo.commons.domains.entities.PriorityWarningVisualization
import br.com.mobicare.cielo.commons.warning.WarningModalPresenter
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleModal
import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PriorityWarningModalPresenterTest {

    private val modal = FeatureToggleModal(id = "1",
            name = "aviso_arv",
            imageUrl = "image.png",
            title = "Aviso",
            message = "Mensagem Aviso",
            external = true,
            actionUrl = "https://google.com",
            actionTitle = "Ir para loja",
            stickyModal = false,
            loggedModal = true
    )

    @Mock
    lateinit var featureTogglePreference: FeatureTogglePreference

    @Mock
    lateinit var menuPreference: MenuPreference

    private lateinit var presenter: WarningModalPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        doReturn(EstabelecimentoObj(ec = "0123456789",
                tradeName = null,
                cnpj = null
        )).whenever(menuPreference).getEstablishment()

        presenter = WarningModalPresenter(featureTogglePreference, menuPreference)
    }

    @Test
    fun `when getEstablishment is different from null SawWarning cannot be null`() {
        val captor = argumentCaptor<List<PriorityWarningVisualization>>()

        presenter.onSaveUserInteraction(modal)
        verify(featureTogglePreference).saveSawWarning(captor.capture())

        assertEquals("1", captor.firstValue[0].idModal)
        assertEquals("0123456789", captor.firstValue[0].ec)
    }

    @Test
    fun `when getEstablishment is null SawWarning must be null`() {
        doReturn(null).whenever(menuPreference).getEstablishment()

        val captor = argumentCaptor<List<PriorityWarningVisualization>>()

        presenter.onSaveUserInteraction(modal)
        verify(featureTogglePreference).saveSawWarning(captor.capture())

        assertEquals("1", captor.firstValue[0].idModal)
        assertEquals(null, captor.firstValue[0].ec)
    }
}