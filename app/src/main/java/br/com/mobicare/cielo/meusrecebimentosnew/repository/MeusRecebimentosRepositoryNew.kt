package br.com.mobicare.cielo.meusrecebimentosnew.repository

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.CieloApplication.Companion.context
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.Utils
import io.reactivex.Observable

class MeusRecebimentosRepositoryNew {
    val api = CieloAPIServices.getInstance(context!!, BuildConfig.HOST_API)

    private val token = UserPreferences.getInstance().token
    private val authorization = Utils.authorization()

    fun onCalculationVision(initialDate: String, finalDate: String): Observable<SummaryResponse> {
        return api.getCaculationVision(authorization, token, initialDate, finalDate)
    }

    fun getSummaryView(
        url: String,
        initialDate: String?,
        finalDate: String?,
        cardBrands: List<Int>?,
        paymentTypes: List<Int>?,
        roNumber: String?,
        page: Int?,
        pageSize: Int?
    ) = api.getSummaryView(
        url,
        authorization,
        token,
        initialDate,
        finalDate,
        cardBrands,
        paymentTypes,
        roNumber,
        page,
        pageSize
    )

    fun getDetailSummaryView(
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
    ) = api.getDetailSummaryView(
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

    fun getReceivablesBankAccounts(initialDate: String, finalDate: String) =
        api.getReceivablesBankAccounts(initialDate, finalDate)

    fun onLoadAlerts()
            : Observable<AlertsResponse> = api.onLoadAlerts()
    fun onGeneratePdfAlerts()
            : Observable<FileResponse> = api.onGeneratePdfAlerts()
}