package br.com.mobicare.cielo.arv.data.repository

import br.com.mobicare.cielo.arv.data.datasource.ArvRemoteDataSource
import br.com.mobicare.cielo.arv.data.model.request.ArvConfirmAnticipationRequest
import br.com.mobicare.cielo.arv.data.model.request.ArvConfirmScheduledAnticipationRequest
import br.com.mobicare.cielo.arv.data.model.request.ArvScheduledAnticipationCancelRequest
import br.com.mobicare.cielo.arv.data.model.request.ArvScheduledAnticipationContractRequest
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.model.ArvBank
import br.com.mobicare.cielo.arv.domain.model.ArvBranchesContracts
import br.com.mobicare.cielo.arv.domain.model.ArvOptIn
import br.com.mobicare.cielo.arv.domain.model.ArvScheduleContract
import br.com.mobicare.cielo.arv.domain.model.ArvScheduledAnticipation
import br.com.mobicare.cielo.arv.domain.repository.ArvRepositoryNew
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

class ArvRepositoryNewImpl(
    private val arvRemoteDataSource: ArvRemoteDataSource
) : ArvRepositoryNew {

    override suspend fun getAnticipation(
        negotiationType: String?,
        receiveToday: Boolean
    ): CieloDataResult<ArvAnticipation> =
        arvRemoteDataSource.getAnticipation(negotiationType, receiveToday)

    override suspend fun getArvAnticipationHistory(
        negotiationType: String?,
        status: String?,
        initialDate: String?,
        finalDate: String?,
        page: Int,
        pageSize: Int,
        modalityType: String?,
        operationNumber: String?
    ) = arvRemoteDataSource.getNegotiations(
        negotiationType,
        status,
        initialDate,
        finalDate,
        page,
        pageSize,
        modalityType,
        operationNumber
    )

    override suspend fun getArvSingleAnticipationWithDate(
        negotiationType: String?,
        initialDate: String?,
        finalDate: String?
    ): CieloDataResult<ArvAnticipation> =
        arvRemoteDataSource.getArvSingleAnticipationWithDate(
            negotiationType,
            initialDate,
            finalDate
        )

    override suspend fun getArvSingleAnticipationWithValue(
        negotiationType: String?,
        amount: Double?,
        receiveToday: Boolean?,
        initialDate: String?,
        finalDate: String?,
    ): CieloDataResult<ArvAnticipation> =
        arvRemoteDataSource.getArvSingleAnticipationWithValue(negotiationType, amount, receiveToday, initialDate, finalDate)


    override suspend fun getArvBanks(): CieloDataResult<List<ArvBank>> =
        arvRemoteDataSource.getBanks()

    override suspend fun confirmAnticipation(request: ArvConfirmAnticipationRequest) =
        arvRemoteDataSource.confirmAnticipation(request)

    override suspend fun getArvSingleAnticipationByBrands(
        negotiationType: String?,
        initialDate: String?,
        finalDate: String?,
        brandCodes: List<Int>?,
        acquirerCode: List<Int>?,
        receiveToday: Boolean?,
    ): CieloDataResult<ArvAnticipation> =
        arvRemoteDataSource.getArvSingleAnticipationByBrands(
            negotiationType, initialDate, finalDate, brandCodes, acquirerCode, receiveToday
        )

    override suspend fun getScheduledAnticipation(): CieloDataResult<ArvScheduledAnticipation> =
        arvRemoteDataSource.getScheduledAnticipation()

    override suspend fun confirmScheduledAnticipation(
        request: ArvConfirmScheduledAnticipationRequest
    ) = arvRemoteDataSource.confirmScheduledAnticipation(request)

    override suspend fun cancelScheduledAnticipation(
        request: ArvScheduledAnticipationCancelRequest
    ) = arvRemoteDataSource.cancelScheduledAnticipation(request)

    override suspend fun getOptInStatus(): CieloDataResult<ArvOptIn> {
        return arvRemoteDataSource.getOptInStatus()
    }

    override suspend fun getScheduledAnticipationContract(request: ArvScheduledAnticipationContractRequest): CieloDataResult<ArvScheduleContract> {
        return arvRemoteDataSource.getArvScheduledContract(
            request
        )
    }

    override suspend fun getBranchesContracts(): CieloDataResult<ArvBranchesContracts> {
        return arvRemoteDataSource.getBranchContracts()
    }
}