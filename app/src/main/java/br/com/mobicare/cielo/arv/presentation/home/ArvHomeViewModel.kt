package br.com.mobicare.cielo.arv.presentation.home

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.data.model.request.ArvHistoricRequest
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.model.ArvScheduledAnticipation
import br.com.mobicare.cielo.arv.domain.useCase.GetArvAnticipationHistoryNewUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvOptInUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvScheduledAnticipationUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvSingleAnticipationWithDateNewUseCase
import br.com.mobicare.cielo.arv.presentation.home.utils.ArvWhatsAppContactData
import br.com.mobicare.cielo.arv.utils.ARVUtils
import br.com.mobicare.cielo.arv.utils.ArvConstants.CIELO_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.ArvConstants.CLOSED_MARKET
import br.com.mobicare.cielo.arv.utils.ArvConstants.CORPORATE_DESK
import br.com.mobicare.cielo.arv.utils.ArvConstants.MARKET_NEGOTIATION_TYPE
import br.com.mobicare.cielo.arv.utils.ArvConstants.MERCHANT_NOT_ELIGIBLE
import br.com.mobicare.cielo.arv.utils.ArvConstants.NONEXISTENT_RECEIVABLES_ANTICIPATION
import br.com.mobicare.cielo.arv.utils.OptInState
import br.com.mobicare.cielo.arv.utils.UiArvHistoricState
import br.com.mobicare.cielo.arv.utils.UiArvHomeState
import br.com.mobicare.cielo.arv.utils.UiArvScheduledAnticipationState
import br.com.mobicare.cielo.arv.utils.UiArvScheduledMarketFeatureToggleState
import br.com.mobicare.cielo.arv.utils.UiArvSingleState
import br.com.mobicare.cielo.arv.utils.UiArvTypeState
import br.com.mobicare.cielo.arv.utils.UiArvUserState
import br.com.mobicare.cielo.commons.constants.HTTP_UNKNOWN
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetConfigurationUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetMeInformationUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.EMPTY_VALUE
import br.com.mobicare.cielo.commons.utils.getErrorMessage
import br.com.mobicare.cielo.commons.utils.isRoot
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ArvHomeViewModel(
    private val getArvOptInUseCase: GetArvOptInUseCase,
    private val getMeInformationUseCase: GetMeInformationUseCase,
    private val arvAnticipationUseCase: GetArvSingleAnticipationWithDateNewUseCase,
    private val arvScheduledAnticipationUseCase: GetArvScheduledAnticipationUseCase,
    private val arvAnticipationHistoryUseCase: GetArvAnticipationHistoryNewUseCase,
    private val getFeatureTogglePreference: GetFeatureTogglePreferenceUseCase,
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getConfigurationUseCase: GetConfigurationUseCase,
) : ViewModel() {
    private val _arvAnticipationTypeMutableLiveData = MutableLiveData<UiArvTypeState>()
    val arvAnticipationTypeLiveData: LiveData<UiArvTypeState> get() = _arvAnticipationTypeMutableLiveData

    private val _arvHomeAnticipationMutableLiveData = MutableLiveData<UiArvHomeState>()
    val arvHomeSingleAnticipationLiveData: LiveData<UiArvHomeState> get() = _arvHomeAnticipationMutableLiveData

    private val _arvCieloAnticipationMutableLiveData = MutableLiveData<UiArvSingleState>()
    val arvCieloAnticipationLiveData: LiveData<UiArvSingleState> get() = _arvCieloAnticipationMutableLiveData

    private val _arvMarketAnticipationMutableLiveData = MutableLiveData<UiArvSingleState>()
    val arvMarketAnticipationLiveData: LiveData<UiArvSingleState> get() = _arvMarketAnticipationMutableLiveData

    private val _arvScheduledAnticipationMutableLiveData = MutableLiveData<UiArvHomeState>()
    val arvScheduledAnticipationLiveData: LiveData<UiArvHomeState> get() = _arvScheduledAnticipationMutableLiveData

    private val _arvUserStateMutableLiveData = MutableLiveData<UiArvUserState>()
    val arvUserState: LiveData<UiArvUserState> get() = _arvUserStateMutableLiveData

    private val _arvScheduledAnticipationStateMutableLiveData =
        MutableLiveData<UiArvScheduledAnticipationState>()
    val arvScheduledAnticipationStateLiveData: LiveData<UiArvScheduledAnticipationState> get() = _arvScheduledAnticipationStateMutableLiveData

    private val _arvAnticipationHistoryMutableLiveData = MutableLiveData<UiArvHistoricState>()
    val arvAnticipationHistoryLiveData: LiveData<UiArvHistoricState> get() = _arvAnticipationHistoryMutableLiveData

    private val _arvOptInMutableLiveData = MutableLiveData<OptInState>()
    val arvOptInStateLiveData: LiveData<OptInState> get() = _arvOptInMutableLiveData

    private val _arvMarketScheduledAnticipationFeatureToggleMutableLiveData = MutableLiveData<UiArvScheduledMarketFeatureToggleState>()
    val arvMarketScheduledAnticipationFeatureToggleLiveData: LiveData<UiArvScheduledMarketFeatureToggleState> get() = _arvMarketScheduledAnticipationFeatureToggleMutableLiveData

    private val _arvWhatsAppContactData = MutableLiveData<ArvWhatsAppContactData>()
    val arvWhatsAppContactData: LiveData<ArvWhatsAppContactData> get() = _arvWhatsAppContactData

    private var isRoot = false

    private val initDate = ARVUtils.minAnticipationRangeDate.formatDateToAPI()
    private val endDate = ARVUtils.maxAnticipationRangeDate.formatDateToAPI()

    suspend fun getAnticipationScheduledFeatureToggle() {
        getFeatureTogglePreference(key = FeatureTogglePreference.ANTECIPE_VENDAS_CARD_PROGRAMADA).onSuccess {
            if (!it) {
                _arvScheduledAnticipationStateMutableLiveData.value =
                    UiArvScheduledAnticipationState.DisabledScheduled
            }
        }
    }

    private suspend fun getSingleAnticipationMarketFeatureToggle() {
        getFeatureTogglePreference(key = FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_AVULSA).onSuccess {
            if (!it) {
                _arvMarketAnticipationMutableLiveData.value =
                    UiArvSingleState.Disabled
            }
        }
    }

    private suspend fun getScheduledAnticipationMarketFeatureToggle() {
        getFeatureTogglePreference(key = FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_PROGRAMADA).onSuccess {
            if (!it) {
                _arvMarketScheduledAnticipationFeatureToggleMutableLiveData.value =
                    UiArvScheduledMarketFeatureToggleState.Disabled
            }
        }
    }

    fun getOptIn() {
        viewModelScope.launch {
            _arvAnticipationHistoryMutableLiveData.value = UiArvHistoricState.ShowLoadingHistoric
            _arvHomeAnticipationMutableLiveData.value = UiArvHomeState.ShowLoadingArvNegotiation
            _arvScheduledAnticipationMutableLiveData.value =
                UiArvHomeState.ShowLoadingArvScheduledAnticipation
            getArvOptInUseCase.invoke().onSuccess {
                when (it.eligible) {
                    true -> {
                        _arvOptInMutableLiveData.value = OptInState.MissingOptIn
                    }
                    false -> {
                        fetchData()
                        getArvAnticipationHistory()
                    }
                    else -> {
                        _arvHomeAnticipationMutableLiveData.value =
                            UiArvHomeState.ErrorArvNegotiation(R.string.anticipation_error)
                    }
                }
            }.onError {
                val error = it.apiException.newErrorMessage
                val message =
                    if (error.httpCode == HTTP_UNKNOWN) {
                        error.message
                    } else {
                        when (error.actionErrorType) {
                            ActionErrorTypeEnum.NETWORK_ERROR -> R.string.anticipation_error
                            else ->
                                error.getErrorMessage(
                                    R.string.anticipation_error,
                                )
                        }
                    }
                newErrorHandler(
                    getUserObjUseCase = getUserObjUseCase,
                    newErrorMessage = error,
                    onErrorAction = {
                        _arvHomeAnticipationMutableLiveData.value =
                            UiArvHomeState.ErrorArvNegotiation(message, error)
                    },
                )
            }.onEmpty {
                _arvHomeAnticipationMutableLiveData.value =
                    UiArvHomeState.ErrorArvNegotiation(R.string.anticipation_error)
            }
        }
    }

    private fun fetchData() {
        viewModelScope.launch {
            getAnticipationSingleFeatureToggle()
            getAnticipationScheduledFeatureToggle()
            getSingleAnticipationMarketFeatureToggle()
            getScheduledAnticipationMarketFeatureToggle()

            val isScheduledDisabled =
                arvScheduledAnticipationStateLiveData.value is UiArvScheduledAnticipationState.DisabledScheduled
            val isSingleDisabled =
                (arvAnticipationTypeLiveData.value as? UiArvTypeState.SetupAnticipationSingle)?.anticipationSingleEnable == false
            val isSingleMarketDisabled =
                (arvMarketAnticipationLiveData.value is UiArvSingleState.Disabled)

            val single =
                if (isSingleDisabled.not()) {
                    Pair(
                        async {
                            arvAnticipationUseCase.invoke(
                                negotiationType = CIELO_NEGOTIATION_TYPE,
                                initialDate = initDate,
                                endDate = endDate
                            )
                        },
                        async {
                            if (isSingleMarketDisabled.not()) {
                                arvAnticipationUseCase.invoke(
                                    negotiationType = MARKET_NEGOTIATION_TYPE,
                                    initialDate = initDate,
                                    endDate = endDate
                                )
                            } else {
                                null
                            }
                        },
                    )
                } else {
                    null
                }
            val scheduled =
                if (isScheduledDisabled.not()) async { arvScheduledAnticipationUseCase.invoke() } else null

            combineResults(single?.first?.await(), single?.second?.await(), scheduled?.await())
        }
    }

    private suspend fun combineResults(
        singleAnticipationResult: CieloDataResult<ArvAnticipation>?,
        marketAnticipationResult: CieloDataResult<ArvAnticipation>?,
        scheduledAnticipationResult: CieloDataResult<ArvScheduledAnticipation>?,
    ) {
        _arvHomeAnticipationMutableLiveData.value = UiArvHomeState.HideLoadingArvNegotiation
        singleAnticipationResult?.onSuccess { anticipation ->
            _arvCieloAnticipationMutableLiveData.value =
                anticipation.let {
                    UiArvSingleState.SuccessArvSingle(it)
                }
        }?.onEmpty {
            _arvHomeAnticipationMutableLiveData.value =
                UiArvHomeState.ErrorArvNegotiation(R.string.anticipation_error)
        }?.onError {
            val error = it.apiException.newErrorMessage

            when (error.flagErrorCode) {
                CLOSED_MARKET ->
                    _arvHomeAnticipationMutableLiveData.value =
                        UiArvHomeState.ClosedMarket

                CORPORATE_DESK ->
                    _arvHomeAnticipationMutableLiveData.value =
                        UiArvHomeState.CorporateDesk

                MERCHANT_NOT_ELIGIBLE ->
                    _arvHomeAnticipationMutableLiveData.value =
                        UiArvHomeState.NotEligible(error)

                NONEXISTENT_RECEIVABLES_ANTICIPATION ->
                    _arvCieloAnticipationMutableLiveData.value =
                        UiArvSingleState.NoValuesToAnticipate

                else -> {
                    val message = if (error.httpCode == HTTP_UNKNOWN) error.message else R.string.anticipation_error
                    newErrorHandler(
                        getUserObjUseCase = getUserObjUseCase,
                        newErrorMessage = error,
                        onErrorAction = {
                            _arvHomeAnticipationMutableLiveData.value =
                                UiArvHomeState.ErrorArvNegotiation(message, error)
                        },
                    )
                }
            }
        }

        marketAnticipationResult?.onSuccess { anticipation ->
            _arvMarketAnticipationMutableLiveData.value =
                anticipation.let {
                    UiArvSingleState.SuccessArvSingle(it)
                }
        }?.onEmpty {
            _arvHomeAnticipationMutableLiveData.value =
                UiArvHomeState.ErrorArvNegotiation(R.string.anticipation_error)
        }?.onError {
            val error = it.apiException.newErrorMessage

            when (error.flagErrorCode) {
                CLOSED_MARKET ->
                    _arvHomeAnticipationMutableLiveData.value =
                        UiArvHomeState.ClosedMarket

                CORPORATE_DESK ->
                    _arvHomeAnticipationMutableLiveData.value =
                        UiArvHomeState.CorporateDesk

                MERCHANT_NOT_ELIGIBLE ->
                    _arvHomeAnticipationMutableLiveData.value =
                        UiArvHomeState.NotEligible(error)

                NONEXISTENT_RECEIVABLES_ANTICIPATION -> {
                    _arvMarketAnticipationMutableLiveData.value =
                        UiArvSingleState.NoValuesToAnticipate
                }

                else -> {
                    val message = if (error.httpCode == HTTP_UNKNOWN) error.message else R.string.anticipation_error
                    newErrorHandler(
                        getUserObjUseCase = getUserObjUseCase,
                        newErrorMessage = error,
                        onErrorAction = {
                            _arvHomeAnticipationMutableLiveData.value =
                                UiArvHomeState.ErrorArvNegotiation(message, error)
                        },
                    )
                }
            }
        }

        if (_arvCieloAnticipationMutableLiveData.value is UiArvSingleState.NoValuesToAnticipate &&
            (
                _arvMarketAnticipationMutableLiveData.value in
                    listOf(UiArvSingleState.NoValuesToAnticipate, UiArvSingleState.Disabled)
            )
        ) {
            _arvHomeAnticipationMutableLiveData.value = UiArvHomeState.NoValuesToAnticipate
        }

        _arvScheduledAnticipationMutableLiveData.value =
            UiArvHomeState.HideLoadingArvScheduledAnticipation
        scheduledAnticipationResult?.onSuccess { anticipation ->
            _arvScheduledAnticipationMutableLiveData.value =
                anticipation.let {
                    scheduledAnticipationScenarioUpdate(it)
                    UiArvHomeState.SuccessArvScheduledNegotiation(it)
                }
        }?.onEmpty {
            _arvScheduledAnticipationMutableLiveData.value =
                UiArvHomeState.ErrorArvNegotiation(R.string.anticipation_error)
        }?.onError {
            val error = it.apiException.newErrorMessage
            _arvScheduledAnticipationMutableLiveData.value =
                UiArvHomeState.HideLoadingArvScheduledAnticipation

            when (error.flagErrorCode) {
                CORPORATE_DESK ->
                    _arvHomeAnticipationMutableLiveData.value =
                        UiArvHomeState.CorporateDesk

                MERCHANT_NOT_ELIGIBLE ->
                    _arvHomeAnticipationMutableLiveData.value =
                        UiArvHomeState.NotEligible(error)

                else -> {
                    val message = if (error.httpCode == HTTP_UNKNOWN) error.message else R.string.anticipation_error
                    newErrorHandler(
                        getUserObjUseCase = getUserObjUseCase,
                        newErrorMessage = error,
                        onErrorAction = {
                            _arvHomeAnticipationMutableLiveData.value =
                                UiArvHomeState.ErrorArvNegotiation(message, error)
                        },
                    )
                }
            }
        }
    }

    private fun checkFeatureToggleValue(isEnable: Boolean): Int {
        return if (isEnable) {
            R.drawable.background_stroke_cloud_200_8dp_radius
        } else {
            R.drawable.background_stroke_cloud_50_8dp_radius
        }
    }

    @VisibleForTesting
    suspend fun getAnticipationSingleFeatureToggle() {
        getFeatureTogglePreference(key = FeatureTogglePreference.ANTECIPE_VENDAS_CARD_AVULSA).onSuccess {
            _arvAnticipationTypeMutableLiveData.value =
                UiArvTypeState.SetupAnticipationSingle(
                    anticipationSingleEnable = it,
                    anticipationSingleBackground = checkFeatureToggleValue(it),
                )
        }
    }

    fun getUserInformation(isLocal: Boolean = true) {
        _arvUserStateMutableLiveData.value = UiArvUserState.ShowLoadingMeInformation
        viewModelScope.launch {
            getMeInformationUseCase(isLocal).onSuccess { me ->
                isRoot = isRoot()
                _arvUserStateMutableLiveData.value =
                    UiArvUserState.SuccessMeInformation(
                        name = me.activeMerchant.tradingName.orEmpty(),
                        numberEstablishment = me.activeMerchant.id,
                        cpnjEstablishment = me.activeMerchant.cnpj?.number.orEmpty(),
                    )
            }.onEmpty {
                _arvUserStateMutableLiveData.value =
                    UiArvUserState.ErrorMeInformation(R.string.anticipation_me_information_error)
            }.onError { error ->
                _arvUserStateMutableLiveData.value = UiArvUserState.HideLoadingMeInformation
                val message =
                    error.apiException.newErrorMessage.getErrorMessage(R.string.anticipation_me_information_error)
                newErrorHandler(
                    getUserObjUseCase = getUserObjUseCase,
                    newErrorMessage = error.apiException.newErrorMessage,
                    onErrorAction = {
                        _arvUserStateMutableLiveData.value =
                            UiArvUserState.ErrorMeInformation(message)
                    },
                )
            }
        }
    }

    fun getArvAnticipationHistory() {
        _arvAnticipationHistoryMutableLiveData.value =
            UiArvHistoricState.ShowLoadingHistoric
        viewModelScope.launch {
            arvAnticipationHistoryUseCase(ArvHistoricRequest()).onSuccess {
                _arvAnticipationHistoryMutableLiveData.value =
                    UiArvHistoricState.HideLoadingHistoric

                _arvAnticipationHistoryMutableLiveData.value =
                    it.items?.firstOrNull()?.let { item ->
                        UiArvHistoricState.SuccessHistoric(item)
                    } ?: UiArvHistoricState.EmptyHistoric
            }.onEmpty {
                _arvAnticipationHistoryMutableLiveData.value =
                    UiArvHistoricState.HideLoadingHistoric

                _arvAnticipationHistoryMutableLiveData.value = UiArvHistoricState.EmptyHistoric
            }.onError { error ->
                _arvAnticipationHistoryMutableLiveData.value =
                    UiArvHistoricState.HideLoadingHistoric
                val message = R.string.anticipation_historic_error
                newErrorHandler(
                    getUserObjUseCase = getUserObjUseCase,
                    newErrorMessage = error.apiException.newErrorMessage,
                    onErrorAction = {
                        _arvAnticipationHistoryMutableLiveData.value =
                            UiArvHistoricState.ErrorHistoric(message)
                    },
                )
            }
        }
    }

    private fun scheduledAnticipationScenarioUpdate(scheduledAnticipation: ArvScheduledAnticipation) {
        var hasCielo = false
        var hasMarket = false

        var cieloWasHiredByRoot = false
        var marketWasHiredByRoot = false

        scheduledAnticipation.rateSchedules?.forEach {
            when (it?.name) {
                CIELO_NEGOTIATION_TYPE -> {
                    hasCielo = it.schedule == true
                    cieloWasHiredByRoot = it.cnpjRoot == true
                }

                MARKET_NEGOTIATION_TYPE -> {
                    hasMarket = it.schedule == true
                    marketWasHiredByRoot = it.cnpjRoot == true
                }
            }
        }

        val notHired =
            !hasMarket && !hasCielo && !cieloWasHiredByRoot && !marketWasHiredByRoot
        if (notHired) {
            _arvScheduledAnticipationStateMutableLiveData.value =
                UiArvScheduledAnticipationState.NotHired
            return
        }

        val fullHiredByRoot = !isRoot && cieloWasHiredByRoot && marketWasHiredByRoot

        if (fullHiredByRoot) {
            _arvScheduledAnticipationStateMutableLiveData.value =
                UiArvScheduledAnticipationState.FullHiredByRoot
            return
        }

        val cieloOnlyHiredByRoot = !isRoot && cieloWasHiredByRoot && !hasMarket
        if (cieloOnlyHiredByRoot) {
            _arvScheduledAnticipationStateMutableLiveData.value =
                UiArvScheduledAnticipationState.CieloOnlyHiredByRoot
            return
        }

        val marketOnlyHiredByRoot = !isRoot && marketWasHiredByRoot && !hasCielo
        if (marketOnlyHiredByRoot) {
            _arvScheduledAnticipationStateMutableLiveData.value =
                UiArvScheduledAnticipationState.MarketOnlyHiredByRoot
            return
        }

        val marketByRootCieloByBranch = !isRoot && marketWasHiredByRoot && hasCielo
        if (marketByRootCieloByBranch) {
            _arvScheduledAnticipationStateMutableLiveData.value =
                UiArvScheduledAnticipationState.MarketByRootCieloByBranch
            return
        }

        val cieloByRootMarketByBranch = !isRoot && cieloWasHiredByRoot && hasMarket
        if (cieloByRootMarketByBranch) {
            _arvScheduledAnticipationStateMutableLiveData.value =
                UiArvScheduledAnticipationState.CieloByRootMarketByBranch
            return
        }

        val fullHired = hasCielo && hasMarket
        if (fullHired) {
            _arvScheduledAnticipationStateMutableLiveData.value =
                UiArvScheduledAnticipationState.FullHired
            return
        }

        val cieloOnlyHired = hasCielo && !hasMarket
        if (cieloOnlyHired) {
            _arvScheduledAnticipationStateMutableLiveData.value =
                UiArvScheduledAnticipationState.CieloOnlyHired
            return
        }

        val marketOnlyHired = !hasCielo && hasMarket
        if (marketOnlyHired) {
            _arvScheduledAnticipationStateMutableLiveData.value =
                UiArvScheduledAnticipationState.MarketOnlyHired
            return
        }
    }

    fun fetchWhatsAppContactData() {
        viewModelScope.launch {
            getConfigurationUseCase(ARV_WA_CONTACT_DATA, EMPTY_VALUE)
                .onSuccess { data ->
                    _arvWhatsAppContactData.value =
                        if (data.isNotBlank()) {
                            ArvWhatsAppContactData.fromJson(data)
                        } else {
                            ArvWhatsAppContactData()
                        }
                }.onEmpty {
                    _arvWhatsAppContactData.value = ArvWhatsAppContactData()
                }
        }
    }

    private companion object {
        const val ARV_WA_CONTACT_DATA = "ARV_WA_CONTACT_DATA"
    }
}
