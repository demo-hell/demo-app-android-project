package br.com.mobicare.cielo.superlink.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.superlink.data.model.response.PaymentLinkResponse

interface SuperLinkRepository {

    suspend fun isPaymentLinkActive(): CieloDataResult<PaymentLinkResponse>

}