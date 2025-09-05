package br.com.mobicare.cielo.arv.domain.repository

import br.com.mobicare.cielo.arv.data.model.request.ArvConfirmAnticipationRequest
import br.com.mobicare.cielo.arv.data.model.request.ArvConfirmScheduledAnticipationRequest
import br.com.mobicare.cielo.arv.data.model.request.ArvScheduledAnticipationCancelRequest
import br.com.mobicare.cielo.arv.data.model.request.ArvScheduledAnticipationContractRequest
import br.com.mobicare.cielo.arv.data.model.response.ArvConfirmAnticipationResponse
import br.com.mobicare.cielo.arv.data.model.response.ArvHistoricResponse
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.domain.model.ArvBank
import br.com.mobicare.cielo.arv.domain.model.ArvBranchesContracts
import br.com.mobicare.cielo.arv.domain.model.ArvOptIn
import br.com.mobicare.cielo.arv.domain.model.ArvScheduleContract
import br.com.mobicare.cielo.arv.domain.model.ArvScheduledAnticipation
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface ArvRepositoryNew {

    suspend fun getAnticipation(
        negotiationType: String?,
        receiveToday: Boolean
    ): CieloDataResult<ArvAnticipation>

    suspend fun getArvAnticipationHistory(
        negotiationType: String?,
        status: String?,
        initialDate: String?,
        finalDate: String?,
        page: Int,
        pageSize: Int,
        modalityType: String?,
        operationNumber: String?
    ): CieloDataResult<ArvHistoricResponse>

    suspend fun getArvSingleAnticipationWithDate(
        negotiationType: String? = null,
        initialDate: String? = null,
        finalDate: String? = null
    ): CieloDataResult<ArvAnticipation>

    suspend fun getArvSingleAnticipationWithValue(
        negotiationType: String? = null,
        amount: Double? = null,
        receiveToday: Boolean? = null,
        initialDate: String? = null,
        finalDate: String? = null,
    ): CieloDataResult<ArvAnticipation>

    suspend fun getArvBanks(): CieloDataResult<List<ArvBank>>

    suspend fun confirmAnticipation(
        request: ArvConfirmAnticipationRequest
    ): CieloDataResult<ArvConfirmAnticipationResponse>

    suspend fun getArvSingleAnticipationByBrands(
        negotiationType: String? = null,
        initialDate: String? = null,
        finalDate: String? = null,
        brandCodes: List<Int>? = null,
        acquirerCode: List<Int>? = null,
        receiveToday: Boolean? = null,
    ): CieloDataResult<ArvAnticipation>

    suspend fun getScheduledAnticipation(): CieloDataResult<ArvScheduledAnticipation>

    suspend fun confirmScheduledAnticipation(
        request: ArvConfirmScheduledAnticipationRequest
    ): CieloDataResult<Void>

    suspend fun cancelScheduledAnticipation(
        request: ArvScheduledAnticipationCancelRequest
    ): CieloDataResult<Void>

    suspend fun getOptInStatus(): CieloDataResult<ArvOptIn>

    suspend fun getScheduledAnticipationContract(
        request: ArvScheduledAnticipationContractRequest
    ): CieloDataResult<ArvScheduleContract>

    suspend fun getBranchesContracts(): CieloDataResult<ArvBranchesContracts>
}