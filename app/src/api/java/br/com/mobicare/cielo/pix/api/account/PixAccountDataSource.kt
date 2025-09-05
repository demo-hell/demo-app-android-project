package br.com.mobicare.cielo.pix.api.account

import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.pix.api.PixAPI
import br.com.mobicare.cielo.pix.domain.PixProfileRequest

class PixAccountDataSource(private val api: PixAPI) {

    private val authorization = Utils.authorization()

    fun getMerchant() = api.getMerchant(authorization)

    fun getProfile() = api.getProfile(authorization)

    fun updateProfile(
        otpCode: String,
        body: PixProfileRequest?
    ) = api.updateProfile(authorization, otpCode, body)
}