package br.com.mobicare.cielo.pix.api.extract

import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse
import br.com.mobicare.cielo.pix.domain.PixExtractFilterRequest
import br.com.mobicare.cielo.pix.domain.PixExtractResponse
import io.reactivex.Observable

class PixExtractRepository(private val dataSource: PixExtractDataSource) : PixExtractRepositoryContract {

    private val authorization = Utils.authorization()

    override fun getExtract(pixExtractRequest: PixExtractFilterRequest): Observable<PixExtractResponse> {
        return pixExtractRequest.run {
            dataSource.getExtract(
                authorization,
                idEndToEnd,
                limit,
                startDate,
                endDate,
                receiptsTab,
                schedulingCode,
                period,
                transferType,
                cashFlowType
            )
        }
    }

    override fun getUserCardBalance(
        cardProxy: String,
        accessToken: String
    ): Observable<PrepaidBalanceResponse> =
        dataSource.getUserCardBalance(cardProxy, accessToken)
}