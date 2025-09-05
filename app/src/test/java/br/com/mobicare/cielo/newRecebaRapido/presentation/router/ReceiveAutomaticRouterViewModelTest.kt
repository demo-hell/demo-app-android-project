package br.com.mobicare.cielo.newRecebaRapido.presentation.router

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domain.useCase.GetUserViewHistoryUseCase
import br.com.mobicare.cielo.commons.rules.TestDispatcherRule
import br.com.mobicare.cielo.newRecebaRapido.domain.entity.EligibilityDetails
import br.com.mobicare.cielo.newRecebaRapido.domain.entity.FastRepayRule
import br.com.mobicare.cielo.newRecebaRapido.domain.entity.ReceiveAutomaticEligibility
import br.com.mobicare.cielo.newRecebaRapido.domain.usecase.GetReceiveAutomaticEligibilityUseCase
import br.com.mobicare.cielo.newRecebaRapido.util.UiReceiveAutomaticRouterState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReceiveAutomaticRouterViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val dispatcherRule = TestDispatcherRule()

    private val states = mutableListOf<UiReceiveAutomaticRouterState>()
    private val getUserViewHistoryUseCase = mockk<GetUserViewHistoryUseCase>()
    private val getReceiveAutomaticEligibilityUseCase = mockk<GetReceiveAutomaticEligibilityUseCase>()

    private lateinit var viewModel: ReceiveAutomaticRouterViewModel

    @Before
    fun setUp() {
        viewModel =
            ReceiveAutomaticRouterViewModel(
                getUserViewHistoryUseCase,
                getReceiveAutomaticEligibilityUseCase,
            )
        viewModel.receiveAutomaticRouterMutableLiveData.observeForever { states.add(it) }
    }

    @After
    fun tearDown() {
        viewModel.receiveAutomaticRouterMutableLiveData.removeObserver { }
    }

    @Test
    fun `it should set UiReceiveAutomaticRouterState as ShowHome when the tUserViewHistory is true and has eligibility`() =
        runTest {
            // given
            coEvery { getReceiveAutomaticEligibilityUseCase() } returns
                CieloDataResult.Success(
                    ReceiveAutomaticEligibility(
                        eligible = true,
                        eligibilityDetails =
                            EligibilityDetails(
                                fastRepayRules = listOf(),
                            ),
                    ),
                )
            coEvery { getUserViewHistoryUseCase(any()) } returns CieloDataResult.Success(true)

            // when
            viewModel.initiateReceiveAutomaticVerificationFlow()

            // then
            dispatcherRule.advanceUntilIdle()
            assertUiStateTransition(UiReceiveAutomaticRouterState.ShowHome)
        }

    @Test
    fun `it should set UiReceiveAutomaticRouterState as ShowOnBoarding when the tUserViewHistory is false and has eligibility`() =
        runTest {
            // given
            coEvery { getReceiveAutomaticEligibilityUseCase() } returns
                CieloDataResult.Success(
                    ReceiveAutomaticEligibility(
                        eligible = true,
                        eligibilityDetails =
                            EligibilityDetails(
                                fastRepayRules = listOf(),
                            ),
                    ),
                )
            coEvery { getUserViewHistoryUseCase(any()) } returns CieloDataResult.Success(false)

            // when
            viewModel.initiateReceiveAutomaticVerificationFlow()

            // then
            dispatcherRule.advanceUntilIdle()
            assertUiStateTransition(
                UiReceiveAutomaticRouterState.ShowOnBoarding,
            )
        }

    @Test
    fun `it should set UiReceiveAutomaticRouterState as ShowOnboarding when the tUserViewHistory is empty and has eligibility`() =
        runTest {
            // given
            coEvery { getReceiveAutomaticEligibilityUseCase() } returns
                CieloDataResult.Success(
                    ReceiveAutomaticEligibility(
                        eligible = true,
                        eligibilityDetails =
                            EligibilityDetails(
                                fastRepayRules = listOf(),
                            ),
                    ),
                )
            coEvery { getUserViewHistoryUseCase(any()) } returns CieloDataResult.Empty()

            // when
            viewModel.initiateReceiveAutomaticVerificationFlow()

            // then
            dispatcherRule.advanceUntilIdle()

            assertUiStateTransition(
                UiReceiveAutomaticRouterState.ShowOnBoarding,
            )
        }

    @Test
    fun `it should set to ShowIneligibleError when the user doesn't have eligibility and has ineligibleRuleCode`() =
        runTest {
            // given
            val result =
                ReceiveAutomaticEligibility(
                    eligible = false,
                    eligibilityDetails =
                        EligibilityDetails(
                            fastRepayRules =
                                listOf(
                                    FastRepayRule(
                                        ruleCode = 4,
                                        ruleEligible = false,
                                        ruleContractRestricted = true,
                                        ruleDescription = "description",
                                    ),
                                ),
                        ),
                )
            coEvery { getReceiveAutomaticEligibilityUseCase() } returns
                CieloDataResult.Success(
                    result,
                )

            // when
            viewModel.initiateReceiveAutomaticVerificationFlow()

            // then
            dispatcherRule.advanceUntilIdle()

            assertUiStateTransition(
                UiReceiveAutomaticRouterState.ShowIneligibleError(result.eligibilityDetails.fastRepayRules.first()),
            )
        }

    @Test
    fun `it should set to ShowContractedServiceError when the user doesn't have eligibility and doesn't have a contractedRuleCode`() =
        runTest {
            // given
            val result =
                ReceiveAutomaticEligibility(
                    eligible = false,
                    eligibilityDetails =
                        EligibilityDetails(
                            fastRepayRules =
                                listOf(
                                    FastRepayRule(
                                        ruleCode = 8,
                                        ruleEligible = false,
                                        ruleContractRestricted = true,
                                        ruleDescription = "description",
                                    ),
                                ),
                        ),
                )
            coEvery { getReceiveAutomaticEligibilityUseCase() } returns
                CieloDataResult.Success(
                    result,
                )

            // when
            viewModel.initiateReceiveAutomaticVerificationFlow()

            // then
            dispatcherRule.advanceUntilIdle()

            assertUiStateTransition(
                UiReceiveAutomaticRouterState.ShowContractedServiceError(result.eligibilityDetails.fastRepayRules.first()),
            )
        }

    @Test
    fun `it should set to ShowGenericError when the user doesn't have eligibility, contractedRuleCode or ineligibleRuleCode`() =
        runTest {
            // given
            coEvery { getReceiveAutomaticEligibilityUseCase() } returns
                CieloDataResult.Success(
                    ReceiveAutomaticEligibility(
                        eligible = false,
                        eligibilityDetails =
                            EligibilityDetails(
                                fastRepayRules =
                                    listOf(
                                        FastRepayRule(
                                            ruleCode = 8,
                                            ruleEligible = false,
                                            ruleContractRestricted = false,
                                            ruleDescription = "description",
                                        ),
                                    ),
                            ),
                    ),
                )

            // when
            viewModel.initiateReceiveAutomaticVerificationFlow()

            // then
            dispatcherRule.advanceUntilIdle()

            assertUiStateTransition(
                UiReceiveAutomaticRouterState.ShowGenericError(),
            )
        }

    @Test
    fun `it should set to ShowGenericError when eligibility api returns an error`() =
        runTest {
            // given
            val error =
                CieloDataResult.APIError(
                    CieloAPIException(
                        newErrorMessage = NewErrorMessage(),
                        httpStatusCode = 500,
                        message = "message",
                        actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
                    ),
                )
            coEvery { getReceiveAutomaticEligibilityUseCase() } returns
                (error)

            // when
            viewModel.initiateReceiveAutomaticVerificationFlow()

            // then
            dispatcherRule.advanceUntilIdle()

            assertUiStateTransition(
                UiReceiveAutomaticRouterState.ShowGenericError(error.apiException.newErrorMessage),
            )
        }

    @Test
    fun `it should set to ShowGenericError when getUserViewHistory returns an error`() =
        runTest {
            // given
            val error =
                CieloDataResult.APIError(
                    CieloAPIException(
                        newErrorMessage = NewErrorMessage(),
                        httpStatusCode = 500,
                        message = "message",
                        actionErrorType = ActionErrorTypeEnum.HTTP_ERROR,
                    ),
                )
            coEvery { getReceiveAutomaticEligibilityUseCase() } returns
                CieloDataResult.Success(
                    ReceiveAutomaticEligibility(
                        eligible = true,
                        eligibilityDetails =
                            EligibilityDetails(
                                fastRepayRules = listOf(),
                            ),
                    ),
                )
            coEvery { getUserViewHistoryUseCase(any()) } returns error

            // when
            viewModel.initiateReceiveAutomaticVerificationFlow()

            // then
            dispatcherRule.advanceUntilIdle()

            assertUiStateTransition(
                UiReceiveAutomaticRouterState.ShowGenericError(error.apiException.newErrorMessage),
            )
        }

    private fun assertUiStateTransition(finalState: UiReceiveAutomaticRouterState) {
        assert(states.size == 3)
        assert(states[0] is UiReceiveAutomaticRouterState.ShowLoading)
        assert(states[1] is UiReceiveAutomaticRouterState.HideLoading)
        assert(states[2] == finalState)
    }
}
