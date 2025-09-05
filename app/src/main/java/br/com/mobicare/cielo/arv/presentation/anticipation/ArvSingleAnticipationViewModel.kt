package br.com.mobicare.cielo.arv.presentation.anticipation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.useCase.GetArvSingleAnticipationWithDateNewUseCase
import br.com.mobicare.cielo.arv.utils.ARVUtils
import br.com.mobicare.cielo.arv.utils.ArvConstants
import br.com.mobicare.cielo.arv.utils.UiArvSingleFeatureToggleState
import br.com.mobicare.cielo.arv.utils.UiArvSingleWithDateState
import br.com.mobicare.cielo.commons.constants.HTTP_UNKNOWN
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.commons.utils.addMonths
import br.com.mobicare.cielo.commons.utils.dateInternationalFormat
import br.com.mobicare.cielo.commons.utils.parseToLocalDatePT
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import kotlinx.coroutines.launch
import java.time.Period

class ArvSingleAnticipationViewModel(
    private val arvSingleAnticipationWithDateNewUseCase: GetArvSingleAnticipationWithDateNewUseCase,
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getFeatureTogglePreference: GetFeatureTogglePreferenceUseCase
) : ViewModel() {

    private val _arvMarketToggleMutableLiveData = MutableLiveData<UiArvSingleFeatureToggleState>()
    val arvMarketToggleLiveData: LiveData<UiArvSingleFeatureToggleState> get() = _arvMarketToggleMutableLiveData

    private val _arvSingleAnticipationWithDataMutableLiveData =
        MutableLiveData<UiArvSingleWithDateState>()
    val arvSingleAnticipationWithDataLiveData: LiveData<UiArvSingleWithDateState> get() = _arvSingleAnticipationWithDataMutableLiveData

    private val _arvDateRangeMutableLiveData = MutableLiveData<Pair<String, String>>()
    val arvDateRangeLiveData: LiveData<Pair<String, String>> get() = _arvDateRangeMutableLiveData

    private val _monthsDifferenceLiveData = MutableLiveData(24)
    val monthsDifferenceLiveData: LiveData<Int> get() = _monthsDifferenceLiveData

    val minSelectableCalendar = ARVUtils.minAnticipationRangeDate
    val maxSelectableCalendar = ARVUtils.maxAnticipationRangeDate

    private var startPeriod: String = minSelectableCalendar.formatBRDate()
    private var endPeriod: String = maxSelectableCalendar.formatBRDate()
    var receivableType: String? = null

    init {
        viewModelScope.launch {
            getSingleAnticipationMarketFeatureToggle()
        }
    }

    private suspend fun getSingleAnticipationMarketFeatureToggle() {
        getFeatureTogglePreference(key = FeatureTogglePreference.ANTECIPE_VENDAS_MERCADO_AVULSA).onSuccess {
            if (!it) {
                _arvMarketToggleMutableLiveData.value =
                    UiArvSingleFeatureToggleState.Disabled
            }
        }
    }

    fun getArvSingleAnticipationWithDate() {
        _arvSingleAnticipationWithDataMutableLiveData.value =
            UiArvSingleWithDateState.ShowLoadingArvSingleWithDate
        viewModelScope.launch {
            arvSingleAnticipationWithDateNewUseCase(
                receivableType,
                startPeriod.dateInternationalFormat(),
                endPeriod.dateInternationalFormat()
            ).onSuccess { anticipation ->
                _arvSingleAnticipationWithDataMutableLiveData.value =
                    UiArvSingleWithDateState.HideLoadingArvSingleWithDate
                _arvSingleAnticipationWithDataMutableLiveData.value =
                    anticipation.let { arvAnticipation ->
                        UiArvSingleWithDateState.SuccessArvSingleWithDate(
                            mapDate(
                                arvAnticipation,
                                arvAnticipation.initialDate
                                    ?: startPeriod.dateInternationalFormat(),
                                arvAnticipation.finalDate
                                    ?: endPeriod.dateInternationalFormat(),
                                receivableType
                            )
                        )
                    }

            }.onEmpty {
                _arvSingleAnticipationWithDataMutableLiveData.value =
                    UiArvSingleWithDateState.HideLoadingArvSingleWithDate
                _arvSingleAnticipationWithDataMutableLiveData.value =
                    UiArvSingleWithDateState.ErrorArvSingleWithDateMessage(R.string.anticipation_error)

            }.onError { apiError ->
                val error = apiError.apiException.newErrorMessage
                val message =
                    if (error.httpCode == HTTP_UNKNOWN) error.message else R.string.anticipation_error

                _arvSingleAnticipationWithDataMutableLiveData.value =
                    UiArvSingleWithDateState.HideLoadingArvSingleWithDate
                when (error.flagErrorCode) {
                    ArvConstants.NONEXISTENT_RECEIVABLES_ANTICIPATION -> _arvSingleAnticipationWithDataMutableLiveData.value =
                        UiArvSingleWithDateState.NoValuesToAnticipate

                    else -> newErrorHandler(getUserObjUseCase = getUserObjUseCase,
                        newErrorMessage = error,
                        onErrorAction = {
                            _arvSingleAnticipationWithDataMutableLiveData.value =
                                UiArvSingleWithDateState.ErrorArvSingleWithDate(error, message)
                        })
                }
            }
        }
    }

    fun fetchAnticipationFixedPeriod(int: Int) {
        val newFixedStartPeriod = ARVUtils.minAnticipationRangeDate.formatBRDate()
        val newFixedEndPeriod = DataCustomNew().apply {
            setDate(ARVUtils.minAnticipationRangeDate.toDate())
            toCalendar().addMonths(int)
        }.formatBRDate()

        updateDateRange(newFixedStartPeriod, newFixedEndPeriod)

        getArvSingleAnticipationWithDate()
    }

    fun updateDateRange(initDate: String? = null, endDate: String? = null) {
        startPeriod = initDate ?: startPeriod
        endPeriod = endDate ?: endPeriod

        _arvDateRangeMutableLiveData.value = Pair(startPeriod, endPeriod)
        updateMonthsOption()

    }

    private fun updateMonthsOption() {
        val startDate = startPeriod.parseToLocalDatePT()
        val endDate = endPeriod.parseToLocalDatePT()

        if (startPeriod != ARVUtils.minAnticipationRangeDate.formatBRDate() ||
            startDate.dayOfMonth != endDate.dayOfMonth
        ) {
            _monthsDifferenceLiveData.value = UNSET_MONTHS_OPTION
        } else {
            _monthsDifferenceLiveData.value = Period.between(startDate, endDate).toTotalMonths().toInt()
        }
    }

    private fun mapDate(
        anticipation: ArvAnticipation,
        start: String?,
        end: String?,
        negotiationType: String?
    ): ArvAnticipation {
        return anticipation.copy(
            initialDate = start,
            finalDate = end,
            negotiationType = negotiationType
        )
    }

    private companion object {
        const val UNSET_MONTHS_OPTION = -1
    }
}



