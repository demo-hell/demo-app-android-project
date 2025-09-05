package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.pixMVVM.data.model.request.PixRefundCreateRequest
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixRefundsRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixRefundsRepository

class PixRefundsRepositoryImpl(
    private val remoteDataSource: PixRefundsRemoteDataSource
): PixRefundsRepository {

    override suspend fun getReceipts(idEndToEndOriginal: String?) =
        remoteDataSource.getReceipts(idEndToEndOriginal)

    override suspend fun getDetail(transactionCode: String?) =
        remoteDataSource.getDetail(transactionCode)

    override suspend fun getDetailFull(transactionCode: String?) =
        remoteDataSource.getDetailFull(transactionCode)

    override suspend fun refund(otpCode: String?, request: PixRefundCreateRequest?) =
        remoteDataSource.refund(otpCode, request)

}