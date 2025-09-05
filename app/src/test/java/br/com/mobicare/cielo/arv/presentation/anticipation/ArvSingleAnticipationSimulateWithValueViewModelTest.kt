package br.com.mobicare.cielo.arv.presentation.anticipation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.useCase.GetArvSingleAnticipationWithValueNewUseCase
import br.com.mobicare.cielo.arv.utils.*
import br.com.mobicare.cielo.arv.utils.ArvFactory.arvSingleAnticipation
import br.com.mobicare.cielo.arv.utils.ArvFactory.resultError
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.commons.utils.captureValues
import br.com.mobicare.cielo.login.domains.entities.UserObj
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
class ArvSingleAnticipationSimulateWithValueViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val arvSingleAnticipationWithValueNewUseCase =
        mockk<GetArvSingleAnticipationWithValueNewUseCase>()
    private val getUserObjUseCase = mockk<GetUserObjUseCase>()

    private val context = mockk<Context>()

    private val initialDate = "03-05-1991"
    private val finalDate = "03-05-2024"

    private lateinit var viewModel: ArvSingleAnticipationSimulateWithValueViewModel

    @Before
    fun setUp() {
        viewModel = ArvSingleAnticipationSimulateWithValueViewModel(
            arvSingleAnticipationWithValueNewUseCase,
            getUserObjUseCase
        )

        coEvery { getUserObjUseCase() } returns CieloDataResult.Success(UserObj())
        mockkObject(CieloApplication)
        every { CieloApplication.context } returns context
    }



    @Test
    fun `it should set UiArvSingleWithValueState as SuccessArvSingleWithValue`() = runTest {
        // given
        coEvery { arvSingleAnticipationWithValueNewUseCase.invoke(any(), any(),any(), any(), any()) } returns
                CieloDataResult.Success(arvSingleAnticipation)

        val uiState = viewModel.arvSingleAnticipationWithValueLiveData.captureValues()

        // when
        viewModel.getArvSingleAnticipationWithValue(ZERO_DOUBLE, initialDate, finalDate)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(uiState[0]).isEqualTo(UiArvSingleWithValueState.ShowLoadingArvSingleWithValue)
        assertThat(uiState[1]).isEqualTo(UiArvSingleWithValueState.HideLoadingArvSingleWithValue)

        assertThat(uiState[2]).isEqualTo(
            UiArvSingleWithValueState.SuccessArvSingleWithValue(arvSingleAnticipation)
        )
    }

    @Test
    fun `it should set UiArvSingleWithValueState as ErrorArvSingleWithValue`() = runTest {
        // given
        coEvery { arvSingleAnticipationWithValueNewUseCase.invoke(any(), any(),any(), any(), any()) } returns resultError

        val uiState = viewModel.arvSingleAnticipationWithValueLiveData.captureValues()

        // when
        viewModel.getArvSingleAnticipationWithValue(ZERO_DOUBLE, initialDate, finalDate)

        // then
        dispatcherRule.advanceUntilIdle()

        assertThat(uiState[0]).isEqualTo(UiArvSingleWithValueState.ShowLoadingArvSingleWithValue)
        assertThat(uiState[1]).isEqualTo(UiArvSingleWithValueState.HideLoadingArvSingleWithValue)

        assertThat(uiState[2]).isEqualTo(
            UiArvSingleWithValueState.ErrorArvSingleWithValue(resultError.apiException.newErrorMessage, R.string.anticipation_error)
        )
    }


    @Test
    fun `it should set UiArvSingleWithValueState as SuccessArvSingleWithValue when the CieloDataResult is Success and the response is not null`() =
        runTest {
            // given
            coEvery {
                arvSingleAnticipationWithValueNewUseCase.invoke(any(), any(),any(), any(), any())
            } returns CieloDataResult.Success(arvSingleAnticipation)

            val uiState = viewModel.arvSingleAnticipationWithValueLiveData.captureValues()

            // when
            viewModel.getArvSingleAnticipationWithValue(ZERO_DOUBLE, initialDate, finalDate)

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvSingleWithValueState.ShowLoadingArvSingleWithValue)
            assertThat(uiState[1]).isEqualTo(UiArvSingleWithValueState.HideLoadingArvSingleWithValue)
            assertThat(viewModel.arvSingleAnticipationWithValueLiveData.value).isEqualTo(
                UiArvSingleWithValueState.SuccessArvSingleWithValue(
                    ArvAnticipation(
                        acquirers = listOf(ArvFactory.acquirer),
                        discountAmount = 1.00,
                        finalDate = "2024-03-28",
                        grossAmount = 65.00,
                        id = "ID",
                        initialDate = "2024-03-28",
                        negotiationType = "CIELO",
                        netAmount = 0.13,
                        nominalFee = 0.00,
                        standardFee = 1.40,
                        token = "abc",
                        effectiveFee = 0.10
                    )
                )
            )
        }

    @Test
    fun `it should set UiArvSingleWithValueState as SuccessArvSingleWithValue when the CieloDataResult is Success and the response is not equal`() =
        runTest {
            // given
            coEvery {
                arvSingleAnticipationWithValueNewUseCase.invoke(any(), any(),any(), any(), any())
            } returns CieloDataResult.Success( arvSingleAnticipation )
            val uiState = viewModel.arvSingleAnticipationWithValueLiveData.captureValues()

            // when
            viewModel.getArvSingleAnticipationWithValue(ZERO_DOUBLE, initialDate, finalDate)

            // then
            dispatcherRule.advanceUntilIdle()

            assertThat(uiState[0]).isEqualTo(UiArvSingleWithValueState.ShowLoadingArvSingleWithValue)
            assertThat(uiState[1]).isEqualTo(UiArvSingleWithValueState.HideLoadingArvSingleWithValue)
            assertThat(viewModel.arvSingleAnticipationWithValueLiveData.value).isNotEqualTo(
                UiArvSingleWithValueState.SuccessArvSingleWithValue(
                    ArvAnticipation(
                        acquirers = listOf(ArvFactory.acquirer),
                        discountAmount = 1.00,
                        finalDate = null,
                        grossAmount = 65.00,
                        id = "ID",
                        initialDate = null,
                        negotiationType = "CIELO",
                        netAmount = 0.13,
                        nominalFee = 0.00,
                        standardFee = 1.40,
                        token = "abc",
                        effectiveFee = 0.10
                    )
                )
            )
        }
}
