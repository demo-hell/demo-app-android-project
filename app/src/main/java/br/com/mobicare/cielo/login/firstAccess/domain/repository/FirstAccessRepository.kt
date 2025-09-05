package br.com.mobicare.cielo.login.firstAccess.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessResponse
import br.com.mobicare.cielo.login.firstAccess.data.model.request.FirstAccessRegistrationRequest

interface FirstAccessRepository {
    suspend fun registrationAccount (
        accountRegistrationPayLoadRequest: FirstAccessRegistrationRequest,
        inviteToken: String? = null,
        akamaiSensorData: String? = null
    ): CieloDataResult<FirstAccessResponse>
}