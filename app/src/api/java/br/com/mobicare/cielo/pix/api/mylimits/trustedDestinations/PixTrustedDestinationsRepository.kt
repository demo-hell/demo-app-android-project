package br.com.mobicare.cielo.pix.api.myLimits.trustedDestinations

import br.com.mobicare.cielo.pix.domain.PixAddNewTrustedDestinationRequest
import br.com.mobicare.cielo.pix.domain.PixDeleteTrustedDestinationRequest

class PixTrustedDestinationsRepository(private val dataSource: PixTrustedDestinationsDataSource) :
    PixTrustedDestinationsRepositoryContract {

    override fun getTrustedDestinations(servicesGroup: String?) =
        dataSource.getTrustedDestinations(servicesGroup)

    override fun addNewTrustedDestination(
        otp: String?,
        body: PixAddNewTrustedDestinationRequest
    ) = dataSource.addNewTrustedDestination(otp, body)

    override fun deleteTrustedDestination(
        otp: String?,
        body: PixDeleteTrustedDestinationRequest
    ) = dataSource.deleteTrustedDestination(otp, body)
}