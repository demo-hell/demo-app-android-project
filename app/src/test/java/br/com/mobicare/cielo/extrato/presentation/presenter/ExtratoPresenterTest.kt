package br.com.mobicare.cielo.extrato.presentation.presenter

import android.content.Context
import br.com.mobicare.cielo.extrato.presentation.ui.fragments.ExtratoFragment
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.CHARGEBACK_REMOVE
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.SOLESP_REMOVE
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ExtratoPresenterTest {

    @Mock
    lateinit var view: ExtratoFragment

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var featureTogglePreference: FeatureTogglePreference

    private lateinit var presenter: ExtratoPresenter

    @Before
    fun setup(){
        MockitoAnnotations.initMocks(this)
        presenter = ExtratoPresenter(view, context, featureTogglePreference)
    }

    @Test
    fun `menu solesp and chargeback enabled`(){
        doReturn(true).whenever(featureTogglePreference)
            .getFeatureTogle(SOLESP_REMOVE)
        doReturn(true).whenever(featureTogglePreference)
            .getFeatureTogle(CHARGEBACK_REMOVE)

        presenter.openBottomSheetMoreOptions()

        val captor = argumentCaptor<Boolean>()
        val captorChargeback = argumentCaptor<Boolean>()

        verify(view).openBottomSheetMoreOptions(captor.capture(),captorChargeback.capture())

        assertEquals(true, captor.firstValue)
        assertEquals(true, captorChargeback.firstValue)
    }

    @Test
    fun `menu solesp disabled`(){
        doReturn(false).whenever(featureTogglePreference)
            .getFeatureTogle(SOLESP_REMOVE)
        doReturn(false).whenever(featureTogglePreference)
            .getFeatureTogle(CHARGEBACK_REMOVE)

        presenter.openBottomSheetMoreOptions()

        val captor = argumentCaptor<Boolean>()
        val captorChargeback = argumentCaptor<Boolean>()

        verify(view).openBottomSheetMoreOptions(captor.capture(), captorChargeback.capture())

        assertEquals(false, captor.firstValue)
        assertEquals(false, captorChargeback.firstValue)
    }

}