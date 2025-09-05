package br.com.mobicare.cielo.solesp.presenter

import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.solesp.ui.start.SolespStartContract
import br.com.mobicare.cielo.solesp.ui.start.SolespStartPresenter
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class SolespStartPresenterTest {

    @Mock
    lateinit var view: SolespStartContract.View

    @Mock
    lateinit var featureTogglePreference: FeatureTogglePreference

    private lateinit var presenter: SolespStartPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = SolespStartPresenter(view, featureTogglePreference)
    }

    @Test
    fun `solesp enabled`() {
        doReturn(true).whenever(featureTogglePreference).getFeatureTogle(FeatureTogglePreference.SOLESP)

        presenter.getSolespEnabled()

        verify(view, never()).showSolespDisabled()
    }

    @Test
    fun `solesp disabled`() {
        doReturn(false).whenever(featureTogglePreference).getFeatureTogle(FeatureTogglePreference.SOLESP)

        presenter.getSolespEnabled()

        verify(view).showSolespDisabled()
    }

}