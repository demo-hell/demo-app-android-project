package br.com.mobicare.cielo.pixMVVM.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduleCancelRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferBankAccountRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferKeyRequest
import br.com.mobicare.cielo.pixMVVM.domain.model.PixSchedulingDetail
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferBank
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferResult

interface PixTransactionsRepository {
    suspend fun getTransferDetails(
        endToEndId: String?,
        transactionCode: String?,
    ): CieloDataResult<PixTransferDetail>

    suspend fun transferWithKey(
        otpCode: String?,
        request: PixTransferKeyRequest?,
    ): CieloDataResult<PixTransferResult>

    suspend fun transferToBankAccount(
        otpCode: String?,
        request: PixTransferBankAccountRequest?,
    ): CieloDataResult<PixTransferResult>

    suspend fun getTransferBanks(): CieloDataResult<List<PixTransferBank>>

    suspend fun cancelTransferSchedule(
        otpCode: String,
        request: PixScheduleCancelRequest,
    ): CieloDataResult<PixTransferResult>

    suspend fun getTransferScheduleDetail(schedulingCode: String?): CieloDataResult<PixSchedulingDetail>

    suspend fun transferScheduledBalance(otpCode: String?): CieloDataResult<Unit>
}
