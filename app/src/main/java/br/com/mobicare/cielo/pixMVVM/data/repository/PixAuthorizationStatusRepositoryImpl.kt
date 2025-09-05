package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixAuthorizationStatusRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixAuthorizationStatusRepository

class PixAuthorizationStatusRepositoryImpl(
    private val remoteDataSource: PixAuthorizationStatusRemoteDataSource
) : PixAuthorizationStatusRepository {

    override suspend fun getPixAuthorizationStatus() = remoteDataSource.getPixAuthorizationStatus()

}