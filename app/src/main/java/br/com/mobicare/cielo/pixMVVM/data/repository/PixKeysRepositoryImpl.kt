package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixKeysRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.model.PixValidateKey
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixKeysRepository

class PixKeysRepositoryImpl(
    private val remoteDataSource: PixKeysRemoteDataSource
) : PixKeysRepository {

    override suspend fun getAllKeys() = remoteDataSource.getAllKeys()

    override suspend fun getValidateKey(
        key: String,
        keyType: String
    ): CieloDataResult<PixValidateKey> {
        return remoteDataSource.getValidateKey(key, keyType)
    }

}