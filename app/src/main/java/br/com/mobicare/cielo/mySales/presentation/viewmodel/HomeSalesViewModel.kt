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
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef.CANCELADA
import br.com.mobicare.cielo.mySales.data.model.Pagination
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4.Companion.SCREEN_NAME_CANCELLATION_MY_CANCELLATIONS
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4.Companion.SCREEN_NAME_SALES_MADE
import br.com.mobicare.cielo.mySales.data.model.params.GetCanceledSalesParams
import br.com.mobicare.cielo.mySales.data.model.params.GetSalesDataParams
import br.com.mobicare.cielo.mySales.data.model.bo.CanceledSummarySalesBO
import br.com.mobicare.cielo.mySales.data.model.bo.SummarySalesBO
import br.com.mobicare.cielo.mySales.domain.usecase.GetCanceledSalesUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetGA4UseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetSalesUseCase
import br.com.mobicare.cielo.mySales.presentation.utils.MySalesViewState
import kotlinx.coroutines.launch

class HomeSalesViewModel(
    private val getSalesUseCase: GetSalesUseCase,
    private val getCanceledSalesUseCase: GetCanceledSalesUseCase,
    private val userPreferences: UserPreferences,
    private val userObjUseCase: GetUserObjUseCase,
    private  val getGA4UseCase: GetGA4UseCase
): ViewModel() {

    private val _getSalesDataViewState = MutableLiveData<MySalesViewState<SummarySalesBO>>()
    val getSalesDataViewState: LiveData<MySalesViewState<SummarySalesBO>>
        get() = _getSalesDataViewState


    private val _getCanceledSalesDataViewState = MutableLiveData<MySalesViewState<CanceledSummarySalesBO>>()
    val  getCanceledSalesDataViewState: LiveData<MySalesViewState<CanceledSummarySalesBO>>
        get() = _getCanceledSalesDataViewState

    var isRefreshing: Boolean = false
    private var lastSale: Sale? = null
    private lateinit var quickFilter: QuickFilter

    private var paginationInfoId: Long? = null //usado para vendas
    private var canceledSalesPaginationObj: Pagination? = null //usado para vendas canceladas
    private var canceledSalesPageNumber: Long? = ONE.toLong() //usado para vendas canceladas
    var isLoadingMorePagingData: Boolean = false
    var isLastPagingData: Boolean = false
    private var saleDataAlreadyLoaded: Boolean = false


    fun getSales(quickFilter: QuickFilter) {
        this.quickFilter = quickFilter

        if(isCanceledSales()) {
            getCanceledSalesData()
            getGA4UseCase.invoke(SCREEN_NAME_CANCELLATION_MY_CANCELLATIONS)
        }else {
            getGA4UseCase.invoke(SCREEN_NAME_SALES_MADE)
            getSalesData()
        }
    }

    fun isCanceledSales(): Boolean = quickFilter.status?.contains(CANCELADA) ?: false

    private fun getSalesData() {
        if(userPreferences.isConvivenciaUser) {
            viewModelScope.launch {
                val params = GetSalesDataParams(
                    accessToken = userPreferences.token,
                    authorization = Utils.authorization(),
                    page = paginationInfoId,
                    quickFilter = quickFilter
                )

                if(paginationInfoId != null)
                    _getSalesDataViewState.postValue(MySalesViewState.LOADING_MORE)
                else{
                    if(isRefreshing.not())
                        _getSalesDataViewState.postValue(MySalesViewState.LOADING)
                }

                getSalesUseCase.invoke(params)
                    .onSuccess { summarySalesBO ->
                        if(summarySalesBO.items.isEmpty() && !saleDataAlreadyLoaded){
                            _getSalesDataViewState.postValue(MySalesViewState.EMPTY)
                            return@launch
                        }

                        lastSale = summarySalesBO.items.lastOrNull()

                        if(paginationInfoId != null){
                            isLoadingMorePagingData = false
                            saleDataAlreadyLoaded = true
                            _getSalesDataViewState.postValue(MySalesViewState.SUCCESS_PAGINATION(summarySalesBO))
                        }
                        else{
                            _getSalesDataViewState.postValue(MySalesViewState.SUCCESS(summarySalesBO))
                            saleDataAlreadyLoaded = true
                        }

                    }
                    .onError { apiError ->
                        val newErrorMessage = apiError.apiException.newErrorMessage
                        newErrorHandler(
                            getUserObjUseCase = userObjUseCase,
                            newErrorMessage = newErrorMessage,
                            onErrorAction = { _getSalesDataViewState.postValue(MySalesViewState.ERROR(newErrorMessage = newErrorMessage)) }
                        )
                    }
                    .onEmpty {
                        _getSalesDataViewState.postValue(MySalesViewState.EMPTY)
                    }
                }
            }else {
                _getSalesDataViewState.postValue(MySalesViewState.ERROR_FULL_SCREEN())
            }
        }

    private fun getCanceledSalesData() {
        if(userPreferences.isConvivenciaUser) {
            viewModelScope.launch {
                val canceledSalesParams = GetCanceledSalesParams(
                    accessToken = userPreferences.token,
                    quickFilter = quickFilter,
                    page = canceledSalesPageNumber,
                )

                if(canceledSalesPaginationObj != null)
                    _getCanceledSalesDataViewState.postValue(MySalesViewState.LOADING_MORE)
                else
                    if(isRefreshing.not())
                        _getCanceledSalesDataViewState.postValue(MySalesViewState.LOADING)

                getCanceledSalesUseCase.invoke(canceledSalesParams)
                    .onSuccess { canceledSummarySalesBO ->
                        canceledSalesPaginationObj = canceledSummarySalesBO.pagination
                        canceledSalesPaginationObj?.let { pagination ->
                            if(pagination.firstPage == true)
                                _getCanceledSalesDataViewState.postValue(MySalesViewState.SUCCESS(canceledSummarySalesBO))
                            else
                                _getCanceledSalesDataViewState.postValue(MySalesViewState.SUCCESS_PAGINATION(canceledSummarySalesBO))
                        }
                    }
                    .onError { apiError ->
                        val newErrorMessage = apiError.apiException.newErrorMessage
                        newErrorHandler(
                            getUserObjUseCase = userObjUseCase,
                            newErrorMessage = newErrorMessage,
                            onHideLoading = { _getCanceledSalesDataViewState.postValue(MySalesViewState.HIDE_LOADING) },
                            onErrorAction = { _getCanceledSalesDataViewState.postValue(MySalesViewState.ERROR(newErrorMessage = newErrorMessage)) }
                        )
                    }
                    .onEmpty {
                        _getCanceledSalesDataViewState.postValue(MySalesViewState.EMPTY)
                    }
            }
        }else {
            _getCanceledSalesDataViewState.postValue(MySalesViewState.ERROR_FULL_SCREEN())
        }
    }

    fun loadMoreSalesData() {
        lastSale?.let {
            paginationInfoId = it.id?.toLong()
            if(paginationInfoId != null){
                isLoadingMorePagingData = true
                getSalesData()
            }else
                isLastPagingData = true
        }
    }

    fun loadMoreCanceledSalesData() {
        canceledSalesPaginationObj?.let { paginationObj ->
            if(paginationObj.lastPage == false){
                canceledSalesPageNumber = canceledSalesPageNumber?.plus(1)
                getCanceledSalesData()
            }
        }
    }

    fun retry() {
        resetVMParams()
        if(isCanceledSales())
            getCanceledSalesData()
        else
            getSalesData()
    }

    fun refresh(){
        resetVMParams(true)
        if(isCanceledSales())
            getCanceledSalesData()
        else
            getSalesData()
    }

    private fun resetVMParams(toRefresh: Boolean = false) {
        paginationInfoId = null
        isRefreshing = toRefresh
        lastSale = null
        paginationInfoId = null
        canceledSalesPaginationObj = null
        canceledSalesPageNumber = ONE.toLong()
        isLoadingMorePagingData = false
        isLastPagingData = false
        saleDataAlreadyLoaded = false
    }

}

