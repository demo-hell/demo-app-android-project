package br.com.mobicare.cielo.chargeback.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.chargeback.domain.useCase.GetChargebackDescriptionReasonUseCase
import br.com.mobicare.cielo.chargeback.presentation.details.viewmodel.ChargebackFeatureToggleViewModel
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory.reason1Text
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory.reason2Text
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory.reason3Text
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory.reasonFromAPI
import br.com.mobicare.cielo.chargeback.utils.ChargebackFactory.chargebackDetailsMock
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase


@OptIn(ExperimentalCoroutinesApi::class)
class ChargebackFeatureToggleViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val dispatcherRule = TestDispatcherRule()

    private val getChargebackDescriptionReasonUseCase = mockk<GetChargebackDescriptionReasonUseCase>()
    private val getFeatureTogglePreference = mockk<GetFeatureTogglePreferenceUseCase>()
    private lateinit var viewModel: ChargebackFeatureToggleViewModel

    @Before
    fun setup() {
        viewModel = ChargebackFeatureToggleViewModel(getChargebackDescriptionReasonUseCase,getFeatureTogglePreference)
    }

    @Test
    fun `check if description ReasonType 1 message is loading`() = runTest {

        //given
        coEvery { getChargebackDescriptionReasonUseCase.invoke(any(), any(), any()) } coAnswers {reason1Text}

        //when
        viewModel.getDescriptionReasonTypeFeatureToggle(chargebackDetailsMock)
        //then
        dispatcherRule.advanceUntilIdle()

        //assert
        assert((viewModel.descriptionReasonTypeMessage as String) == reason1Text)

    }

    @Test
    fun `check if description ReasonType 2 message is loading`() = runTest {

        //given
        coEvery { getChargebackDescriptionReasonUseCase.invoke(any(), any(), any()) } coAnswers {reason2Text}

        //when
        viewModel.getDescriptionReasonTypeFeatureToggle(chargebackDetailsMock)
        //then
        dispatcherRule.advanceUntilIdle()

        //assert
        assert((viewModel.descriptionReasonTypeMessage as String) == reason2Text)

    }


    @Test
    fun `check if description ReasonType 3 message is loading`() = runTest {

        //given
        coEvery { getChargebackDescriptionReasonUseCase.invoke(any(), any(), any()) } coAnswers {reason3Text}

        //when
        viewModel.getDescriptionReasonTypeFeatureToggle(chargebackDetailsMock)
        //then
        dispatcherRule.advanceUntilIdle()

        //assert
        assert((viewModel.descriptionReasonTypeMessage as String) == reason3Text)

    }

    @Test
    fun `check if description returned via API is loading`() = runTest {

        //given
        coEvery { getChargebackDescriptionReasonUseCase.invoke(any(), any(), any()) } coAnswers {reasonFromAPI}

        //when
        viewModel.getDescriptionReasonTypeFeatureToggle(chargebackDetailsMock)
        //then
        dispatcherRule.advanceUntilIdle()

        //assert
        assert((viewModel.descriptionReasonTypeMessage as String) == reasonFromAPI)

    }
}