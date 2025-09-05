package br.com.mobicare.cielo.pix.api.myLimits.timeManagement

import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.pix.api.PixAPI
import br.com.mobicare.cielo.pix.domain.PixTimeManagementRequest

class PixTimeManagementDataSource (private val api: PixAPI){

    private val authorization = Utils.authorization()

    fun getNightTime() = api.getNightTime(authorization)

    fun updateNightTime(
            otpCode: String?,
            body: PixTimeManagementRequest?
    ) = api.updateNightTime(authorization, otpCode, body)
}