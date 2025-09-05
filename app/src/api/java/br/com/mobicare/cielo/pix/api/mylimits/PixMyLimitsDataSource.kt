package br.com.mobicare.cielo.pix.api.myLimits

import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.pix.api.PixAPI
import br.com.mobicare.cielo.pix.domain.PixMyLimitsRequest

class PixMyLimitsDataSource (private val api: PixAPI) {

    private val authorization = Utils.authorization()

    fun getLimits(
        serviceGroup: String?,
        beneficiaryType: String?
    ) = api.getLimits(authorization, beneficiaryType, serviceGroup)

    fun updateLimit(
            otpCode: String?,
            body: PixMyLimitsRequest?
    ) = api.updateLimit(authorization, otpCode, body)
}