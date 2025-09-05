package br.com.mobicare.cielo.chargeback.domain.repository

import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackDocumentParams
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackDocumentSenderParams
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackListParams
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackDocument
import br.com.mobicare.cielo.chargeback.domain.model.ChargebackDocumentSender
import br.com.mobicare.cielo.chargeback.domain.model.Chargebacks
import br.com.mobicare.cielo.chargeback.domain.model.Lifecycle
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface ChargebackRepository {
    suspend fun getChargebackList(params: ChargebackListParams): CieloDataResult<Chargebacks>
    suspend fun getChargebackLifecycle(caseId: Int): CieloDataResult<List<Lifecycle>>
    suspend fun getChargebackDocument(params: ChargebackDocumentParams): CieloDataResult<ChargebackDocument>
    suspend fun getChargebackDocumentSender(params: ChargebackDocumentSenderParams) : CieloDataResult<ChargebackDocumentSender>
}