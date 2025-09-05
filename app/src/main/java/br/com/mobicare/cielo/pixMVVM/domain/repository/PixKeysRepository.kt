package br.com.mobicare.cielo.pixMVVM.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixKeysResponse
import br.com.mobicare.cielo.pixMVVM.domain.model.PixValidateKey

interface PixKeysRepository {

    suspend fun getAllKeys(): CieloDataResult<PixKeysResponse>

    suspend fun getValidateKey(
        key: String,
        keyType: String
    ): CieloDataResult<PixValidateKey>

}