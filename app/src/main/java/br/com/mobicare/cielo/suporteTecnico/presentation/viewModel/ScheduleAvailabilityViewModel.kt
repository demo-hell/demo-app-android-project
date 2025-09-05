package br.com.mobicare.cielo.suporteTecnico.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.constants.DASH
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.helpers.DateTimeHelper
import br.com.mobicare.cielo.orders.domain.OrderReplacementResponse
import br.com.mobicare.cielo.recebaMais.domain.OwnerAddress
import br.com.mobicare.cielo.recebaMais.domain.OwnerPhone
import br.com.mobicare.cielo.suporteTecnico.data.Address
import br.com.mobicare.cielo.suporteTecnico.data.Availability
import br.com.mobicare.cielo.suporteTecnico.data.OpenTicket
import br.com.mobicare.cielo.suporteTecnico.data.ScheduleDataResponse
import br.com.mobicare.cielo.suporteTecnico.data.UserOwnerSupportResponse
import br.com.mobicare.cielo.suporteTecnico.domain.useCase.GetPostOrdersReplacementsUseCase
import br.com.mobicare.cielo.suporteTecnico.domain.useCase.GetScheduleAvailabilityUseCase
import br.com.mobicare.cielo.suporteTecnico.utils.ADDRESS_FORMAT
import br.com.mobicare.cielo.suporteTecnico.utils.CELLPHONE
import br.com.mobicare.cielo.suporteTecnico.utils.SCHEDULE_FORMAT
import br.com.mobicare.cielo.suporteTecnico.utils.SELECT_ADDRESS
import br.com.mobicare.cielo.suporteTecnico.utils.UIStatePostOrdersReplacements
import kotlinx.coroutines.launch

class ScheduleAvailabilityViewModel(
    private val getScheduleAvailabilityUseCase: GetScheduleAvailabilityUseCase,
    private val postOrdersReplacementsUseCase: GetPostOrdersReplacementsUseCase
) : ViewModel() {

    private val _scheduleAvailability = MutableLiveData<UiState<ScheduleDataResponse>>()
    val scheduleAvailability: LiveData<UiState<ScheduleDataResponse>> = _scheduleAvailability

    private val _merchantAddress = MutableLiveData<UiState<UserOwnerSupportResponse>>()
    val merchantAddress: LiveData<UiState<UserOwnerSupportResponse>> get() = _merchantAddress

    private val _postOrdersReplacements =
        MutableLiveData<UIStatePostOrdersReplacements<OrderReplacementResponse>>()
    val postOrdersReplacements: LiveData<UIStatePostOrdersReplacements<OrderReplacementResponse>> get() = _postOrdersReplacements

    private var _originalData: Any? = null
    private var _formattedData: HashMap<String, String>? = null
    val formattedData: HashMap<String, String>? get() = _formattedData

    private var _addressSelected = MutableLiveData<OwnerAddress?>()
    val addressSelected: LiveData<OwnerAddress?> get() = _addressSelected

    private var _contactInput: Any? = null
    private var _contactInOriginalData = MutableLiveData<OwnerPhone?>()
    val contactInOriginalData: LiveData<OwnerPhone?> get() = _contactInOriginalData

    fun getScheduleAvailability() {
        _scheduleAvailability.value = UiState.Loading

        viewModelScope.launch {
            getScheduleAvailabilityUseCase.getScheduleAvailability()
                .onSuccess { scheduleDataResponse ->
                    _scheduleAvailability.postValue(UiState.Success(scheduleDataResponse))
                }
                .onError {
                    _scheduleAvailability.postValue(UiState.Error(null))
                }
                .onEmpty {
                    _scheduleAvailability.postValue(UiState.Empty)
                }
        }
    }

    fun getMerchant() {
        _merchantAddress.value = UiState.Loading

        viewModelScope.launch {
            getScheduleAvailabilityUseCase.getMerchant()
                .onSuccess { userOwnerSupportResponse ->
                    _merchantAddress.value = UiState.HideLoading
                    _merchantAddress.postValue(UiState.Success(userOwnerSupportResponse))
                }
                .onError {
                    _merchantAddress.value = UiState.HideLoading
                    _merchantAddress.postValue(UiState.Error(null))
                }
                .onEmpty {
                    _merchantAddress.value = UiState.HideLoading
                    _merchantAddress.postValue(UiState.Empty)
                }
        }
    }

    fun postOrdersReplacements(request: OpenTicket) {
        _postOrdersReplacements.value = UIStatePostOrdersReplacements.Loading

        viewModelScope.launch {
            postOrdersReplacementsUseCase(request)
                .onSuccess {
                    updateLoadingOrders(it)
                }
                .onError {
                    _postOrdersReplacements.postValue(UIStatePostOrdersReplacements.Error(it.apiException.newErrorMessage))
                }
                .onEmpty {
                    _postOrdersReplacements.postValue(UIStatePostOrdersReplacements.Empty)
                }
        }
    }

    private suspend fun updateLoadingOrders(data: OrderReplacementResponse) {
        _postOrdersReplacements.value = UIStatePostOrdersReplacements.UpdateLoadingMessage
        _postOrdersReplacements.value = UIStatePostOrdersReplacements.Success(data)
    }

    fun saveOriginalData(data: Any) {
        _originalData = data
    }

    fun saveOriginalPhone(data: Any) {
        _contactInput = data
        getContactByPhoneNumber()
    }

    fun formatData(data: Any) {
        when (data) {
            is ScheduleDataResponse -> {
                _formattedData = formatSchedule(data.availabilityList)
            }

            is UserOwnerSupportResponse -> {
                _formattedData = formatAddress(data.addresses)
            }
        }
    }

    private fun getFirstNameOfWeekDay(dayOfWeek: String): String {

        val dayParts = dayOfWeek.split(DASH)
        return dayParts[ZERO]
    }

    private fun formatSchedule(scheduleList: List<Availability>): HashMap<String, String> {
        val scheduleMap = HashMap<String, String>()
        scheduleList.forEach {
            val initialDay =
                getFirstNameOfWeekDay(DateTimeHelper.convertWeekDayToPortuguese(it.initialWeekDay))
            val finalDay =
                getFirstNameOfWeekDay(DateTimeHelper.convertWeekDayToPortuguese(it.finalWeekDay))
            val initialHour = formatHour(it.initialHour)
            val finalHour = formatHour(it.finalHour)
            scheduleMap[it.code] = String.format(SCHEDULE_FORMAT, initialDay, finalDay, initialHour,finalHour)
        }
        return scheduleMap
    }

    private fun formatAddress(addressList: List<OwnerAddress>): HashMap<String, String> {
        val addressMap = HashMap<String, String>()
        addressList.forEach {
            addressMap[it.id] = String.format(ADDRESS_FORMAT, formatField(it.streetAddress),
                formatField(it.number ?: EMPTY), formatField(it.streetAddress2 ?: EMPTY),
                formatField(it.neighborhood), formatField(it.city), formatField(it.state), formatField(it.zipCode))
        }
        return addressMap
    }

    fun formatField(fieldAddress: String): String {
        return if (fieldAddress.isNullOrEmpty())
            EMPTY
        else
            fieldAddress
    }

    private fun formatHour(hour: Int): String {
        return "${hour}h"
    }

    fun populateViewIfOriginalDataIsNull() {
        if (_originalData == null) {
            val firstAddress =
                (_merchantAddress.value as? UiState.Success<UserOwnerSupportResponse>)?.data?.addresses?.firstOrNull()
            _addressSelected.postValue(firstAddress)
        }
    }

    fun getCollectionForOption(title: String, searchString: String) {
        when (title) {
            SELECT_ADDRESS -> {
                val addresses = _originalData as? List<OwnerAddress> ?: return
                val filteredAddresses = addresses.firstOrNull { address ->
                    address.id.contains(searchString, ignoreCase = true)
                }
                if (filteredAddresses != null) {
                    _addressSelected.value = filteredAddresses
                }
            }
            else -> return
        }
    }

    private fun formattedPhoneToOwnerPhone(formattedPhone: String): OwnerPhone? {
        val cleanedPhone = formattedPhone.replace("(", "").replace(")", "").replace("-", "")

        if (!cleanedPhone.contains(" ")) {
            return null
        }

        val phoneParts = cleanedPhone.split(" ")

        val areaCode = phoneParts[ZERO]
        val number = phoneParts[ONE]

        return OwnerPhone(
            areaCode = areaCode,
            number = number,
            type = CELLPHONE
        )
    }

    private fun getContactByPhoneNumber() {
        val contacts =
            (_merchantAddress.value as? UiState.Success<UserOwnerSupportResponse>)?.data?.contacts
                ?: return
        val filteredContacts = contacts.firstOrNull { contact ->
            contact.ownerPhones.any { it.number == _contactInput }
        }
        if (filteredContacts != null) {
            _contactInOriginalData.value =
                filteredContacts.ownerPhones.firstOrNull { it.number == _contactInput }
        } else {
            _contactInOriginalData.value = formattedPhoneToOwnerPhone(_contactInput.toString())
        }
    }

    fun convertAddressToAddress(
        address: OwnerAddress?,
        landMark: String?,
        storeFront: String?
    ): Address {
        return Address(
            id = address?.id,
            streetAddress = address?.streetAddress,
            streetAddress2 = address?.streetAddress2,
            neighborhood = address?.neighborhood,
            number = address?.number,
            city = address?.city,
            state = address?.state,
            zipCode = address?.zipCode,
            landMark = landMark,
            storeFront = storeFront
        )
    }
}