package br.com.mobicare.cielo.arv.presentation.anticipation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.arv.data.model.request.ArvConfirmAnticipationRequest
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.model.ArvBank
import br.com.mobicare.cielo.arv.domain.useCase.ConfirmArvAnticipationUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvBanksUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvSingleAnticipationWithFilterUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvSingleAnticipationWithValueNewUseCase
import br.com.mobicare.cielo.arv.utils.ArvConstants.CIELO_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.ArvConstants.MARKET_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.ArvConstants.SIMULATION_TYPE_VALUE
import br.com.mobicare.cielo.arv.utils.UiArvBanksState
import br.com.mobicare.cielo.arv.utils.UiArvConfirmAnticipationState
import br.com.mobicare.cielo.arv.utils.UiArvFeeLoadingState
import br.com.mobicare.cielo.arv.utils.UiArvLoadingState
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.NOT_ELIGIBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import kotlinx.coroutines.launch

class ArvSimulationViewModel(
    private val getArvBanksUseCase: GetArvBanksUseCase,
    private val confirmArvAnticipationUseCase: ConfirmArvAnticipationUseCase,
    private val arvAnticipationByBrandsUseCase: GetArvSingleAnticipationWithFilterUseCase,
    private val arvSingleAnticipationWithValueNewUseCase: GetArvSingleAnticipationWithValueNewUseCase,
    private val getUserObjUseCase: GetUserObjUseCase,
    private val userPreferences: UserPreferences,
) : ViewModel() {
    private val _banksLiveData = MutableLiveData<UiArvBanksState>()
    val banksLiveData: LiveData<UiArvBanksState> get() = _banksLiveData

    val userInformationLiveData by lazy(::getUserInformation)

    private val _arvSimulationDataMutableLiveData = MutableLiveData<ArvAnticipation>()
    val arvSimulationDataLiveData: LiveData<ArvAnticipation> get() = _arvSimulationDataMutableLiveData

    private val _arvSelectedBankMutableLiveData = MutableLiveData<ArvBank?>()
    val arvSelectedBankLiveData: LiveData<ArvBank?> get() = _arvSelectedBankMutableLiveData

    private val _arvConfirmAnticipationState = MutableLiveData<UiArvConfirmAnticipationState>()
    val arvConfirmAnticipationState: LiveData<UiArvConfirmAnticipationState> get() = _arvConfirmAnticipationState

    private val _arvLoadingState = MutableLiveData<UiArvLoadingState>()
    val arvLoadingState: LiveData<UiArvLoadingState> get() = _arvLoadingState

    private val _arvFeeLoadingState = MutableLiveData<UiArvFeeLoadingState>()
    val arvFeeLoadingState: LiveData<UiArvFeeLoadingState> get() = _arvFeeLoadingState

    private val _arvPurpleAlertState = MutableLiveData<Boolean>()
    val arvPurpleAlertState: LiveData<Boolean> get() = _arvPurpleAlertState

    init {
        getUserInformation()
        getArvBanks()
    }

    fun getUserInformation(): LiveData<Pair<String, String>> {
        val liveData = MutableLiveData<Pair<String, String>>()
        liveData.value = userPreferences.userInformation?.let {
            Pair(it.activeMerchant.tradingName.orEmpty(), it.activeMerchant.cnpj?.number.orEmpty())
        }
        return liveData
    }

    private fun getArvBanks() {
        viewModelScope.launch {
            _banksLiveData.value = UiArvBanksState.ShowLoadingArvBanks
            getArvBanksUseCase()
                .onSuccess {
                    _banksLiveData.value = UiArvBanksState.SuccessArvBanks(it)
                    _arvSelectedBankMutableLiveData.value = it.firstOrNull()
                    if (arvSelectedBankLiveData.value?.receiveToday == true) {
                        getArvFeeData(true)
                    } else {
                        updatePurpleAlertData(false)
                    }
                }
                .onError { apiError ->
                    val error = apiError.apiException.newErrorMessage
                    _arvSelectedBankMutableLiveData.value = null
                    newErrorHandler(
                        getUserObjUseCase = getUserObjUseCase,
                        newErrorMessage = error,
                        onErrorAction = {
                            _banksLiveData.value = UiArvBanksState.ShowTryAgain(error)
                        }
                    )
                }
        }
    }

    fun updateBanks() {
        getArvBanks()
    }

    fun updateSimulationData(simulationData: ArvAnticipation) {
        _arvSimulationDataMutableLiveData.value = simulationData
    }

    private fun updatePurpleAlertData(hasReceiveToday: Boolean) {
        _arvPurpleAlertState.value = hasReceiveToday
    }

    fun handleBankSelect(
        arvBank: ArvBank,
        hasReceiveTodayCurrentBank: Boolean?,
    ) {
        val hasReceiveTodaySelectedBank = arvBank.receiveToday
        if (hasReceiveTodaySelectedBank != hasReceiveTodayCurrentBank) {
            getArvFeeData(hasReceiveTodaySelectedBank)
        }
        updatePurpleAlertData(hasReceiveTodaySelectedBank)
        _arvSelectedBankMutableLiveData.value = arvBank
    }

    private fun getArvFeeData(
        receiveToday: Boolean?
    ) {
        _arvFeeLoadingState.value = UiArvFeeLoadingState.ShowLoading
        viewModelScope.launch {
            if (arvSimulationDataLiveData.value?.simulationType == SIMULATION_TYPE_VALUE) {
                arvSingleAnticipationWithValueNewUseCase.invoke(
                    negotiationType = arvSimulationDataLiveData.value?.negotiationType,
                    value = arvSimulationDataLiveData.value?.netAmount,
                    receiveToday = receiveToday,
                    initialDate = arvSimulationDataLiveData.value?.initialDate,
                    finalDate = arvSimulationDataLiveData.value?.finalDate,
                )
                    .onSuccess { response ->
                        _arvSimulationDataMutableLiveData.value = response
                        _arvFeeLoadingState.value = UiArvFeeLoadingState.HideLoading
                    }
                    .onError { error ->
                        handleApiError(error, receiveToday)
                    }
            } else {
                getArvAnticipationByBrandsData(receiveToday)
                    .onSuccess { response ->
                        handleArvFeeSuccess(response)
                    }
                    .onError { error ->
                        handleApiError(error, receiveToday)
                    }
            }
        }
    }

    private suspend fun getArvAnticipationByBrandsData(receiveToday: Boolean?) =
        arvAnticipationByBrandsUseCase.invoke(
            negotiationType = arvSimulationDataLiveData.value?.negotiationType,
            initialDate = arvSimulationDataLiveData.value?.initialDate,
            endDate = arvSimulationDataLiveData.value?.finalDate,
            brandCodes = if (arvSimulationDataLiveData.value?.negotiationType == CIELO_NEGOTIATION_TYPE) {
                arvSimulationDataLiveData.value?.acquirers?.first()?.cardBrands?.filter {
                    it.isSelected
                }?.mapNotNull { it.code }
            } else {
                null
            },
            acquirerCode = if (arvSimulationDataLiveData.value?.negotiationType == MARKET_NEGOTIATION_TYPE) {
                arvSimulationDataLiveData.value?.acquirers?.filterNotNull()?.filter {
                    it.isSelected
                }?.mapNotNull { it.code }
            } else {
                null
            },
            receiveToday = receiveToday
        )

    private fun handleArvFeeSuccess(response: ArvAnticipation) {
        val arvSimulationData: ArvAnticipation? = arvSimulationDataLiveData.value
        if (arvSimulationData != null) {
            _arvSimulationDataMutableLiveData.value = response.appendNotSelected(arvSimulationData)
        } else {
            _arvSimulationDataMutableLiveData.value = response
        }
        _arvFeeLoadingState.value = UiArvFeeLoadingState.HideLoading
    }

    private suspend fun handleApiError(error: CieloDataResult.APIError, receiveToday: Boolean?) {
        val errorMessage = error.apiException.newErrorMessage
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = errorMessage,
            onHideLoading = {
                _arvFeeLoadingState.value = UiArvFeeLoadingState.HideLoading
            }
        ) {
            _arvFeeLoadingState.value = UiArvFeeLoadingState.Error(
                error = errorMessage,
                onErrorAction = { getArvFeeData(receiveToday) }
            )
        }
    }

    fun confirmAnticipation(arvToken: String, arvBank: ArvBank) {
        viewModelScope.launch {
            _arvLoadingState.value = UiArvLoadingState.ShowLoading
            confirmArvAnticipationUseCase(
                ArvConfirmAnticipationRequest(
                    token = arvToken,
                    code = arvBank.code ?: EMPTY,
                    agency = arvBank.agency ?: EMPTY,
                    account = arvBank.account ?: EMPTY,
                    accountDigit = arvBank.accountDigit ?: EMPTY
                )
            ).onSuccess {
                _arvConfirmAnticipationState.value = UiArvConfirmAnticipationState.Success(it)
            }.onEmpty {
                setConfirmAnticipationError()
            }.onError {
                val error = it.apiException.newErrorMessage
                newErrorHandler(
                    getUserObjUseCase = getUserObjUseCase,
                    newErrorMessage = error,
                    onHideLoading = {
                        _arvLoadingState.value = UiArvLoadingState.HideLoading
                    },
                    onErrorAction = {
                        setConfirmAnticipationError(error)
                    }
                )
            }
        }
    }

    private fun setConfirmAnticipationError(error: NewErrorMessage? = null) {
        _arvConfirmAnticipationState.value = when {
            error?.flagErrorCode?.contains(OTP) == true -> UiArvConfirmAnticipationState.ErrorToken(error)
            error?.flagErrorCode?.contains(NOT_ELIGIBLE) == true -> UiArvConfirmAnticipationState.ErrorNotEligible(error)
            else -> UiArvConfirmAnticipationState.Error(error)
        }
    }

    fun selectedAcquirersNameList(): List<String>? {
        return arvSimulationDataLiveData.value?.acquirers?.map { it?.name.orEmpty() }
    }

    fun selectedBrandsNameList(): List<String>? {
        val brands =
            arvSimulationDataLiveData.value?.acquirers?.flatMap {
                it?.cardBrands?.filter { itCardBrand ->
                    itCardBrand.isSelected
                }.orEmpty()
            }?.map { it.name.orEmpty() }
        return brands
    }

}
