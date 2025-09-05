package br.com.mobicare.cielo.openFinance.presentation.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.mainbottomnavigation.presenter.EMPTY
import br.com.mobicare.cielo.openFinance.domain.model.ConsentDetail
import br.com.mobicare.cielo.openFinance.domain.usecase.ConsentDetailUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.EndShareUseCase
import br.com.mobicare.cielo.openFinance.presentation.manager.sharedData.consentDetail.ConsentDetailViewModel
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsentDetail
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateEndShare
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory.resultError
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConsentDetailViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val useCaseConsentDetail = mockk<ConsentDetailUseCase>()
    private val useCaseEndShare = mockk<EndShareUseCase>()
    private val userPreferences = mockk<UserPreferences>(relaxed = true)
    private val requestEndShare = OpenFinanceFactory.endShareRequest
    private val successResponse = OpenFinanceFactory.successConsentDetail
    private val resultSuccess = CieloDataResult.Success(successResponse)
    private val resultSuccessEndShare = CieloDataResult.Success(Any())
    private val context: Context = mockk()
    private lateinit var viewModel: ConsentDetailViewModel

    @Before
    fun setUp() {
        viewModel = ConsentDetailViewModel(useCaseConsentDetail, useCaseEndShare, userPreferences)
    }

    @Test
    fun `it should set the success state in the consent detail call success result`() =
        runTest {

            coEvery { useCaseConsentDetail(EMPTY) } returns resultSuccess

            viewModel.getConsentDetail(EMPTY, context)

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.getConsentDetailLiveData.value is UIStateConsentDetail.Success)
        }

    @Test
    fun `it should set the error state in the consent detail call error result`() =
        runTest {

            coEvery { useCaseConsentDetail(EMPTY) } returns resultError

            viewModel.getConsentDetail(EMPTY, context)

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.getConsentDetailLiveData.value is UIStateConsentDetail.Error)
        }

    @Test
    fun `it should set loading state before success state in the consent detail call result`() =
        runTest {

            coEvery { useCaseConsentDetail(EMPTY) } returns resultSuccess

            val states = mutableListOf<UIStateConsentDetail<ConsentDetail>>()

            viewModel.getConsentDetailLiveData.observeForever { states.add(it) }

            viewModel.getConsentDetail(EMPTY, context)

            dispatcherRule.advanceUntilIdle()

            assert(states[0] is UIStateConsentDetail.Loading)
            assert(states[1] is UIStateConsentDetail.Success)
        }

    @Test
    fun `it should set the success state in the end sharing call success result`() =
        runTest {

            coEvery { useCaseEndShare(EMPTY, requestEndShare) } returns resultSuccessEndShare

            viewModel.endShare(EMPTY)

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.endShareLiveData.value is UIStateEndShare.SuccessEndShare)
        }

    @Test
    fun `it should set the error state in the end sharing call error result`() =
        runTest {

            coEvery { useCaseEndShare(EMPTY, requestEndShare) } returns resultError

            viewModel.endShare(EMPTY)

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.endShareLiveData.value is UIStateEndShare.ErrorEndShare)
        }
}