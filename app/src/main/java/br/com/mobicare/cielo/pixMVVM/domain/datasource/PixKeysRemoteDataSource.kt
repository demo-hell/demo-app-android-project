package br.com.mobicare.cielo.pixMVVM.domain.datasource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixKeysResponse
import br.com.mobicare.cielo.pixMVVM.domain.model.PixValidateKey

interface PixKeysRemoteDataSource {

    suspend fun getAllKeys(): CieloDataResult<PixKeysResponse>

    suspend fun getValidateKey(
        key: String,
        keyType: String
    ): CieloDataResult<PixValidateKey>

}