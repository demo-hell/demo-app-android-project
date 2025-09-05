package br.com.mobicare.cielo.eventTracking.presentation.ui.machineRequest

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
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterChip
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterChipType
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus
import br.com.mobicare.cielo.eventTracking.domain.useCase.GetDeliveryEventListUseCase
import br.com.mobicare.cielo.eventTracking.utils.EventsRequestResource
import br.com.mobicare.cielo.eventTracking.utils.MachineRequestItem
import kotlinx.coroutines.launch

class MachineRequestViewModel(
    private val getDeliveryEventListUseCase: GetDeliveryEventListUseCase
) : ViewModel() {
    private val _deliveryEventList =
        MutableLiveData<EventsRequestResource<List<MachineRequestItem>>>()
    val deliveryEventList: MutableLiveData<EventsRequestResource<List<MachineRequestItem>>> get() = _deliveryEventList

    private val _cieloFilterMachineList = MutableLiveData<MutableSet<List<CieloFilterChip>>>()
    val cieloFilterMachineList get() = _cieloFilterMachineList


    private var filterInitialDate: String? = null
    private var filterEndDate: String? = null
    private var filterServiceType: String? = null
    private var filterRequestStatus: EventRequestStatus? = null

    private fun getFilterTranslatedValues() {
        _cieloFilterMachineList.value?.last().also { filterList ->
            val dateFilter = filterList?.firstOrNull { it.filterType == CieloFilterChipType.DATE }
            val statusFilter = filterList?.firstOrNull { it.filterType == CieloFilterChipType.STATUS }
            val requestFilter = filterList?.firstOrNull { it.filterType == CieloFilterChipType.REQUEST }

            filterRequestStatus = if ((statusFilter?.currentSelected ?: ONE_NEGATIVE) > ZERO) EventRequestStatus.ATTENDED else null
            filterInitialDate = dateFilter?.initialDate
            filterEndDate = dateFilter?.endDate
            filterServiceType = requestFilter?.serviceType
        }
    }

    fun updateMachineFilter(filterList: List<CieloFilterChip>) {
        if (_cieloFilterMachineList.value != null) {
            _cieloFilterMachineList.value = _cieloFilterMachineList.value?.apply {
                remove(filterList)
                add(filterList)
            }
        } else {
            _cieloFilterMachineList.value = mutableSetOf(filterList)
        }
        requestDeliveryEventList()
    }

    fun startFilter(filterList: List<CieloFilterChip>) {
        _cieloFilterMachineList.value = mutableSetOf(filterList)
        requestDeliveryEventList()
    }

    fun getCurrentSelectedFilterStatus() =
        cieloFilterMachineList.value?.last()?.firstOrNull { it.filterType == CieloFilterChipType.STATUS }
            ?.let {
                it.filterPossibilities[if (it.currentSelected < ZERO) ZERO else it.currentSelected].normalizeToLowerSnakeCase()
            } ?: EMPTY

    fun clearLastSelectedFilter() {
        _cieloFilterMachineList.value = _cieloFilterMachineList.value?.apply {
            remove(last())
        }
        requestDeliveryEventList()
    }

    fun requestDeliveryEventList() {
        getFilterTranslatedValues()
        viewModelScope.launch {
            _deliveryEventList.value = EventsRequestResource.Loading
            getDeliveryEventListUseCase(filterInitialDate, filterEndDate, filterServiceType, filterRequestStatus)
                .onSuccess { machineRequestList ->
                    _deliveryEventList.value =
                        EventsRequestResource.Success(machineRequestList)
                }
                .onError {
                    _deliveryEventList.value = EventsRequestResource.Error(it.apiException)
                }
                .onEmpty {
                    _deliveryEventList.value = EventsRequestResource.Success(emptyList())
                }
        }
    }
}