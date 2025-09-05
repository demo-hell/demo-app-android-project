package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixAccountBalanceRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixAccountBalanceRepository

class PixAccountBalanceRepositoryImpl(
    private val remoteDataSource: PixAccountBalanceRemoteDataSource
) : PixAccountBalanceRepository {

    override suspend fun getAccountBalance() = remoteDataSource.getAccountBalance()

}