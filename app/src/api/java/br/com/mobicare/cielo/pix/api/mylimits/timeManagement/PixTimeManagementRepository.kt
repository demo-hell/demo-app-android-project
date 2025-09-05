package br.com.mobicare.cielo.pix.api.myLimits.timeManagement

import br.com.mobicare.cielo.pix.domain.PixTimeManagementRequest

class PixTimeManagementRepository(private val dataSource: PixTimeManagementDataSource):
PixTimeManagementRepositoryContract {

    override fun getNightTime() = dataSource.getNightTime()

    override fun updateNightTime(
            otpCode: String?,
            body: PixTimeManagementRequest?
    ) = dataSource.updateNightTime(otpCode, body)
}