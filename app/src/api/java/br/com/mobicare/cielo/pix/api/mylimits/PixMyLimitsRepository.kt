package br.com.mobicare.cielo.pix.api.myLimits

import br.com.mobicare.cielo.pix.domain.PixMyLimitsRequest

class PixMyLimitsRepository(private val dataSource: PixMyLimitsDataSource) :
    PixMyLimitsRepositoryContract {

    override fun getLimits(
        serviceGroup: String?,
        beneficiaryType: String?
    ) = dataSource.getLimits(serviceGroup, beneficiaryType)

    override fun updateLimits(
            otpCode: String?,
            body: PixMyLimitsRequest?
    ) = dataSource.updateLimit(otpCode, body)
}