package br.com.mobicare.cielo.mySales.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.mySales.data.model.Pagination
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.mySales.data.model.params.GetSalesDataParams
import br.com.mobicare.cielo.mySales.data.model.bo.SummarySalesBO
import br.com.mobicare.cielo.mySales.domain.usecase.GetMySalesTransactionsUseCase
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesViewState
import kotlinx.coroutines.launch

class MySalesTransactionsViewModel(
    private val getMySalesTransactionsUseCase : GetMySalesTransactionsUseCase,
    private val userPreferences: UserPreferences,
    private val userObjUseCase: GetUserObjUseCase,
    private val featureToggleUseCase: GetFeatureTogglePreferenceUseCase
    ): ViewModel() {

    private val _getSalesTransactionViewState = MutableLiveData<MySalesViewState<SummarySalesBO>>()
    val getSalesTransactionViewState: LiveData<MySalesViewState<SummarySalesBO>>
        get() = _getSalesTransactionViewState


    private lateinit var quickFilter: QuickFilter
    private var page: Int? = ONE
    private var paginationObj: Pagination? = null
    var isLastPage: Boolean? = false
    var isRefreshing: Boolean = false
    var isLoadingMorePagingData: Boolean = false
    var cancelSaleFeatureToggle: Boolean = false



    fun getMySalesTransactions(quickFilter: QuickFilter) {
        this.quickFilter = quickFilter
        getCancelFeatureToggle()
        getSalesTransactions()
    }


    private fun getSalesTransactions() {
        viewModelScope.launch {
            val params = GetSalesDataParams(
                accessToken = userPreferences.token,
                authorization = Utils.authorization(),
                page = page?.toLong(),
                quickFilter = quickFilter
            )

            if(page != ONE){
                _getSalesTransactionViewState.postValue(MySalesViewState.LOADING_MORE)
            }else {
                _getSalesTransactionViewState.postValue(MySalesViewState.LOADING)
            }

            getMySalesTransactionsUseCase.invoke(params)
                .onSuccess { summarySalesBO ->
                    paginationObj = summarySalesBO.pagination
                    isLastPage = summarySalesBO.pagination?.lastPage
                    isLoadingMorePagingData = false

                    if(paginationObj?.firstPage == true)
                        _getSalesTransactionViewState.postValue(MySalesViewState.SUCCESS(summarySalesBO))
                    else
                        _getSalesTransactionViewState.postValue(MySalesViewState.SUCCESS_PAGINATION(summarySalesBO))
                }
                .onError { apiError ->

                    val newErrorMessage = apiError.apiException.newErrorMessage
                    newErrorHandler(
                        getUserObjUseCase = userObjUseCase,
                        newErrorMessage = newErrorMessage,
                        onHideLoading = {
                            _getSalesTransactionViewState.postValue(MySalesViewState.HIDE_LOADING) },
                        onErrorAction = {
                            _getSalesTransactionViewState.postValue(MySalesViewState.ERROR(newErrorMessage = newErrorMessage)) }
                    )
                }
                .onEmpty {
                    _getSalesTransactionViewState.postValue(MySalesViewState.EMPTY)
                }
        }
    }

    fun loadMoreSalesTransactions() {
        paginationObj?.let { paginationObj ->
            if(paginationObj.lastPage != true) {
                page = page?.plus(1)
                isLoadingMorePagingData = true
                getSalesTransactions()
            }
        }
    }

    fun refresh() {
        resetVMParams(true)
        getSalesTransactions()
    }

    fun refresh(quickFilter: QuickFilter) {
        this.quickFilter = quickFilter
        resetVMParams(true)
        getSalesTransactions()
    }


    fun retry() {
        resetVMParams()
        getSalesTransactions()
    }

    private fun resetVMParams(toRefresh: Boolean = false){
        paginationObj = null
        isRefreshing = toRefresh
        isLastPage = false
        isLoadingMorePagingData = false
        page = ONE
    }


    fun isFilterNotSelected(quickFilter: QuickFilter?): Boolean {
        quickFilter?.let { filter ->
            return (filter.cardBrand.isNullOrEmpty() && filter.paymentType.isNullOrEmpty() &&
                    (filter.nsu.isNullOrEmpty() && filter.authorizationCode.isNullOrEmpty() &&
                            filter.tid.isNullOrEmpty() && filter.truncatedCardNumber.isNullOrEmpty() &&
                            filter.softDescriptor.isNullOrEmpty() && filter.initialAmount == null &&
                            filter.finalAmount == null))
        }
        return true
    }
    private fun getCancelFeatureToggle() {
        viewModelScope.launch {
            featureToggleUseCase(key = FeatureTogglePreference.EFETIVAR_CANCELAMENTO).onSuccess {
                cancelSaleFeatureToggle = it
            }
        }
    }

}