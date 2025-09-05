package br.com.mobicare.cielo.arv.presentation.historic.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.data.model.request.ArvHistoricRequest
import br.com.mobicare.cielo.arv.data.model.response.Item
import br.com.mobicare.cielo.arv.domain.useCase.GetArvAnticipationHistoryNewUseCase
import br.com.mobicare.cielo.arv.utils.UiArvHistoricState
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.getErrorMessage
import kotlinx.coroutines.launch

class ArvHistoricListViewModel constructor(
        private val getArvAnticipationHistoryUseCase: GetArvAnticipationHistoryNewUseCase,
        private val getUserObjUseCase: GetUserObjUseCase
) : ViewModel() {

    private val _arvHistoricUiState = MutableLiveData<UiArvHistoricState>()
    val arvHistoricUiState: LiveData<UiArvHistoricState> get() = _arvHistoricUiState

    private val _arvHistoricListNegotiations = MutableLiveData<ArrayList<Item>?>()
    val arvHistoricLisNegotiations: LiveData<ArrayList<Item>?> get() = _arvHistoricListNegotiations

    private var endList: Boolean = false
    private var page: Int = ONE

    fun getHistoric(isMoreHistoric: Boolean, isStart: Boolean) {
        if ((isMoreHistoric && endList)
                || (isStart && page > ONE)
                || (_arvHistoricUiState.value == UiArvHistoricState.ShowLoadingHistoric)
                || (_arvHistoricUiState.value == UiArvHistoricState.ShowLoadingMoreHistoric)) return
        if (isMoreHistoric.not()) {
            page = ONE
            endList = false
        }

        _arvHistoricUiState.value =
                if (isMoreHistoric) UiArvHistoricState.ShowLoadingMoreHistoric
                else UiArvHistoricState.ShowLoadingHistoric

        viewModelScope.launch {
            val params = ArvHistoricRequest(page = page)
            getArvAnticipationHistoryUseCase(params)
                    .onSuccess { response ->
                        page++
                        if (response.items.isNullOrEmpty().not()) {
                            response.items?.let {
                                val listItems: ArrayList<Item> = ArrayList()
                                it.forEach { item ->
                                    if (item != null) listItems.add(item)
                                }

                                _arvHistoricUiState.value =
                                        if (isMoreHistoric) UiArvHistoricState.HideLoadingMoreHistoric
                                        else UiArvHistoricState.HideLoadingHistoric

                                if (isMoreHistoric) {
                                    if (listItems.isNotEmpty()) {
                                        val value = _arvHistoricListNegotiations.value
                                        value?.addAll(listItems)
                                        _arvHistoricListNegotiations.value = value
                                    }
                                } else {
                                    if (listItems.isNotEmpty()) {
                                        _arvHistoricUiState.value = UiArvHistoricState.Success
                                        _arvHistoricListNegotiations.value = listItems
                                    }else{
                                        _arvHistoricUiState.value = UiArvHistoricState.EmptyHistoric
                                    }
                                }
                                if (listItems.count() < TWENTY_FIVE) endList = true
                            }
                        } else {
                            endList = true
                            _arvHistoricUiState.value =
                                    if (isMoreHistoric) UiArvHistoricState.HideLoadingMoreHistoric
                                    else UiArvHistoricState.HideLoadingHistoric
                            _arvHistoricUiState.value =
                                    UiArvHistoricState.EmptyHistoric
                        }
                    }.onEmpty {
                        _arvHistoricUiState.value =
                                if (isMoreHistoric) UiArvHistoricState.HideLoadingMoreHistoric
                                else UiArvHistoricState.HideLoadingHistoric
                        if (isMoreHistoric.not()) {
                            _arvHistoricUiState.value =
                                    UiArvHistoricState.EmptyHistoric
                        }
                    }.onError { error ->
                        _arvHistoricUiState.value =
                                if (isMoreHistoric) UiArvHistoricState.HideLoadingMoreHistoric
                                else UiArvHistoricState.HideLoadingHistoric

                        val message = error.apiException.newErrorMessage
                                .getErrorMessage(R.string.anticipation_historic_error)
                        newErrorHandler(
                                getUserObjUseCase = getUserObjUseCase,
                                newErrorMessage = error.apiException.newErrorMessage,
                                onErrorAction = {
                                    _arvHistoricUiState.value =
                                            UiArvHistoricState.ErrorHistoric(message, error.apiException.newErrorMessage)
                                }
                        )
                    }
        }
    }

}