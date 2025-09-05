package br.com.mobicare.cielo.pix.ui.extract.onboarding

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class PixExtractOnboardingPresenterTest {
    @Mock
    lateinit var userPreferences: UserPreferences

    @Mock
    lateinit var view: PixOnboardingExtractContract.View

    private lateinit var presenter: PixOnboardingExtractPresenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = PixOnboardingExtractPresenter(view, userPreferences)
    }

    @Test
    fun `when saveShowPixOnboardingExtract passing true is called, value must be persisted`() {
        presenter.saveShowPixOnboardingExtract()
        doReturn(true).whenever(userPreferences).isShowPixOnboardingExtract
        Assert.assertTrue(userPreferences.isShowPixOnboardingExtract)
    }
    @Test
    fun `when saveShowPixOnboardingExtract passing false is called, value must not be persisted`() {
        presenter.saveShowPixOnboardingExtract()
        doReturn(false).whenever(userPreferences).isShowPixOnboardingExtract
        Assert.assertFalse(userPreferences.isShowPixOnboardingExtract)
    }
}