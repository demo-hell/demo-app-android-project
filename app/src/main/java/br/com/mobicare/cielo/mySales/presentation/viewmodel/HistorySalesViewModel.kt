package br.com.mobicare.cielo.mySales.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.mySales.data.model.params.GetSalesHistoryParams
import br.com.mobicare.cielo.mySales.data.model.bo.ResultSummarySalesHistoryBO
import br.com.mobicare.cielo.mySales.domain.usecase.GetSalesHistoryUseCase
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesViewState
import kotlinx.coroutines.launch

class HistorySalesViewModel(
    private val getHistorySalesUseCase: GetSalesHistoryUseCase,
    private val userPreferences: UserPreferences,
    private val userObjUseCase: GetUserObjUseCase ):  ViewModel() {

    private val _getSalesHistoryDataViewState = MutableLiveData<MySalesViewState<ResultSummarySalesHistoryBO>>()
    val getSalesHistoryDataViewState: LiveData<MySalesViewState<ResultSummarySalesHistoryBO>>
        get() = _getSalesHistoryDataViewState


    var isRefreshing: Boolean = false
    lateinit var quickFilter: QuickFilter

    fun getSalesHistory(quickFilter: QuickFilter){
        this.quickFilter = quickFilter
        getSalesHistoryData()
    }

    private fun getSalesHistoryData(){
        viewModelScope.launch {
            val params = GetSalesHistoryParams(
                accessToken = userPreferences.token,
                authorization = Utils.authorization(),
                quickFilter = quickFilter
            )

            if(isRefreshing.not())
                _getSalesHistoryDataViewState.postValue(MySalesViewState.LOADING)

            getHistorySalesUseCase.invoke(params)
                .onSuccess { historySalesBO ->
                    _getSalesHistoryDataViewState.postValue(MySalesViewState.SUCCESS(historySalesBO))
                }
                .onError { apiError ->
                    val newErrorMessage = apiError.apiException.newErrorMessage
                    newErrorHandler(
                        getUserObjUseCase = userObjUseCase,
                        newErrorMessage = newErrorMessage,
                        onHideLoading = {
                            _getSalesHistoryDataViewState.postValue(MySalesViewState.HIDE_LOADING) },
                        onErrorAction = {
                            _getSalesHistoryDataViewState.postValue(MySalesViewState.ERROR(newErrorMessage = newErrorMessage)) }
                    )
                }.onEmpty {
                    _getSalesHistoryDataViewState.postValue(MySalesViewState.EMPTY)
                }
        }
    }

    fun refresh() {
        isRefreshing = true
        getSalesHistoryData()
    }

    fun retry() {
        getSalesHistoryData()
    }

}