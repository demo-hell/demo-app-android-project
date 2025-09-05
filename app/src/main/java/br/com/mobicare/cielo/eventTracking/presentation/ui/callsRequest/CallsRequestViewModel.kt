package br.com.mobicare.cielo.eventTracking.presentation.ui.callsRequest

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.eventTracking.domain.model.CallRequest
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterChip
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterChipType
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus
import br.com.mobicare.cielo.eventTracking.domain.useCase.GetCallsEventListUseCase
import br.com.mobicare.cielo.eventTracking.utils.EventsRequestResource
import kotlinx.coroutines.launch

class CallsRequestViewModel(
    private val getCallsEventListUseCase: GetCallsEventListUseCase
) : ViewModel(){

    private val _callsFilterList = MutableLiveData<MutableSet<List<CieloFilterChip>>>()
    val callsFilterList get() = _callsFilterList

    private val _callsEventList =
        MutableLiveData<EventsRequestResource<List<CallRequest>>>()
    val callsEventList: MutableLiveData<EventsRequestResource<List<CallRequest>>> get() = _callsEventList

    private var filterInitialDate: String? = null
    private var filterEndDate: String? = null
    private var filterRequestStatus: EventRequestStatus? = null
    private var filterSearchQuery: String? = null
    private var filterSearchRequest: String? = null

    private fun getFilterTranslatedValues() {
        _callsFilterList.value?.last().also { filterList ->
            val statusFilter = filterList?.firstOrNull { it.filterType == CieloFilterChipType.STATUS }
            val searchFilter = filterList?.firstOrNull { it.filterType == CieloFilterChipType.SEARCH }

            filterRequestStatus = if ((statusFilter?.currentSelected ?: ONE_NEGATIVE) > ZERO) EventRequestStatus.ATTENDED else null
            filterSearchQuery = searchFilter?.searchRequest
        }
    }

    fun updateCallsFilter(filterList: List<CieloFilterChip>) {
        if (_callsFilterList.value != null) {
            _callsFilterList.value = _callsFilterList.value?.apply {
                remove(filterList)
                add(filterList)
            }
        } else {
            _callsFilterList.value = mutableSetOf(filterList)
        }
        requestCallsEventList()
    }

    fun startFilter(filterList: List<CieloFilterChip>) {
        _callsFilterList.value = mutableSetOf(filterList)
        requestCallsEventList()
    }

    fun getCurrentSelectedFilterStatus() =
        callsFilterList.value?.last()?.firstOrNull { it.filterType == CieloFilterChipType.STATUS }
            ?.let {
                it.filterPossibilities[if (it.currentSelected < ZERO) ZERO else it.currentSelected].normalizeToLowerSnakeCase()
            } ?: EMPTY

    fun clearLastSelectedFilter() {
        _callsFilterList.value = _callsFilterList.value?.apply {
            remove(last())
        }
        requestCallsEventList()
    }

    fun setSearchQuery(query: String){
        filterSearchRequest = query
    }

    fun requestCallsEventList() {
        getFilterTranslatedValues()
        viewModelScope.launch {
            _callsEventList.value = EventsRequestResource.Loading
            getCallsEventListUseCase(
                filterInitialDate,
                filterEndDate,
                filterRequestStatus,
                filterSearchRequest
            )
                .onSuccess { callsRequestList ->
                    _callsEventList.value =
                        EventsRequestResource.Success(callsRequestList)
                }
                .onError {
                    _callsEventList.value = EventsRequestResource.Error(it.apiException)
                }
                .onEmpty {
                    _callsEventList.value = EventsRequestResource.Success(emptyList())
                }
        }
    }
}