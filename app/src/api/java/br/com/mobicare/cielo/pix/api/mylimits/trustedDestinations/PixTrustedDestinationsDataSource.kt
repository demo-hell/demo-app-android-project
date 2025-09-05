package br.com.mobicare.cielo.pix.api.myLimits.trustedDestinations

import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.pix.api.PixAPI
import br.com.mobicare.cielo.pix.domain.PixAddNewTrustedDestinationRequest
import br.com.mobicare.cielo.pix.domain.PixDeleteTrustedDestinationRequest

class PixTrustedDestinationsDataSource(private val api: PixAPI) {

    private val authorization = Utils.authorization()

    fun getTrustedDestinations(servicesGroup: String?) =
        api.getTrustedDestinations(authorization, servicesGroup)

    fun addNewTrustedDestination(
        otp: String?,
        body: PixAddNewTrustedDestinationRequest
    ) = api.addNewTrustedDestination(authorization, otp, body)

    fun deleteTrustedDestination(
        otp: String?,
        body: PixDeleteTrustedDestinationRequest
    ) = api.deleteTrustedDestination(authorization, otp, body)

}