package br.com.mobicare.cielo.suporteTecnico.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.HTTP_422_UNPROCESSABLE_ENTITY
import br.com.mobicare.cielo.commons.constants.HTTP_422_UNPROCESSABLE_ENTITY_MESSAGE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.suporteTecnico.domain.useCase.GetMerchantEquipmentsUseCase
import br.com.mobicare.cielo.suporteTecnico.utils.UiStateEquipments
import br.com.mobicare.cielo.suporteTecnico.utils.UiStateEquipments.Empty
import br.com.mobicare.cielo.suporteTecnico.utils.UiStateEquipments.Error
import br.com.mobicare.cielo.suporteTecnico.utils.UiStateEquipments.ErrorWithoutMachine
import br.com.mobicare.cielo.suporteTecnico.utils.UiStateEquipments.HideLoading
import br.com.mobicare.cielo.suporteTecnico.utils.UiStateEquipments.ShowLoading
import br.com.mobicare.cielo.suporteTecnico.utils.UiStateEquipments.Success
import br.com.mobicare.cielo.taxaPlanos.domain.TerminalsResponse
import kotlinx.coroutines.launch

class MerchantEquipmentsViewModel(
    private val getMerchantEquipmentsUseCase: GetMerchantEquipmentsUseCase,
    private val getUserObjUseCase: GetUserObjUseCase,
) : ViewModel() {
    private val _merchantEquipments = MutableLiveData<UiStateEquipments<TerminalsResponse>>()
    val merchantEquipments: LiveData<UiStateEquipments<TerminalsResponse>>
        get() = _merchantEquipments

    fun getMerchantEquipment() {
        _merchantEquipments.value = ShowLoading

        viewModelScope.launch {
            getMerchantEquipmentsUseCase()
                .onSuccess { terminalsResponse ->
                    _merchantEquipments.value = processSuccess(terminalsResponse)
                }.onError { responseError ->
                    _merchantEquipments.value = HideLoading

                    newErrorHandler(
                        getUserObjUseCase = getUserObjUseCase,
                        newErrorMessage = responseError.apiException.newErrorMessage,
                        onErrorAction = {
                            _merchantEquipments.value =
                                when (responseError.apiException.newErrorMessage.httpCode) {
                                    HTTP_422_UNPROCESSABLE_ENTITY -> {
                                        ErrorWithoutMachine(responseError.apiException.newErrorMessage)
                                    }

                                    else -> {
                                        Error
                                    }
                                }
                        },
                    )
                }.onEmpty {
                    _merchantEquipments.value = HideLoading
                    _merchantEquipments.postValue(Empty)
                }
        }
    }

    private fun processSuccess(data: TerminalsResponse?): UiStateEquipments<TerminalsResponse>? {
        val items = data?.terminals?.filter { it.replacementAllowed == true }

        return if (items.isNullOrEmpty()) {
            ErrorWithoutMachine(
                NewErrorMessage(
                    httpCode = HTTP_422_UNPROCESSABLE_ENTITY,
                    message = HTTP_422_UNPROCESSABLE_ENTITY_MESSAGE,
                ),
            )
        } else {
            Success(
                data =
                    TerminalsResponse(
                        rentalEquipments = data.rentalEquipments,
                        terminals = items,
                    ),
            )
        }
    }
}
