package br.com.mobicare.cielo.pix.api.keys

import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.pix.api.PixAPI
import br.com.mobicare.cielo.pix.domain.CreateKeyRequest
import br.com.mobicare.cielo.pix.domain.PixKeyDeleteRequest
import br.com.mobicare.cielo.pix.domain.ValidateCode

class PixKeysDataSource(private val api: PixAPI) {

    private val authorization = Utils.authorization()

    fun getKeys() = api.getKeys(authorization)

    fun createKey(otpCode: String?, body: CreateKeyRequest) =
        api.createKey(authorization, otpCode, body)

    fun validateKey(key: String?, type: String?) =
        api.validateKey(authorization, key, type)

    fun deleteKey(otpCode: String?, body: PixKeyDeleteRequest) =
        api.deleteKey(authorization, otpCode, body)

    fun requestValidateCode(body: ValidateCode) =
        api.requestValidateCode(authorization, body)

}