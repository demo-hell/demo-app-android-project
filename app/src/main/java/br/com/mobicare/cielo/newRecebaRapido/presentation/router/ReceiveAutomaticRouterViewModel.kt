package br.com.mobicare.cielo.newRecebaRapido.presentation.router

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserViewHistoryUseCase
import br.com.mobicare.cielo.newRecebaRapido.domain.entity.EligibilityDetails
import br.com.mobicare.cielo.newRecebaRapido.domain.entity.ReceiveAutomaticEligibility
import br.com.mobicare.cielo.newRecebaRapido.domain.usecase.GetReceiveAutomaticEligibilityUseCase
import br.com.mobicare.cielo.newRecebaRapido.presentation.enums.ContractedRuleCodeEnum
import br.com.mobicare.cielo.newRecebaRapido.presentation.enums.EligibilityErrorEnum
import br.com.mobicare.cielo.newRecebaRapido.presentation.enums.EligibilityErrorEnum.CONTRACTED_SERVICE_RULE
import br.com.mobicare.cielo.newRecebaRapido.presentation.enums.EligibilityErrorEnum.INELIGIBLE_RULE
import br.com.mobicare.cielo.newRecebaRapido.presentation.enums.EligibilityErrorEnum.INVALID_RULE
import br.com.mobicare.cielo.newRecebaRapido.presentation.enums.IneligibilityRuleCodeEnum
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.USER_VIEW_RECEIVE_AUTOMATIC_ONBOARDING
import br.com.mobicare.cielo.newRecebaRapido.util.UiReceiveAutomaticRouterState
import kotlinx.coroutines.launch

class ReceiveAutomaticRouterViewModel(
    private val getUserViewHistory: GetUserViewHistoryUseCase,
    private val getReceiveAutomaticEligibilityUseCase: GetReceiveAutomaticEligibilityUseCase,
) : ViewModel() {
    private val _receiveAutomaticRouterMutableLiveData = MutableLiveData<UiReceiveAutomaticRouterState>()
    val receiveAutomaticRouterMutableLiveData: LiveData<UiReceiveAutomaticRouterState> get() = _receiveAutomaticRouterMutableLiveData

    fun initiateReceiveAutomaticVerificationFlow() {
        _receiveAutomaticRouterMutableLiveData.value = UiReceiveAutomaticRouterState.ShowLoading
        viewModelScope.launch {
            getReceiveAutomaticEligibilityUseCase()
                .onSuccess {
                    handleEligibility(it)
                }.onEmpty {
                    updateRouterState(UiReceiveAutomaticRouterState.ShowGenericError())
                }.onError { error ->
                    updateRouterState(UiReceiveAutomaticRouterState.ShowGenericError(error.apiException.newErrorMessage))
                }
        }
    }

    private fun handleEligibility(eligibility: ReceiveAutomaticEligibility) {
        if (eligibility.eligible) {
            getUserViewReceiveAutomaticOnBoarding()
        } else {
            handleRuleCode(eligibility.eligibilityDetails)
        }
    }

    private fun getUserViewReceiveAutomaticOnBoarding() {
        viewModelScope.launch {
            getUserViewHistory(key = USER_VIEW_RECEIVE_AUTOMATIC_ONBOARDING)
                .onSuccess { completedOnBoarding ->
                    updateRouterState(
                        if (completedOnBoarding) {
                            UiReceiveAutomaticRouterState.ShowHome
                        } else {
                            UiReceiveAutomaticRouterState.ShowOnBoarding
                        },
                    )
                }.onEmpty {
                    updateRouterState(UiReceiveAutomaticRouterState.ShowOnBoarding)
                }.onError {
                    updateRouterState(UiReceiveAutomaticRouterState.ShowGenericError(it.apiException.newErrorMessage))
                }
        }
    }

    private fun handleRuleCode(eligibilityDetails: EligibilityDetails) {
        val fastRepayRule = eligibilityDetails.fastRepayRules.find { it.ruleEligible.not() && it.ruleContractRestricted }

        when (verifyRuleError(fastRepayRule?.ruleCode)) {
            INELIGIBLE_RULE -> {
                updateRouterState(UiReceiveAutomaticRouterState.ShowIneligibleError(fastRepayRule))
            }
            CONTRACTED_SERVICE_RULE -> {
                updateRouterState(UiReceiveAutomaticRouterState.ShowContractedServiceError(fastRepayRule))
            }
            INVALID_RULE -> {
                updateRouterState(UiReceiveAutomaticRouterState.ShowGenericError())
            }
        }
    }

    private fun verifyRuleError(code: Int?): EligibilityErrorEnum {
        return when {
            IneligibilityRuleCodeEnum.values().any { code == it.code } -> INELIGIBLE_RULE
            ContractedRuleCodeEnum.values().any { code == it.code } -> CONTRACTED_SERVICE_RULE
            else -> INVALID_RULE
        }
    }

    private fun updateRouterState(newState: UiReceiveAutomaticRouterState) {
        _receiveAutomaticRouterMutableLiveData.value = UiReceiveAutomaticRouterState.HideLoading
        _receiveAutomaticRouterMutableLiveData.value = newState
    }
}
