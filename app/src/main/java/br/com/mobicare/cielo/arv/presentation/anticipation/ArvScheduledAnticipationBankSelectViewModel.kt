package br.com.mobicare.cielo.arv.presentation.anticipation

import br.com.mobicare.cielo.arv.domain.model.ArvBank
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.arv.domain.model.ArvScheduledAnticipation
import br.com.mobicare.cielo.arv.domain.model.Schedules
import br.com.mobicare.cielo.arv.domain.useCase.GetArvBranchContractsUseCase
import br.com.mobicare.cielo.arv.presentation.model.ScheduleContract
import br.com.mobicare.cielo.arv.utils.ArvConstants
import br.com.mobicare.cielo.arv.utils.UiArvBranchesContractsState
import br.com.mobicare.cielo.arv.utils.UiArvScheduledMarketFeatureToggleState
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.extensions.formatRate
import br.com.mobicare.cielo.extensions.orZero
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import kotlinx.coroutines.launch

class ArvScheduledAnticipationBankSelectViewModel(
    args: ArvScheduledAnticipationBankSelectFragmentArgs,
    private val getArvBranchContractsUseCase: GetArvBranchContractsUseCase,
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getFeatureTogglePreference: GetFeatureTogglePreferenceUseCase
) : ViewModel() {

    private val _arvBanksListMutableLiveData = MutableLiveData<List<ArvBank>?>()
    val arvBanksListLiveData: MutableLiveData<List<ArvBank>?> get() = _arvBanksListMutableLiveData

    private val _arvSelectedBankMutableLiveData = MutableLiveData<ArvBank?>()
    val arvSelectedBankLiveData: LiveData<ArvBank?> get() = _arvSelectedBankMutableLiveData

    private val _arvBranchesContractsStateMutableLiveData = MutableLiveData<UiArvBranchesContractsState>()
    val arvBranchesContractsStateLiveData: LiveData<UiArvBranchesContractsState> get() = _arvBranchesContractsStateMutableLiveData

    private val _arvMarketToggleMutableLiveData = MutableLiveData<UiArvScheduledMarketFeatureToggleState>()
    val arvMarketToggleLiveData: LiveData<UiArvScheduledMarketFeatureToggleState> get() = _arvMarketToggleMutableLiveData

    private val _arvNegotiationAvailableTypeMutableLiveData = MutableLiveData<String?>()
    val arvNegotiationAvailableTypeMutableLiveData: LiveData<String?> get() = _arvNegotiationAvailableTypeMutableLiveData

    private val _arvNegotiationTypeMutableLiveData = MutableLiveData<String?>()
    val arvNegotiationTypeLiveData: LiveData<String?> get() = _arvNegotiationTypeMutableLiveData


    init {
        viewModelScope.launch {
            getScheduledAnticipationMarketFeatureToggle()
            setInitAvailableNegotiations(args.scheduledanticipationargs)
            setInitNegotiationType()
        }
    }

    private fun setInitAvailableNegotiations(arvScheduledAnticipation: ArvScheduledAnticipation) {
        var availableTypes = arvScheduledAnticipation.rateSchedules?.filterNot {
            it?.schedule == true || it?.cnpjRoot == true
        }?.map { it?.name }

        if (arvMarketToggleLiveData.value is UiArvScheduledMarketFeatureToggleState.Disabled) {
            availableTypes = availableTypes?.filterNot { it == ArvConstants.MARKET_NEGOTIATION_TYPE }
        }

        if (availableTypes?.size.orZero > ONE) {
            _arvNegotiationAvailableTypeMutableLiveData.value = ArvConstants.BOTH_NEGOTIATION_TYPE
        } else {
            _arvNegotiationAvailableTypeMutableLiveData.value = availableTypes?.firstOrNull()
        }
    }

    private fun setInitNegotiationType() {
        _arvNegotiationTypeMutableLiveData.value =
            when (arvNegotiationAvailableTypeMutableLiveData.value) {
                ArvConstants.BOTH_NEGOTIATION_TYPE, ArvConstants.CIELO_NEGOTIATION_TYPE ->
                    ArvConstants.CIELO_NEGOTIATION_TYPE

                ArvConstants.MARKET_NEGOTIATION_TYPE -> ArvConstants.MARKET_NEGOTIATION_TYPE
                else -> null
            }
    }

    fun setupBankList(banks: List<ArvBank>) {
        _arvBanksListMutableLiveData.value = banks
        _arvSelectedBankMutableLiveData.value = banks.firstOrNull()
    }

    fun handleBankSelect(arvBank: ArvBank) {
        _arvSelectedBankMutableLiveData.value = arvBank
    }

    fun getBranchesContracts() {
        if(_arvBranchesContractsStateMutableLiveData.value is UiArvBranchesContractsState.AlreadyShowed)
            return

        viewModelScope.launch {
            _arvBranchesContractsStateMutableLiveData.value = UiArvBranchesContractsState.ShowLoading
            getArvBranchContractsUseCase().onSuccess {
                _arvBranchesContractsStateMutableLiveData.value = UiArvBranchesContractsState.HideLoading
                if(it.total.orZero > ZERO) {
                    _arvBranchesContractsStateMutableLiveData.value =
                        UiArvBranchesContractsState.SuccessListContracts(
                            mapContracts(it.schedules) ?: emptyList()
                        )
                }
            }.onError {
                _arvBranchesContractsStateMutableLiveData.value = UiArvBranchesContractsState.HideLoading
                val error = it.apiException.newErrorMessage
                newErrorHandler(
                    getUserObjUseCase = getUserObjUseCase,
                    newErrorMessage = error,
                    onErrorAction = {
                        _arvBranchesContractsStateMutableLiveData.value =
                            UiArvBranchesContractsState.ShowError(error)
                    })
            }
        }
    }

    private fun mapContracts(contracts: List<Schedules>?) =
        contracts?.flatMap {
            listOfNotNull(
                if (it.contractDateCielo != null) {
                    ScheduleContract(
                        cnpj = it.cnpj.orEmpty(),
                        schedule = ArvConstants.CIELO_NEGOTIATION_TYPE,
                        fee = it.nominalRateCielo.formatRate(),
                        hireDate = it.contractDateCielo
                    )
                } else {
                    null
                },
                if (it.contractDateMarket != null) {
                    ScheduleContract(
                        cnpj = it.cnpj.orEmpty(),
                        schedule = ArvConstants.MARKET_NEGOTIATION_TYPE,
                        fee = it.nominalRateMarket.formatRate(),
                        hireDate = it.contractDateMarket
                    )
                } else {
                    null
                }
            )
        }

    fun setAlreadyShowedContracts(wasAlreadyShowed: Boolean) {
        if(wasAlreadyShowed) {
            _arvBranchesContractsStateMutableLiveData.value = UiArvBranchesContractsState.AlreadyShowed
        }
    }

    private suspend fun getScheduledAnticipationMarketFeatureToggle() {
        getFeatureTogglePreference(key = FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_PROGRAMADA).onSuccess {
            if (!it) {
                _arvMarketToggleMutableLiveData.value =
                    UiArvScheduledMarketFeatureToggleState.Disabled
            }
        }
    }

    fun setArvNegotiation(arvNegotiationType: String?) {
        _arvNegotiationTypeMutableLiveData.value = arvNegotiationType
    }
}
