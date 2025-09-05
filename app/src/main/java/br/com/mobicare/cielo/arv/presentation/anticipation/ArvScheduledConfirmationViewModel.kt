package br.com.mobicare.cielo.arv.presentation.anticipation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_AUTOMATIC_CONFIRMATION
import br.com.mobicare.cielo.arv.data.model.request.ArvConfirmScheduledAnticipationRequest
import br.com.mobicare.cielo.arv.data.model.request.ArvScheduledAnticipationContractRequest
import br.com.mobicare.cielo.arv.data.model.request.NegotiationType
import br.com.mobicare.cielo.arv.domain.model.ArvBank
import br.com.mobicare.cielo.arv.domain.model.ArvScheduledAnticipation
import br.com.mobicare.cielo.arv.domain.useCase.ConfirmArvScheduledAnticipationUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvScheduledContractUseCase
import br.com.mobicare.cielo.arv.utils.UiArvConfirmScheduledAnticipationState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.NOT_ELIGIBLE
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import kotlinx.coroutines.launch

class ArvScheduledConfirmationViewModel(
    private val getArvScheduledContractUseCase: GetArvScheduledContractUseCase,
    private val confirmArvScheduledAnticipationUseCase: ConfirmArvScheduledAnticipationUseCase,
    private val getUserObjUseCase: GetUserObjUseCase,
    private val userPreferences: UserPreferences,
    private val arvAnalytics: ArvAnalyticsGA4

) : ViewModel() {

    val userInformationLiveData by lazy(::getUserInformation)

    private val _selectedBankMutableLiveData = MutableLiveData<ArvBank>()
    val selectedBank: LiveData<ArvBank> get() = _selectedBankMutableLiveData

    private val _arvScheduledAnticipationMutableLiveData =
        MutableLiveData<ArvScheduledAnticipation>()
    val arvScheduledAnticipationLiveData: LiveData<ArvScheduledAnticipation> get() = _arvScheduledAnticipationMutableLiveData

    private val _arvConfirmScheduledAnticipationState =
        MutableLiveData<UiArvConfirmScheduledAnticipationState>()
    val arvConfirmScheduledAnticipationState: LiveData<UiArvConfirmScheduledAnticipationState> get() = _arvConfirmScheduledAnticipationState

    init {
        getUserInformation()
    }

    fun getUserInformation(): LiveData<Pair<String, String>> {
        val liveData = MutableLiveData<Pair<String, String>>()
        liveData.value = userPreferences.userInformation?.let {
            Pair(it.activeMerchant.tradingName.orEmpty(), it.activeMerchant.cnpj?.number.orEmpty())
        }
        return liveData
    }

    fun updateAnticipationDataUpdateBankData(bank: ArvBank) {
        _selectedBankMutableLiveData.value = bank
    }

    fun updateAnticipationData(anticipation: ArvScheduledAnticipation) {
        _arvScheduledAnticipationMutableLiveData.value = anticipation
    }


    fun confirmAnticipation() {
        val arvScheduledAnticipation = arvScheduledAnticipationLiveData.value ?: return
        viewModelScope.launch {
            _arvConfirmScheduledAnticipationState.value =
                UiArvConfirmScheduledAnticipationState.ShowLoading
            confirmArvScheduledAnticipationUseCase(
                ArvConfirmScheduledAnticipationRequest(
                    token = arvScheduledAnticipation.token,
                    rateSchedules = arvScheduledAnticipation.rateSchedules,
                    domicile = selectedBank.value
                )
            ).onSuccess {
                _arvConfirmScheduledAnticipationState.value = UiArvConfirmScheduledAnticipationState.HideLoading
                _arvConfirmScheduledAnticipationState.value = UiArvConfirmScheduledAnticipationState.Success
            }.onEmpty {
                _arvConfirmScheduledAnticipationState.value = UiArvConfirmScheduledAnticipationState.HideLoading
                _arvConfirmScheduledAnticipationState.value = UiArvConfirmScheduledAnticipationState.Success
            }.onError {
                val error = it.apiException.newErrorMessage
                newErrorHandler(
                    getUserObjUseCase = getUserObjUseCase,
                    newErrorMessage = error,
                    onHideLoading = {
                        _arvConfirmScheduledAnticipationState.value =
                            UiArvConfirmScheduledAnticipationState.HideLoading
                    },
                    onErrorAction = {
                        setConfirmAnticipationError(error)
                    }
                )
            }
        }
    }

    private fun setConfirmAnticipationError(error: NewErrorMessage? = null) {
        _arvConfirmScheduledAnticipationState.value = UiArvConfirmScheduledAnticipationState.HideLoading
        _arvConfirmScheduledAnticipationState.value =
            when {
                error?.flagErrorCode?.contains(OTP) == true -> UiArvConfirmScheduledAnticipationState.ErrorToken(error)
                error?.flagErrorCode?.contains(NOT_ELIGIBLE) == true -> UiArvConfirmScheduledAnticipationState.ErrorNotEligible(error)
                else -> UiArvConfirmScheduledAnticipationState.Error(error)
            }
    }

    suspend fun getArvScheduledContract(
        negotiationType: String
    ): String? {
        getArvScheduledContractUseCase(
            ArvScheduledAnticipationContractRequest(NegotiationType.valueOf(negotiationType))
        ).onSuccess {
            return it.file
        }.onEmpty {
            return null
        }.onError {
            val error = it.apiException.newErrorMessage
            newErrorHandler(
                getUserObjUseCase = getUserObjUseCase,
                newErrorMessage = error,
                onErrorAction = {
                    arvAnalytics.logException(
                        SCREEN_VIEW_ARV_AUTOMATIC_CONFIRMATION,
                        error
                    )
                }
            )
        }
        return null
    }

}
