package br.com.mobicare.cielo.home.presentation.arv.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.useCase.GetArvScheduledAnticipationUseCase
import br.com.mobicare.cielo.arv.domain.useCase.GetArvSingleAnticipationWithDateNewUseCase
import br.com.mobicare.cielo.arv.utils.ARVUtils
import br.com.mobicare.cielo.arv.utils.ArvConstants
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_420
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.commons.utils.getDateInTheFuture
import br.com.mobicare.cielo.extensions.isHigherThanZero
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import kotlinx.coroutines.launch

class ArvCardAlertHomeViewModel(
    private val getArvSingleAnticipationWithDateNewUseCase: GetArvSingleAnticipationWithDateNewUseCase,
    private val getFeatureTogglePreference: GetFeatureTogglePreferenceUseCase,
    private val arvScheduledAnticipationUseCase: GetArvScheduledAnticipationUseCase,
) : ViewModel() {
    private val _arvCardAlertLiveData = MutableLiveData<UiArvCardAlertState?>()
    val arvCardAlertLiveData: LiveData<UiArvCardAlertState?> get() = _arvCardAlertLiveData

    fun getArvCardInformation() {
        viewModelScope.launch {
            getFeatureTogglePreference(key = FeatureTogglePreference.HOME_ALERT_CARD_ARV).onSuccess { isEnabled ->
                if (isEnabled) {
                    getSchedule()
                } else {
                    _arvCardAlertLiveData.value = UiArvCardAlertState.HideArvCardAlert
                }
            }
        }
    }

    private fun getSchedule() {
        viewModelScope.launch {
            arvScheduledAnticipationUseCase.invoke()
                .onSuccess { arvScheduledAnticipation ->
                    arvScheduledAnticipation.rateSchedules?.forEach {
                        when (it?.name) {
                            ArvConstants.CIELO_NEGOTIATION_TYPE -> {
                                if (it.schedule == false && it.cnpjRoot == false) {
                                    getArvValue()
                                } else {
                                    _arvCardAlertLiveData.value = UiArvCardAlertState.HideArvCardAlert
                                }
                            }
                        }
                    }
                }.onError { error ->
                    handleArvCardAlertError(error.apiException)
                }.onEmpty { _arvCardAlertLiveData.value = UiArvCardAlertState.HideArvCardAlert }
        }
    }

    private fun getArvValue() {
        val formattedDatesPair = getFormattedDatesPair()
        viewModelScope.launch {
            getArvSingleAnticipationWithDateNewUseCase.invoke(
                negotiationType = ArvConstants.CIELO_NEGOTIATION_TYPE,
                initialDate = formattedDatesPair.first,
                endDate = formattedDatesPair.second,
            ).onSuccess { arvAnticipation ->
                handleArvCardAlertSuccess(arvAnticipation)
            }.onError { error ->
                handleArvCardAlertError(error.apiException)
            }.onEmpty {
                _arvCardAlertLiveData.value = UiArvCardAlertState.HideArvCardAlert
            }
        }
    }

    private fun handleArvCardAlertError(error: CieloAPIException) {
        if (error.httpStatusCode == HTTP_STATUS_420) {
            _arvCardAlertLiveData.value = UiArvCardAlertState.HideArvCardAlert
        } else {
            _arvCardAlertLiveData.value =
                UiArvCardAlertState.Error(error.newErrorMessage)
        }
    }

    private fun handleArvCardAlertSuccess(arvAnticipation: ArvAnticipation) {
        if (arvAnticipation.grossAmount.isHigherThanZero()) {
            _arvCardAlertLiveData.value = UiArvCardAlertState.ShowArvCardAlert(arvAnticipation)
        } else {
            _arvCardAlertLiveData.value = UiArvCardAlertState.HideArvCardAlert
        }
    }

    private fun getFormattedDatesPair(): Pair<String, String> {
        val firstFormattedDate = ARVUtils.minAnticipationRangeDate.formatDateToAPI()
        val secondFormattedDate = ARVUtils.maxAnticipationRangeDate.formatDateToAPI()
        return Pair(firstFormattedDate, secondFormattedDate)
    }
}
