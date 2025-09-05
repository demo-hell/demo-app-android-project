package br.com.mobicare.cielo.arv.presentation.home

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.arv.presentation.home.whatsAppNews.ArvWhatsAppNewsViewModel
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences.Companion.ARV_WHATSAPP_NEWS_ALREADY_VIEWED
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences.Companion.ARV_WHATSAPP_NEWS_DISMISSED_COUNTER
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserViewCounterUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserViewHistoryUseCase
import br.com.mobicare.cielo.commons.domain.useCase.SaveUserViewCounterUseCase
import br.com.mobicare.cielo.commons.domain.useCase.SaveUserViewHistoryUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.ARV_ENABLE_WHATSAPP_NEWS
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArvWhatsAppNewsViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val getUserViewHistoryUseCase = mockk<GetUserViewHistoryUseCase>()
    private val saveUserViewHistoryUseCase = mockk<SaveUserViewHistoryUseCase>()
    private val getUserViewCounterUseCase = mockk<GetUserViewCounterUseCase>()
    private val saveUserViewCounterUseCase = mockk<SaveUserViewCounterUseCase>()
    private val getFeatureTogglePreferenceUseCase = mockk<GetFeatureTogglePreferenceUseCase>()

    private val context = mockk<Context>()

    private lateinit var viewModel: ArvWhatsAppNewsViewModel

    @Before
    fun setUp() {
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context

        viewModel =
            ArvWhatsAppNewsViewModel(
                getUserViewHistoryUseCase,
                saveUserViewHistoryUseCase,
                getUserViewCounterUseCase,
                saveUserViewCounterUseCase,
                getFeatureTogglePreferenceUseCase,
            )
    }

    private fun mockAndRunCheckEnablementAssertion(
        ftEnableNews: Boolean = true,
        isAlreadyViewedAndConfirmed: Boolean = false,
        dismissCounter: Int = 0,
        expectedEnableNewsValue: Boolean,
    ) {
        // given
        coEvery { getFeatureTogglePreferenceUseCase(ARV_ENABLE_WHATSAPP_NEWS) } returns
            CieloDataResult.Success(ftEnableNews)

        coEvery { getUserViewHistoryUseCase(ARV_WHATSAPP_NEWS_ALREADY_VIEWED) } returns
            CieloDataResult.Success(isAlreadyViewedAndConfirmed)

        coEvery { getUserViewCounterUseCase(ARV_WHATSAPP_NEWS_DISMISSED_COUNTER) } returns
            CieloDataResult.Success(dismissCounter)

        // when
        viewModel.checkEnablement()

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(viewModel.enableNews.value).isEqualTo(expectedEnableNewsValue)
    }

    @Test
    fun `it should disable news when feature toggle is off`() =
        runTest {
            mockAndRunCheckEnablementAssertion(
                ftEnableNews = false,
                expectedEnableNewsValue = false,
            )
        }

    @Test
    fun `it should enable news when it has been dismissed zero times`() =
        runTest {
            mockAndRunCheckEnablementAssertion(
                dismissCounter = 0,
                expectedEnableNewsValue = true,
            )
        }

    @Test
    fun `it should enable news when it has been dismissed one time`() =
        runTest {
            mockAndRunCheckEnablementAssertion(
                dismissCounter = 1,
                expectedEnableNewsValue = true,
            )
        }

    @Test
    fun `it should enable news when it has been dismissed two times`() =
        runTest {
            mockAndRunCheckEnablementAssertion(
                dismissCounter = 2,
                expectedEnableNewsValue = true,
            )
        }

    @Test
    fun `it should disable news when it has been dismissed three times or more`() =
        runTest {
            mockAndRunCheckEnablementAssertion(
                dismissCounter = 3,
                expectedEnableNewsValue = false,
            )
            mockAndRunCheckEnablementAssertion(
                dismissCounter = 4,
                expectedEnableNewsValue = false,
            )
        }

    @Test
    fun `it should disable news when it has already been viewed and confirmed`() =
        runTest {
            mockAndRunCheckEnablementAssertion(
                isAlreadyViewedAndConfirmed = true,
                expectedEnableNewsValue = false,
            )
        }
}
