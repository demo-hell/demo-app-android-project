package br.com.mobicare.cielo.minhasVendas.fragments.filter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.mySales.data.model.CardBrand
import br.com.mobicare.cielo.mySales.data.model.PaymentType
import br.com.mobicare.cielo.mySales.data.model.responses.ResultCardBrands
import br.com.mobicare.cielo.mySales.data.model.responses.ResultPaymentTypes
import br.com.mobicare.cielo.minhasVendas.fragments.common.ItemSelectable
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import br.com.mobicare.cielo.minhasVendas.repository.MinhasVendasRepository

class MinhasVendasFilterBottomSheetPresenter(
        private val view: MinhasVendasFilterBottomSheetContract.View,
        private val repository: MinhasVendasRepository,
        userPreferences: UserPreferences) : MinhasVendasFilterBottomSheetContract.Presenter {

    private var brands: List<ItemSelectable<CardBrand>>? = null
    private var paymentTypes: List<ItemSelectable<PaymentType>>? = null
    private var quickFilter: QuickFilter? = null
    private var token = userPreferences.token
    private var authorization = Utils.authorization()

    var isCanceledFilters: Boolean = false
    var isMoreFilters: Boolean = false
    var isLoadPaymentsType: Boolean = false

    override fun onPause() {
        this.repository.disposable()
    }

    override fun onDestroy() {
        this.repository.onDestroy()
    }

    override fun load(quickFilter: QuickFilter, isLoadPaymentsType: Boolean) {
        this.quickFilter = quickFilter
        this.isLoadPaymentsType = isLoadPaymentsType
        this.view.loadingState()
        if (this.paymentTypes == null) {
            if (isLoadPaymentsType) {
                this.loadPaymentsType(quickFilter)
                return
            }
        }

        if (this.brands == null) {
            this.loadBrands()
            return
        }

        if (isCanceledFilters || isMoreFilters) {
            this.showData()
        }
    }

    override fun applyFilter(inputQuickFilter: QuickFilter?) {
        val cardBrandsSelected = this.brands?.filter { it.isSelected }?.map { it.data.code }
        var paymentTypesSelected = this.paymentTypes?.filter { it.isSelected }?.map { it.data.value.toInt() }

        this.quickFilter?.let {

            this.view.applyFilter(QuickFilter
                    .Builder()
                    .from(it)
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
            )
        }
    }

    override fun cleanFilter() {
        this.quickFilter?.let {
            this.view.applyFilter(QuickFilter
                    .Builder()
                    .from(it)
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
                    .build())
        }
    }

    private fun loadPaymentsType(filter: QuickFilter) {
        filter.initialDate?.let { itInitialDate ->
            filter.finalDate?.let { itFinalDate ->

                val resultPaymentTypeHandler = object : APICallbackDefault<ResultPaymentTypes, String> {
                    override fun onError(error: ErrorMessage) {
                        if (error.logout) {
                            this@MinhasVendasFilterBottomSheetPresenter.view.logout(error)
                        }
                        this@MinhasVendasFilterBottomSheetPresenter.loadBrands()
                    }

                    override fun onSuccess(response: ResultPaymentTypes) {
                        response.paymentTypes?.let {
                            this@MinhasVendasFilterBottomSheetPresenter.processResultPaymentTYPES(it)
                        }

                        if (isCanceledFilters.not() && isMoreFilters.not()) {
                            this@MinhasVendasFilterBottomSheetPresenter.loadBrands()
                        } else {
                            if (response.cardBrands?.size != 0) {
                                loadCanceledCardBrands(response.cardBrands?.map {
                                    CardBrand(it.code?.toInt()!!, it.name!!)
                                })
                            } else {
                                showData()
                            }

                        }
                    }
                }

                if (isCanceledFilters.not()) {
                    this.repository.getPaymentTypes( 
                            this.token,
                            this.authorization,
                            itInitialDate,
                            itFinalDate,
                            callback = resultPaymentTypeHandler)

                } else {
                    this.repository.filterCanceledSells(this.token, itInitialDate, itFinalDate,
                            resultPaymentTypeHandler)
                }
            }
        }
    }

    fun loadBrands() {
        this.repository.getCardBrands(
                this.token,
                this.authorization,
                callback = object : APICallbackDefault<ResultCardBrands, String> {
                    override fun onError(error: ErrorMessage) {
                        if (error.logout) {
                            this@MinhasVendasFilterBottomSheetPresenter.view.logout(error)
                        }
                        showData()
                    }

                    override fun onSuccess(response: ResultCardBrands) {
                        response.cardBrands?.let {
                            this@MinhasVendasFilterBottomSheetPresenter.processResultCardBrands(it)
                        }
                    }
                })
    }

    private fun loadCanceledCardBrands(cardBrands: List<CardBrand>?) {
        cardBrands?.run {
            this@MinhasVendasFilterBottomSheetPresenter.processResultCardBrands(this)
        }
    }

    private fun processResultPaymentTYPES(result: List<PaymentType>) {
        val paymentTypesApi = result.map { ItemSelectable(it) }
        this.quickFilter?.let { itFilter ->
            itFilter.paymentType?.forEach { itCode ->
                val filtered = paymentTypesApi.filter { it.data.value.toInt() == itCode }
                if (filtered.isNotEmpty()) {
                    filtered.first().isSelected = true
                }
            }
        }
        this.paymentTypes = paymentTypesApi
    }

    private fun processResultCardBrands(result: List<CardBrand>) {
        val brandsApi = result.map { ItemSelectable(it) }
        this.quickFilter?.let { filter ->
            filter.cardBrand?.let { itCards ->
                itCards.forEach { itCard ->
                    val filtered = brandsApi.filter { it.data.code == itCard }
                    if (filtered.isNotEmpty()) {
                        filtered.first().isSelected = true
                    }
                }
            }
        }
        this.brands = brandsApi
        this.showData()
    }

    private fun showData() {
        this.view.hideLoading()
        if (this.paymentTypes.isNullOrEmpty() && this.brands.isNullOrEmpty() && isCanceledFilters.not() && isMoreFilters.not()) {
            this.view.showError() 
            return
        }

        this.paymentTypes?.let {
            if (it.isNotEmpty()) {
                this.view.showPaymentTypes(it)
            }
        }

        this.brands?.let {
            if (it.isNotEmpty()) {
                this.view.showCardBrands(it) 
            }
        }

        if (isCanceledFilters || isMoreFilters) {
            this.view.loadMoreFilters(quickFilter) 
            this.view.showCancelInputs() 
        }

        if (isLoadPaymentsType.not())
            this.view.loadNsuAndAuthorizationCode(quickFilter) 
    }

}