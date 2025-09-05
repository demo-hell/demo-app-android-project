package br.com.mobicare.cielo.openFinance.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.openFinance.domain.model.InfoDetailsShare
import br.com.mobicare.cielo.openFinance.domain.usecase.CreateShareUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.TermsOfUseUseCase
import br.com.mobicare.cielo.openFinance.domain.usecase.UpdateShareUseCase
import br.com.mobicare.cielo.openFinance.presentation.manager.newShare.OpenFinanceNewShareViewModel
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsentDetail
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateFile
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory.deadline
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory.infoDetailsShare
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceFactory.jsonStringInfoDetailsShare
import com.google.gson.Gson
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class NewShareViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val useCaseCreateShare = mockk<CreateShareUseCase>()
    private val useCaseUpdateShare = mockk<UpdateShareUseCase>()
    private val useCaseTermsOfUse = mockk<TermsOfUseUseCase>()
    private val userPreferences = mockk<UserPreferences>(relaxed = true)
    private val requestCreateShare = OpenFinanceFactory.requestCreateShare
    private val requestUpdateShare = OpenFinanceFactory.requestUpdateShare
    private val successResponseCreateShare = OpenFinanceFactory.successCreateShare
    private val successResponseUpdateShare = OpenFinanceFactory.responseUpdateShare
    private val successResponseTermsOfUse = OpenFinanceFactory.responseTermsOfUse
    private val resultSuccessCreateShare = CieloDataResult.Success(successResponseCreateShare)
    private val resultSuccessUpdateShare = CieloDataResult.Success(successResponseUpdateShare)
    private val resultSuccessTermsOfUse = CieloDataResult.Success(successResponseTermsOfUse)
    private lateinit var viewModel: OpenFinanceNewShareViewModel

    @Before
    fun setUp() {
        every { userPreferences.infoDetailsShare } returns jsonStringInfoDetailsShare

        viewModel = OpenFinanceNewShareViewModel(useCaseCreateShare, useCaseUpdateShare, useCaseTermsOfUse, userPreferences)
    }

    @Test
    fun `it should set the success state in the create share call success result`() {
        runTest {

            coEvery {
                useCaseCreateShare(requestCreateShare)
            } returns resultSuccessCreateShare

            viewModel.createShare(requestCreateShare.authorizationServerId, requestCreateShare.organizationId)

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.createShareLiveData.value is UIStateConsentDetail.Success)
        }
    }

    @Test
    fun `it should set the error state in the create share call error result`() {
        runTest {

            coEvery {
                useCaseCreateShare(requestCreateShare)
            } returns OpenFinanceFactory.resultError

            viewModel.createShare(requestCreateShare.authorizationServerId, requestCreateShare.organizationId)

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.createShareLiveData.value is UIStateConsentDetail.Error)
        }
    }

    @Test
    fun `it should set the success state in the update share call success result`() {
        runTest {

            coEvery {
                useCaseUpdateShare(EMPTY, requestUpdateShare)
            } returns resultSuccessUpdateShare

            viewModel.updateShare(OpenFinanceFactory.deadline, ZERO)

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.updateShareLiveData.value is UIStateConsentDetail.Success)
        }
    }

    @Test
    fun `it should set the error state in the update share call error result`() {
        runTest {

            coEvery {
                useCaseUpdateShare(EMPTY, requestUpdateShare)
            } returns OpenFinanceFactory.resultError

            viewModel.updateShare(OpenFinanceFactory.deadline, ZERO)

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.updateShareLiveData.value is UIStateConsentDetail.Error)
        }
    }

    @Test
    fun `it should set the success state in the terms of use call success result`() {
        runTest {

            coEvery {
                useCaseTermsOfUse()
            } returns resultSuccessTermsOfUse

            viewModel.getTermsOfUse()

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.termsOfUseLiveData.value is UIStateFile.SuccessDocument)
        }
    }

    @Test
    fun `it should set the error state in the terms of use call error result`() {
        runTest {

            coEvery {
                useCaseTermsOfUse()
            } returns OpenFinanceFactory.resultError

            viewModel.getTermsOfUse()

            dispatcherRule.advanceUntilIdle()

            assert(viewModel.termsOfUseLiveData.value is UIStateFile.ErrorDocument)
        }
    }
}