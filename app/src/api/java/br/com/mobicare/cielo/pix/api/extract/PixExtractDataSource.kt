package br.com.mobicare.cielo.pix.api.extract

import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse
import br.com.mobicare.cielo.pix.api.PixAPI
import br.com.mobicare.cielo.pix.domain.ReceiptsTab
import io.reactivex.Observable

class PixExtractDataSource(private val api: PixAPI) {

    fun getExtract(
        authorization: String,
        idEndToEnd: String?,
        limit: Int?,
        startDate: String?,
        endDate: String?,
        receiptsTab: ReceiptsTab,
        schedulingCode: String?,
        period: String?,
        transferType: String?,
        cashFlowType: String?
    ) =
        api.getExtract(
            authorization,
            idEndToEnd = idEndToEnd,
            limit = limit,
            startDate = startDate,
            endDate = endDate,
            receiptsTab = receiptsTab.value,
            schedulingCode = schedulingCode,
            period = period,
            transferType = transferType,
            cashFlowType = cashFlowType
        )

    fun getUserCardBalance(
        cardProxy: String,
        accessToken: String
    ): Observable<PrepaidBalanceResponse> =
        api.fetchUserCardBalance(cardProxy, accessToken)
}