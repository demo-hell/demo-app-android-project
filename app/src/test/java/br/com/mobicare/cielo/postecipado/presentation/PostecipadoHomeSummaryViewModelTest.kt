package br.com.mobicare.cielo.postecipado.presentation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.home.presentation.postecipado.domain.usecase.GetPostecipadoSummaryUseCase
import br.com.mobicare.cielo.home.presentation.postecipado.presentation.PostecipadoHomeSummaryViewModel
import br.com.mobicare.cielo.home.presentation.postecipado.utils.PostecipadoUiState.*
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.postecipado.utils.PostecipadoFactory
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
class PostecipadoHomeSummaryViewModelTest {

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val context = mockk<Context>(relaxed = true)
    private val userPreferences = mockk<UserPreferences>(relaxed = true)

    private val getPostecipadoSummaryUseCase = mockk<GetPostecipadoSummaryUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()

    private lateinit var viewModel: PostecipadoHomeSummaryViewModel

    private val completeResponse = PostecipadoFactory.getCompleteResponse()
    private val emptyList = PostecipadoFactory.getEmptyListResponse()
    private val dataResultSuccess = CieloDataResult.Success(completeResponse)
    private val dataResultEmptyResponse = CieloDataResult.Success(emptyList)
    private val resultError = CieloDataResult.APIError(CieloAPIException.networkError(EMPTY))

    @Before
    fun setUp() {
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())

        viewModel = PostecipadoHomeSummaryViewModel(getPostecipadoSummaryUseCase, getUserObjUseCase)
    }

    @Test
    fun `getPlanInformation should update PostecipadoUiState with Success when use case succeeds`() = runTest {
        coEvery { getPostecipadoSummaryUseCase(any()) } returns dataResultSuccess

        viewModel.getPlanInformation()

        dispatcherRule.advanceUntilIdle()

        viewModel.postecipadoUiState.value.let {
          assert(it is Success && completeResponse.first() == it.data)
        }
    }

    @Test
    fun `getPlanInformation should update PostecipadoUiState with Error when use case returns an error` () = runTest {
        coEvery { getPostecipadoSummaryUseCase(any()) } returns resultError

        viewModel.getPlanInformation()

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.postecipadoUiState.value is Error)
    }

    @Test
    fun `getPlanInformation should update PostecipadoUiState with Empty when use case returns an empty result` () = runTest {
        coEvery { getPostecipadoSummaryUseCase(any()) } returns CieloDataResult.Empty()

        viewModel.getPlanInformation()

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.postecipadoUiState.value is Empty)
    }

    @Test
    fun `getPlanInformation should update PostecipadoUiState with Empty when use case returns an empty list`() = runTest {
        coEvery { getPostecipadoSummaryUseCase(any()) } returns dataResultEmptyResponse

        viewModel.getPlanInformation()

        dispatcherRule.advanceUntilIdle()

        assert(viewModel.postecipadoUiState.value is Empty)
    }
}