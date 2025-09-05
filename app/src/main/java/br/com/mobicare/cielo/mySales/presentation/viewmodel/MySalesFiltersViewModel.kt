package br.com.mobicare.cielo.mySales.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.mySales.data.model.CardBrand
import br.com.mobicare.cielo.mySales.data.model.PaymentType
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.mySales.data.model.params.GetBrandsSalesFiltersParams
import br.com.mobicare.cielo.mySales.data.model.params.ItemSelectable
import br.com.mobicare.cielo.mySales.data.model.bo.ResultPaymentTypesBO
import br.com.mobicare.cielo.mySales.domain.usecase.GetCardBrandsUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetFilteredCanceledSellsUseCase
import br.com.mobicare.cielo.mySales.domain.usecase.GetPaymentTypeUseCase
import kotlinx.coroutines.launch


class MySalesFiltersViewModel(
    private val paymentTypeFilterUseCase: GetPaymentTypeUseCase,
    private val cardBrandFilterUseCase: GetCardBrandsUseCase,
    private val canceledSalesFilter: GetFilteredCanceledSellsUseCase,
    private val userPreferences: UserPreferences
): ViewModel() {


    //region - Variaveis de livedata

    private val _paymentTypeLiveData = MutableLiveData<List<ItemSelectable<PaymentType>>?>()
    val paymentTypeLiveData get() = _paymentTypeLiveData

    private val _brandsLiveData = MutableLiveData<List<ItemSelectable<CardBrand>>?>()
    val brandsLiveData get() = _brandsLiveData

    private val _error = MutableLiveData<Unit>()
    val error get() = _error


    private val _loadNsuLiveData = MutableLiveData<QuickFilter>()
    val loadNsuLiveData get() = _loadNsuLiveData

    private val _loadMoreFiltersAndShowCancelInputsLiveData = MutableLiveData<QuickFilter>()
    val loadMoreFiltersAndShowCancelInputsLiveData get() = _loadMoreFiltersAndShowCancelInputsLiveData

    private val _loadingLiveData = MutableLiveData<Unit>()
    val loadingLiveData get() = _loadingLiveData

    private val _applyFilterLiveData = MutableLiveData<QuickFilter>()
    val applyFilterLiveData get() = _applyFilterLiveData


    private val _hideLoading = MutableLiveData<Unit>()
    val hideLoading get() = _hideLoading

    //endregion

    private var brands: List<ItemSelectable<CardBrand>>? = null
    private var paymentTypes: List<ItemSelectable<PaymentType>>? = null
    private lateinit var quickFilter: QuickFilter

    var isCanceledFilters: Boolean = false
    var isMoreFilters: Boolean = false
    var isLoadPaymentsType: Boolean = false

    fun getFilters(quickFilter: QuickFilter, isLoadPaymentType: Boolean) {
        this.quickFilter = quickFilter
        this.isLoadPaymentsType = isLoadPaymentType
        loadingLiveData.postValue(Unit)

        if (paymentTypes.isNullOrEmpty() && isLoadPaymentType) {
            processFilters()
            return
        }

        if (brands.isNullOrEmpty()){
            getCardBrandsFromService()
            return
        }

        if (isCanceledFilters || isMoreFilters) {
            showData()
        }
    }


    fun applyFilter(inputQuickFilter: QuickFilter?) {
        val cardBrandsSelected = brands?.filter { it.isSelected }?.map { it.data.code }
        val paymentTypesSelected = paymentTypes?.filter { it.isSelected }?.map { it.data.value.toInt() }

        val newQuickFilter =   QuickFilter
            .Builder()
            .from(quickFilter)
            .cardBrand(cardBrandsSelected)
            .paymentType(paymentTypesSelected)
            .initialAmount(inputQuickFilter?.initialAmount)
            .finalAmount(inputQuickFilter?.finalAmount)
            .grossAmount(inputQuickFilter?.grossAmount)
            .saleGrossAmount(inputQuickFilter?.saleGrossAmount)
            .authorizationCode(inputQuickFilter?.authorizationCode)
            .softDescriptor(inputQuickFilter?.softDescriptor)
            .truncatedCardNumber(inputQuickFilter?.truncatedCardNumber)
            .cardNumber(inputQuickFilter?.cardNumber)
            .nsu(inputQuickFilter?.nsu)
            .tid(inputQuickFilter?.tid)
            .build()

        _applyFilterLiveData.postValue(newQuickFilter)
    }


    fun clearFilter(): QuickFilter {
        return QuickFilter
            .Builder()
            .from(quickFilter)
            .initialAmount(null)
            .finalAmount(null)
            .cardBrand(null)
            .paymentType(null)
            .saleGrossAmount(null)
            .grossAmount(null)
            .authorizationCode(null)
            .softDescriptor(null)
            .cardNumber(null)
            .truncatedCardNumber(null)
            .nsu(null)
            .tid(null)
            .build()
    }

    private fun getPaymentTypesFromService() {
        viewModelScope.launch {
            val params = GetBrandsSalesFiltersParams(
                accessToken = userPreferences.token,
                authorization = Utils.authorization(),
                quickFilter = quickFilter
            )
            paymentTypeFilterUseCase.invoke(params)
                .onSuccess {
                    processAPIResult(it)
                }
                .onError {
                    getCardBrandsFromService()
                }
        }
    }


    private fun getCardBrandsFromService() {
        viewModelScope.launch {
            val params = GetBrandsSalesFiltersParams(
                accessToken = userPreferences.token,
                authorization = Utils.authorization(),
                quickFilter = quickFilter
            )
            cardBrandFilterUseCase.invoke(params)
                .onSuccess {
                    processResultCardBrands(it.cardBrands)

                }
                .onError {
                    showData()
                }
        }
    }

    private fun getCanceledSellsFromService() {
        viewModelScope.launch {
            val params = GetBrandsSalesFiltersParams(
                accessToken = userPreferences.token,
                authorization = Utils.authorization(),
                quickFilter = quickFilter
            )
            canceledSalesFilter.invoke(params)
                .onSuccess {
                    processAPIResult(it)
                }.onError {
                    getCardBrandsFromService()
                }
        }
    }

    private fun processAPIResult(data: ResultPaymentTypesBO){
        data.paymentTypes.let {
            processResultPaymentTypes(it)
        }
        if(isCanceledFilters.not() && isMoreFilters.not())
            getCardBrandsFromService()
        else{
            val responseCardBrands = data.cardBrands
            if(responseCardBrands.isNullOrEmpty().not()){
                val listOfBrands = responseCardBrands?.map { CardBrand(it.code?.toInt()!!,it.name!!) }
                processResultCardBrands(listOfBrands)
            }else
                showData()
        }
    }

    private fun processFilters() {
        if(isCanceledFilters.not()){
            getPaymentTypesFromService()
        }
        else{
            getCanceledSellsFromService()
        }
    }

    private fun showData(){
        _hideLoading.postValue(Unit)
        if(paymentTypes.isNullOrEmpty() && brands.isNullOrEmpty() && isCanceledFilters.not() && isMoreFilters.not()){
            _error.postValue(Unit)
        }

        if(paymentTypes?.isNotEmpty() == true){
            _paymentTypeLiveData.postValue(paymentTypes)
        }

        if(brands?.isNotEmpty() == true){
            _brandsLiveData.postValue(brands)
        }

        if(isCanceledFilters || isMoreFilters){
            _loadMoreFiltersAndShowCancelInputsLiveData.postValue(quickFilter)
        }

        if(isLoadPaymentsType.not()) {
            _loadNsuLiveData.postValue(quickFilter)
        }
    }

    private fun processResultCardBrands(result: List<CardBrand>?){
        result?.let{ cardBrands ->
            val brandsApi = cardBrands.map { ItemSelectable(it) }
            quickFilter.cardBrand?.let { itCards ->
                itCards.forEach{ itCard ->
                    val filtered = brandsApi.filter { it.data.code == itCard }
                    if(filtered.isNotEmpty())
                        filtered.first().isSelected = true
                }
            }
            brands = brandsApi
        }
        showData()
    }

    private fun processResultPaymentTypes(result: List<PaymentType>?) {
        result?.let { paymentTypesResult ->
            val paymentTypesApi = paymentTypesResult.map { ItemSelectable(it) }
            quickFilter.paymentType?.forEach { code ->
                val filtered = paymentTypesApi.filter { it.data.value.toInt() == code }
                if(filtered.isNotEmpty())
                    filtered.first().isSelected = true
            }
            paymentTypes = paymentTypesApi
        }
    }
}