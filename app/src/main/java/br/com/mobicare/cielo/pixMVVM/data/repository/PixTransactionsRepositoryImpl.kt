package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixScheduleCancelRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferBankAccountRequest
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixTransferKeyRequest
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixTransactionsRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixTransactionsRepository

class PixTransactionsRepositoryImpl(
    private val remoteDataSource: PixTransactionsRemoteDataSource,
) : PixTransactionsRepository {
    override suspend fun getTransferDetails(
        endToEndId: String?,
        transactionCode: String?,
    ) = remoteDataSource.getTransferDetails(endToEndId, transactionCode)

    override suspend fun transferWithKey(
        otpCode: String?,
        request: PixTransferKeyRequest?,
    ) = remoteDataSource.transferWithKey(otpCode, request)

    override suspend fun transferToBankAccount(
        otpCode: String?,
        request: PixTransferBankAccountRequest?,
    ) = remoteDataSource.transferToBankAccount(otpCode, request)

    override suspend fun getTransferBanks() = remoteDataSource.getTransferBanks()

    override suspend fun cancelTransferSchedule(
        otpCode: String,
        request: PixScheduleCancelRequest,
    ) = remoteDataSource.cancelTransferSchedule(otpCode, request)

    override suspend fun getTransferScheduleDetail(schedulingCode: String?) = remoteDataSource.getTransferScheduleDetail(schedulingCode)

    override suspend fun transferScheduledBalance(otpCode: String?): CieloDataResult<Unit> =
        remoteDataSource.transferScheduledBalance(otpCode)
}
