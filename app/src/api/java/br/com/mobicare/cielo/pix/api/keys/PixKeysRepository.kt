package br.com.mobicare.cielo.pix.api.keys

import br.com.mobicare.cielo.pix.domain.CreateKeyRequest
import br.com.mobicare.cielo.pix.domain.PixKeyDeleteRequest
import br.com.mobicare.cielo.pix.domain.ValidateCode

class PixKeysRepository(private val dataSource: PixKeysDataSource) : PixKeysRepositoryContract {

    override fun getKeys() =
        dataSource.getKeys()

    override fun createKey(otpCode: String?, body: CreateKeyRequest) =
        dataSource.createKey(otpCode, body)

    override fun validateKey(key: String?, type: String?) =
        dataSource.validateKey(key, type)

    override fun deleteKey(otpCode: String?, body: PixKeyDeleteRequest) =
        dataSource.deleteKey(otpCode, body)

    override fun requestValidateCode(body: ValidateCode) =
        dataSource.requestValidateCode(body)
}