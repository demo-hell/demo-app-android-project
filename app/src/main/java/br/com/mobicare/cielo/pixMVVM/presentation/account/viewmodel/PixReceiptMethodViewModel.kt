package br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.pixMVVM.domain.enums.ProfileType
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetOnBoardingFulfillmentUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.account.enums.PixReceiptMethod
import br.com.mobicare.cielo.pixMVVM.presentation.account.utils.PixReceiptMethodUiState
import kotlinx.coroutines.launch

class PixReceiptMethodViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getOnBoardingFulfillmentUseCase: GetOnBoardingFulfillmentUseCase,
    private val getFeatureTogglePreferenceUseCase: GetFeatureTogglePreferenceUseCase,
) : ViewModel() {
    private val _uiState = MutableLiveData<PixReceiptMethodUiState>()
    val uiState: LiveData<PixReceiptMethodUiState> get() = _uiState

    private var _onBoardingFulfillment: OnBoardingFulfillment? = null
    val onBoardingFulfillment get() = _onBoardingFulfillment

    private var _activeReceiptMethod: PixReceiptMethod? = null
    val activeReceiptMethod get() = _activeReceiptMethod

    val isSuccessState get() = uiState.value is PixReceiptMethodUiState.Success

    private var _ftScheduledTransferEnable = false
    val ftScheduledTransferEnable get() = _ftScheduledTransferEnable

    private val _ftTransferScheduledBalanceEnabled = MutableLiveData(false)
    val ftTransferScheduledBalanceEnabled: LiveData<Boolean> get() = _ftTransferScheduledBalanceEnabled

    fun getOnBoardingFulfillment() {
        viewModelScope.launch {
            setState(PixReceiptMethodUiState.Loading)
            getFeatureToggleScheduledTransferEnable()
        }
    }

    fun fetchFeatureToggleTransferScheduledBalance() {
        viewModelScope.launch {
            getFeatureTogglePreferenceUseCase(
                FeatureTogglePreference.PIX_SHOW_BUTTON_TRANSFER_SCHEDULED_BALANCE
            ).onSuccess { result ->
                _ftTransferScheduledBalanceEnabled.postValue(result)
            }
        }
    }

    private suspend fun getFeatureToggleScheduledTransferEnable() {
        getFeatureTogglePreferenceUseCase.invoke(FeatureTogglePreference.PIX_BUTTON_CHANGE_TYPE_ACCOUNT_TO_SCHEDULED_TRANSFER)
            .onSuccess { result ->
                _ftScheduledTransferEnable = result
                fetchOnBoardingFulfillment()
            }
    }

    private suspend fun fetchOnBoardingFulfillment() {
        getOnBoardingFulfillmentUseCase()
            .onSuccess {
                setOnBoardingFulfillment(it)
                setState(PixReceiptMethodUiState.Success)
            }.onError {
                handleError(it.apiException.newErrorMessage)
            }.onEmpty {
                setState(PixReceiptMethodUiState.Error)
            }
    }

    private fun setOnBoardingFulfillment(onBoardingFulfillment: OnBoardingFulfillment) {
        _onBoardingFulfillment = onBoardingFulfillment
        setActiveReceiptMethod()
    }

    private fun setActiveReceiptMethod() {
        _activeReceiptMethod =
            when {
                isCieloAccount -> PixReceiptMethod.CIELO_ACCOUNT
                isTransferBySale -> PixReceiptMethod.TRANSFER_BY_SALE
                isScheduledTransfer -> PixReceiptMethod.SCHEDULED_TRANSFER
                else -> null
            }
    }

    private suspend fun handleError(error: NewErrorMessage) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onErrorAction = { setState(PixReceiptMethodUiState.Error) },
        )
    }

    private val isCieloAccount get() =
        onBoardingFulfillment?.profileType == ProfileType.FREE_MOVEMENT

    private val isTransferBySale get() =
        onBoardingFulfillment?.let {
            it.profileType == ProfileType.AUTOMATIC_TRANSFER &&
                (it.settlementScheduled?.isEnabled == null || it.settlementScheduled.isEnabled == false)
        }.ifNull { false }

    private val isScheduledTransfer get() =
        onBoardingFulfillment?.let {
            it.profileType == ProfileType.AUTOMATIC_TRANSFER && it.settlementScheduled?.isEnabled == true
        }.ifNull { false }

    private fun setState(state: PixReceiptMethodUiState) {
        _uiState.postValue(state)
    }
}
