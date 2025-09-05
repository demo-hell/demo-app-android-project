package br.com.mobicare.cielo.superlink.domain.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.superlink.data.model.response.PaymentLinkResponse

interface SuperLinkDataSource {

    suspend fun isPaymentLinkActive(): CieloDataResult<PaymentLinkResponse>

}