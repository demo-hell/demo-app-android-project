package br.com.mobicare.cielo.openFinance.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.constants.ONE_TEXT
import br.com.mobicare.cielo.commons.constants.TWENTYFIVE_TEXT
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.openFinance.domain.usecase.SharedDataConsentsUseCase
import br.com.mobicare.cielo.openFinance.presentation.manager.sharedData.OpenFinanceSharedDataViewModel
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsents
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SharedDataViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val useCaseSharedData = mockk<SharedDataConsentsUseCase>()
    private val featureTogglePreference = mockk<GetFeatureTogglePreferenceUseCase>(relaxed = true)
    private val successResponse = OpenFinanceFactory.successResponseSharedDataConsent
    private val resultSuccess = CieloDataResult.Success(successResponse)
    private lateinit var viewModel: OpenFinanceSharedDataViewModel

    @Before
    fun setUp() {
        viewModel = OpenFinanceSharedDataViewModel(
            useCaseSharedData,
            featureTogglePreference
        )
    }

    @Test
    fun `it should set the success state in the shared data of the received consents call success result`() =
        runTest {

            coEvery {
                useCaseSharedData(
                    OpenFinanceConstants.RECEIVING_JOURNEY, ONE_TEXT, TWENTYFIVE_TEXT
                )
            } returns resultSuccess

            viewModel.getConsentsReceived()

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.getConsentsReceivedLiveData.value is UIStateConsents.Success)
        }

    @Test
    fun `it should set the error state in the shared data of the received consents call error result`() =
        runTest {

            coEvery {
                useCaseSharedData(
                    OpenFinanceConstants.RECEIVING_JOURNEY, ONE_TEXT, TWENTYFIVE_TEXT
                )
            } returns OpenFinanceFactory.resultError

            viewModel.getConsentsReceived()

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.getConsentsReceivedLiveData.value is UIStateConsents.Error)
        }

    @Test
    fun `it should set the success state in the shared data of the sent consents call success result`() =
        runTest {

            coEvery {
                useCaseSharedData(
                    OpenFinanceConstants.TRANSMITTING_JOURNEY, ONE_TEXT, TWENTYFIVE_TEXT
                )
            } returns resultSuccess

            viewModel.getConsentsSent()

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.getConsentsSentLiveData.value is UIStateConsents.Success)
        }

    @Test
    fun `it should set the error state in the shared data of the sent consents call error result`() =
        runTest {

            coEvery {
                useCaseSharedData(
                    OpenFinanceConstants.TRANSMITTING_JOURNEY, ONE_TEXT, TWENTYFIVE_TEXT
                )
            } returns OpenFinanceFactory.resultError

            viewModel.getConsentsSent()

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.getConsentsSentLiveData.value is UIStateConsents.Error)
        }
}