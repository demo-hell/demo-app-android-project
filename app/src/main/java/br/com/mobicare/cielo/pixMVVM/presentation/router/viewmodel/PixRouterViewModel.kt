package br.com.mobicare.cielo.pixMVVM.presentation.router.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.MENU_PIX
import br.com.mobicare.cielo.mfa.MfaEligibilityResponse
import br.com.mobicare.cielo.mfa.MfaRepository
import br.com.mobicare.cielo.mfa.commons.MerchantStatusMFA
import br.com.mobicare.cielo.pixMVVM.domain.enums.BlockType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixStatus
import br.com.mobicare.cielo.pixMVVM.domain.enums.ProfileType
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetOnBoardingFulfillmentUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.router.utils.PixRouterUiState
import kotlinx.coroutines.launch

class PixRouterViewModel(
    private val getOnBoardingFulfillmentUseCase: GetOnBoardingFulfillmentUseCase,
    private val featureTogglePreference: FeatureTogglePreference,
    private val userPreferences: UserPreferences,
    private val mfaRepository: MfaRepository
) : ViewModel() {

    private var showDataQuery = false

    private val _uiState = MutableLiveData<PixRouterUiState>()
    val uiState: LiveData<PixRouterUiState> get() = _uiState

    private val featureToggleMenuPix
        get() = featureTogglePreference.getFeatureToggleObject(MENU_PIX)

    private val showMenuPix get() = featureToggleMenuPix?.show ?: false

    private val isOnBoardingViewed get() = userPreferences.isPixOnboardingHomeViewed

    private val isUnavailable get() = showMenuPix.not() && showDataQuery.not()

    fun setShowDataQuery(value: Boolean) {
        showDataQuery = value
    }

    fun getOnBoardingFulfillment() {
        if (verifyAvailability()) {
            viewModelScope.launch {
                setLoadingState()
                getOnBoardingFulfillmentUseCase()
                    .onSuccess {
                        checkEnablementOrEligibility(it)
                    }.onError {
                        setErrorState(it.apiException.message)
                    }.onEmpty {
                        setErrorState()
                    }
            }
        }
    }

    private fun verifyAvailability(): Boolean {
        if (isUnavailable) {
            setState(PixRouterUiState.Unavailable(featureToggleMenuPix?.statusMessage))
            return false
        }
        return true
    }

    private fun checkEnablementOrEligibility(onBoardingFulfillment: OnBoardingFulfillment) {
        val isBlocked =
            onBoardingFulfillment.isEnabled == false && onBoardingFulfillment.blockType != null
        if (onBoardingFulfillment.isEnabled == true || isBlocked) {
            checkAuthorizationStatus(onBoardingFulfillment)
        } else if (onBoardingFulfillment.isEligible == true) {
            checkAccreditation(onBoardingFulfillment)
        } else {
            setState(PixRouterUiState.NotEligible)
        }
    }

    private fun checkAccreditation(onBoardingFulfillment: OnBoardingFulfillment) {
        if (onBoardingFulfillment.status == PixStatus.WAITING_ACTIVATION) {
            setState(PixRouterUiState.AccreditationRequired)
        } else {
            setShowAuthorizationStatusState()
        }
    }

    private fun checkAuthorizationStatus(onBoardingFulfillment: OnBoardingFulfillment) {
        if (showDataQuery) {
            setShowAuthorizationStatusState()
        } else {
            checkStatus(onBoardingFulfillment)
        }
    }

    private fun checkStatus(onBoardingFulfillment: OnBoardingFulfillment) {
        if (onBoardingFulfillment.status == PixStatus.ACTIVE) {
            checkBlockType(onBoardingFulfillment)
        } else {
            setShowAuthorizationStatusState()
        }
    }

    private fun checkBlockType(onBoardingFulfillment: OnBoardingFulfillment) {
        when (onBoardingFulfillment.blockType) {
            BlockType.IN_PROGRESS ->
                setShowAuthorizationStatusState()

            BlockType.BANK_DOMICILE, BlockType.PENNY_DROP ->
                setState(PixRouterUiState.BlockPennyDrop)

            else ->
                checkProfileType(onBoardingFulfillment)
        }
    }

    private fun checkProfileType(onBoardingFulfillment: OnBoardingFulfillment) {
        when (onBoardingFulfillment.profileType) {
            ProfileType.LEGACY -> setShowPixExtractState(
                ProfileType.LEGACY,
                onBoardingFulfillment.pixAccount,
                onBoardingFulfillment.settlementScheduled
            )

            ProfileType.AUTOMATIC_TRANSFER -> setShowPixExtractState(
                ProfileType.AUTOMATIC_TRANSFER,
                onBoardingFulfillment.pixAccount,
                onBoardingFulfillment.settlementScheduled
            )

            ProfileType.FREE_MOVEMENT -> checkMfaEligibility(onBoardingFulfillment)
            ProfileType.PARTNER_BANK -> setState(PixRouterUiState.EnablePixPartner)
            else -> setErrorState()
        }
    }

    private fun checkMfaEligibility(onBoardingFulfillment: OnBoardingFulfillment) {
        mfaRepository.run {
            if (hasValidSeed()) {
                setTokenConfigurationRequiredState(onBoardingFulfillment)
                return
            }
            checkEligibility(object : APICallbackDefault<MfaEligibilityResponse, String> {
                override fun onSuccess(response: MfaEligibilityResponse) =
                    checkMerchantStatus(response.status, onBoardingFulfillment)

                override fun onError(error: ErrorMessage) =
                    setState(PixRouterUiState.MfaEligibilityError(error))
            })
        }
    }

    private fun checkMerchantStatus(status: String?, onBoardingFulfillment: OnBoardingFulfillment) {
        if (status == MerchantStatusMFA.ACTIVE.name) {
            setState(
                if (isOnBoardingViewed) {
                    PixRouterUiState.ShowPixHome(onBoardingFulfillment.pixAccount)
                } else {
                    PixRouterUiState.OnBoardingRequired(onBoardingFulfillment.pixAccount)
                }
            )
        } else {
            setTokenConfigurationRequiredState(onBoardingFulfillment)
        }
    }

    private fun setState(state: PixRouterUiState) {
        _uiState.postValue(state)
    }

    private fun setLoadingState() {
        setState(PixRouterUiState.Loading)
    }

    private fun setErrorState(message: String? = null) {
        setState(PixRouterUiState.Error(message))
    }

    private fun setShowAuthorizationStatusState() {
        setState(PixRouterUiState.ShowAuthorizationStatus)
    }

    private fun setTokenConfigurationRequiredState(onBoardingFulfillment: OnBoardingFulfillment) {
        setState(
            PixRouterUiState.TokenConfigurationRequired(
                isOnBoardingViewed,
                onBoardingFulfillment.pixAccount
            )
        )
    }

    private fun setShowPixExtractState(
        profileType: ProfileType,
        pixAccount: OnBoardingFulfillment.PixAccount?,
        settlementScheduled: OnBoardingFulfillment.SettlementScheduled?
    ) {
        setState(PixRouterUiState.ShowPixExtract(profileType, pixAccount, settlementScheduled))
    }

}