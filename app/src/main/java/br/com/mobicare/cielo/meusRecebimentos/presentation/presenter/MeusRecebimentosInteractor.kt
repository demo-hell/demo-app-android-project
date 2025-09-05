package br.com.mobicare.cielo.meusRecebimentos.presentation.presenter

import br.com.mobicare.cielo.meusrecebimentosnew.models.detailsummaryview.DetailSummaryViewResponse
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.SummaryViewResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.AlertsResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.FileResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.ReceivablesBankAccountsResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.SummaryResponse
import io.reactivex.Observable

interface MeusRecebimentosInteractor {

    fun onCalculationVision(initialDate: String, finalDate: String)
            : Observable<SummaryResponse>

    fun onLoadAlerts()
            : Observable<AlertsResponse>
    fun onGeneratePdfAlerts()
            : Observable<FileResponse>

    fun getSummaryView(
            url: String,
            initialDate: String?,
            finalDate: String?,
            cardBrands: List<Int>? = null,
            paymentTypes: List<Int>? = null,
            roNumber: String? = null,
            page: Int? = 1,
            pageSize: Int? = 25
    ): Observable<SummaryViewResponse>

    fun getDetailSummaryView(
            url: String,
            customId: String? = null,
            initialDate: String? = null,
            finalDate: String? = null,
            paymentTypeCode: List<Int>? = null,
            cardBrandCode: List<Int>? = null,
            authorizationCode: String? = null,
            nsu: Int? = null,
            operationNumber: String? = null,
            roNumber: String? = null,
            initialAmount: Double? = null,
            finalAmount: Double? = null,
            saleCode: String? = null,
            transactionId: String? = null,
            truncatedCardNumber: String? = null,
            terminal: String? = null,
            transactionTypeCode: Int? = null,
            merchantId: String? = null,
            page: Int? = 1,
            pageSize: Int? = 25): Observable<DetailSummaryViewResponse>

    fun getReceivablesBankAccounts(initialDate: String, finalDate: String)
            : Observable<ReceivablesBankAccountsResponse>
}