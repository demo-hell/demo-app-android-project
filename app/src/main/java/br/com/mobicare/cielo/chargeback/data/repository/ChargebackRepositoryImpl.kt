package br.com.mobicare.cielo.chargeback.data.repository

import br.com.mobicare.cielo.chargeback.data.datasource.ChargebackRemoteDataSource
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackDocumentParams
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackDocumentSenderParams
import br.com.mobicare.cielo.chargeback.data.model.request.ChargebackListParams
import br.com.mobicare.cielo.chargeback.domain.model.Chargebacks
import br.com.mobicare.cielo.chargeback.domain.repository.ChargebackRepository
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class ChargebackRepositoryImpl(
    private val chargebackRemoteDataSource: ChargebackRemoteDataSource
) : ChargebackRepository {

    override suspend fun getChargebackList(params: ChargebackListParams): CieloDataResult<Chargebacks> =
        chargebackRemoteDataSource.getChargebackList(params)

    override suspend fun getChargebackLifecycle(caseId: Int) =
        chargebackRemoteDataSource.getChargebackLifecycle(caseId)

    override suspend fun getChargebackDocument(params: ChargebackDocumentParams) =
        chargebackRemoteDataSource.getChargebackDocument(params)

    override suspend fun getChargebackDocumentSender(params: ChargebackDocumentSenderParams) =
        chargebackRemoteDataSource.getChargebackDocumentSender(params)

}