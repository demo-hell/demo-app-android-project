package br.com.mobicare.cielo.minhasVendas.datasource

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.mySales.data.model.responses.ResultPaymentTypes
import br.com.mobicare.cielo.mySales.data.model.responses.ResultSummaryCanceledSales
import br.com.mobicare.cielo.minhasVendas.domain.SellsCancelParametersRequest
import io.reactivex.Observable

class MinhasVendasDataSource(context: Context) {

    private val api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

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
            page: String? = null,
            pageSize: Int? = null)
            = this.api.getSummarySalesOnline(
            accessToken,
            authorization,
            initialDate,
            finalDate,
            cardBrand,
            paymentType,
            terminal,
            status,
            cardNumber,
            nsu,
            authorizationCode,
            page,
            pageSize)

    fun getSummarySalesHistory(
            accessToken: String,
            authorization: String,
            type: String,
            initialDate: String? = null,
            finalDate: String? = null,
            cardBrands: List<Int>? = null,
            paymentTypes: List<Int>? = null)
            = this.api.getSummarySalesHistory(
            accessToken,
            authorization,
            type,
            initialDate,
            finalDate,
            cardBrands,
            paymentTypes)

    fun getCardBrands(accessToken: String, authorization: String) = this.api.getCardBrands(accessToken, authorization)

    fun getPaymentTypes(
            accessToken: String,
            authorization: String,
            initialDate: String,
            finalDate: String
    ) = this.api.getPaymentTypes(accessToken, authorization, initialDate, finalDate)

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
            pageSize: Int? = null
    ) = this.api.getSummarySales(
            accessToken,
            authorization,
            initialDate,
            finalDate,
            initialAmount,
            finalAmount,
            customId,
            saleCode,
            truncatedCardNumber,
            cardBrands,
            paymentTypes,
            terminal,
            status,
            cardNumber,
            nsu,
            authorizationCode,
            page,
            pageSize)


        fun getCanceledSells(
                accessToken: String,
                sellsCancelParametersRequest: SellsCancelParametersRequest,
                pageNumber: Long?,
                pageSize: Int
        ): Observable<ResultSummaryCanceledSales> {
                return this.api.getCanceledSales(
                        accessToken,
                        sellsCancelParametersRequest,
                        pageNumber,
                        pageSize
                )
        }

    fun filterCanceledSells(accessToken: String, initialDate: String, finalDate: String):
            Observable<ResultPaymentTypes> {
        return this.api.filterCanceledSells(accessToken, initialDate, finalDate)
    }

}