package br.com.mobicare.cielo.meusRecebimentos.presentation.presenter

import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.SummaryViewResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.AlertsResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.FileResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.MeusRecebimentosRepositoryNew
import br.com.mobicare.cielo.meusrecebimentosnew.repository.SummaryResponse
import io.reactivex.Observable

class MeusRecebimentosInteractorImpl : MeusRecebimentosInteractor {

    private val repository = MeusRecebimentosRepositoryNew()

    override fun onCalculationVision(
        initialDate: String,
        finalDate: String
    ): Observable<SummaryResponse> {
        return repository.onCalculationVision(initialDate, finalDate)
    }
    override fun getSummaryView(
        url: String,
        initialDate: String?,
        finalDate: String?,
        cardBrands: List<Int>?,
        paymentTypes: List<Int>?,
        roNumber: String?,
        page: Int?,
        pageSize: Int?
    ): Observable<SummaryViewResponse> {
        return repository.getSummaryView(
            url,
            initialDate,
            finalDate,
            cardBrands,
            paymentTypes,
            roNumber,
            page,
            pageSize
        )
    }

    override fun getDetailSummaryView(
        url: String,
        customId: String?,
        initialDate: String?,
        finalDate: String?,
        paymentTypeCode: List<Int>?,
        cardBrandCode: List<Int>?,
        authorizationCode: String?,
        nsu: Int?,
        operationNumber: String?,
        roNumber: String?,
        initialAmount: Double?,
        finalAmount: Double?,
        saleCode: String?,
        transactionId: String?,
        truncatedCardNumber: String?,
        terminal: String?,
        transactionTypeCode: Int?,
        merchantId: String?,
        page: Int?,
        pageSize: Int?
    ) = repository.getDetailSummaryView(
        url,
        customId,
        initialDate,
        finalDate,
        paymentTypeCode,
        cardBrandCode,
        authorizationCode,
        nsu,
        operationNumber,
        roNumber,
        initialAmount,
        finalAmount,
        saleCode,
        transactionId,
        truncatedCardNumber,
        terminal,
        transactionTypeCode,
        merchantId,
        page,
        pageSize
    )

    override fun getReceivablesBankAccounts(initialDate: String, finalDate: String) =
        repository.getReceivablesBankAccounts(initialDate, finalDate)

    override fun onLoadAlerts(): Observable<AlertsResponse> {
        return repository.onLoadAlerts()
    }
    override fun onGeneratePdfAlerts(): Observable<FileResponse> {
        return repository.onGeneratePdfAlerts()
    }
}