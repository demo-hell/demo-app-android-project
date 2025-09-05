package br.com.mobicare.cielo.minhasVendas.repository

import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.minhasVendas.datasource.MinhasVendasDataSource
import br.com.mobicare.cielo.minhasVendas.domain.*
import br.com.mobicare.cielo.mySales.data.model.responses.ResultCardBrands
import br.com.mobicare.cielo.mySales.data.model.responses.ResultPaymentTypes
import br.com.mobicare.cielo.mySales.data.model.responses.ResultSummaryCanceledSales
import br.com.mobicare.cielo.mySales.data.model.responses.ResultSummarySalesHistory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MinhasVendasRepository(private val dataSource: MinhasVendasDataSource) : DisposableDefault {

    private var compositeDisp = CompositeDisposable()

    override fun disposable() {
        compositeDisp.clear()
    }

    override fun onDestroy() {
        compositeDisp.dispose()
    }

    fun getSummarySalesOnline(
            accessToken: String,
            authorization: String,
            initialDate: String? = null,
            finalDate: String? = null,
            cardBrand: List<Int>? = null,
            paymentType: List<Int>? = null,
            terminal: List<String>? = null,
            status: List<Int>? = null,
            cardNumber: Int? = null,
            nsu: String? = null,
            authorizationCode: String? = null,
            page: Long? = null,
            pageSize: Int? = null,
            callback: APICallbackDefault<ResultSummarySales, String>) {
        compositeDisp.add(dataSource.getSummarySalesOnline(accessToken, authorization, initialDate, finalDate, cardBrand, paymentType, terminal,
                status, cardNumber, nsu, authorizationCode, if (page == null) null else page.toString(), pageSize)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    if (!compositeDisp.isDisposed) {
                        callback.onSuccess(it)
                    }
                }, {
                    if (!compositeDisp.isDisposed) {
                        val errorMessage = APIUtils.convertToErro(it)
                        callback.onError(errorMessage)
                    }
                }))
    }

    private fun getType(initialDate: String?, cardBrands: List<Int>?, paymentTypes: List<Int>?): String {
        if (initialDate != null && cardBrands == null && paymentTypes == null)
            return "DATE"
        if (cardBrands != null)
            return "CARD_BRAND"
        if (paymentTypes != null)
            return "PAYMENT_TYPE"
        return "DATE"
    }

    fun getSummarySalesHistory(
            accessToken: String,
            authorization: String,
            initialDate: String? = null,
            finalDate: String? = null,
            cardBrands: List<Int>? = null,
            paymentTypes: List<Int>? = null,
            callback: APICallbackDefault<ResultSummarySalesHistory, String>) {
        compositeDisp.add(dataSource.getSummarySalesHistory(
                accessToken,
                authorization,
                "DATE",
                initialDate,
                finalDate,
                cardBrands,
                paymentTypes)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    if (!compositeDisp.isDisposed) {
                        callback.onSuccess(it)
                    }
                }, {
                    if (!compositeDisp.isDisposed) {
                        val errorMessage = APIUtils.convertToErro(it)
                        callback.onError(errorMessage)
                    }
                })
        )
    }

    fun getSummarySales(
            accessToken: String,
            authorization: String,
            initialDate: String,
            finalDate: String,
            initialAmount: Double? = null,
            finalAmount: Double? = null,
            customId: String? = null,
            saleCode: String? = null,
            truncatedCardNumber: String? = null,
            cardBrands: List<Int>? = null,
            paymentTypes: List<Int>? = null,
            terminal: List<String>? = null,
            status: List<Int>? = null,
            cardNumber: Int? = null,
            nsu: String? = null,
            authorizationCode: String? = null,
            page: Int? = null,
            pageSize: Int? = null,
            callback: APICallbackDefault<ResultSummarySales, String>) {
        compositeDisp.add(dataSource.getSummarySales(accessToken, authorization, initialDate, finalDate, initialAmount, finalAmount,
                customId, saleCode, truncatedCardNumber, cardBrands, paymentTypes, terminal, status, cardNumber, nsu, authorizationCode,
                page, pageSize)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    if (!compositeDisp.isDisposed) {
                        callback.onSuccess(it)
                    }
                }, {
                    if (!compositeDisp.isDisposed) {
                        val errorMessage = APIUtils.convertToErro(it)
                        callback.onError(errorMessage)
                    }
                }))
    }

    fun getCardBrands(accessToken: String, authorization: String, callback: APICallbackDefault<ResultCardBrands, String>) {
        compositeDisp.add(
                dataSource.getCardBrands(accessToken, authorization)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe { callback.onStart() }
                        .subscribe({
                            if (!compositeDisp.isDisposed) {
                                callback.onSuccess(it)
                            }
                        }, {
                            if (!compositeDisp.isDisposed) {
                                val errorMessage = APIUtils.convertToErro(it)
                                callback.onError(errorMessage)
                            }
                        })
        )
    }

    fun getPaymentTypes(
            accessToken: String,
            authorization: String,
            initialDate: String,
            finalDate: String,
            callback: APICallbackDefault<ResultPaymentTypes, String>) {
        compositeDisp.add(
                dataSource.getPaymentTypes(accessToken, authorization, initialDate, finalDate)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe { callback.onStart() }
                        .subscribe({
                            if (!compositeDisp.isDisposed) {
                                callback.onSuccess(it)
                            }
                        }, {
                            if (!compositeDisp.isDisposed) {
                                val errorMessage = APIUtils.convertToErro(it)
                                callback.onError(errorMessage)
                            }
                        })
        )
    }

    fun getCanceledSells(
        accessToken: String,
        sellsCancelParametersRequest: SellsCancelParametersRequest,
        callback: APICallbackDefault<ResultSummaryCanceledSales, String>,
        pageNumber: Long?,
        pageSize: Int = TWENTY_FIVE
    ) {
        compositeDisp.add(
            dataSource.getCanceledSells(
                accessToken,
                sellsCancelParametersRequest,
                pageNumber,
                pageSize
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                })
        )
    }


    fun filterCanceledSells(accessToken: String,
                            initialDate: String,
                            finalDate: String,
                            callback: APICallbackDefault<ResultPaymentTypes, String>) {
        compositeDisp.add(
                dataSource.filterCanceledSells(accessToken, initialDate, finalDate)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe { callback.onStart() }
                        .subscribe({
                            callback.onSuccess(it)
                        }, {
                            val errorMessage = APIUtils.convertToErro(it)
                            callback.onError(errorMessage)
                        })
        )
    }

}