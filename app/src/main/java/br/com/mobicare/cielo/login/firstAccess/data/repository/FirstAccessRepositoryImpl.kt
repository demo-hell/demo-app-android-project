package br.com.mobicare.cielo.login.firstAccess.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.login.firstAccess.data.datasource.FirstAccessDataSourceImpl
import br.com.mobicare.cielo.login.firstAccess.domain.repository.FirstAccessRepository
import br.com.mobicare.cielo.login.firstAccess.data.model.request.FirstAccessRegistrationRequest
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessResponse

class FirstAccessRepositoryImpl (
    private val firstAccessDataSource: FirstAccessDataSourceImpl
) : FirstAccessRepository {
    override suspend fun registrationAccount(
        accountRegistrationPayLoadRequest: FirstAccessRegistrationRequest,
        inviteToken: String?,
        akamaiSensorData: String?
    ) : CieloDataResult<FirstAccessResponse> {
        return firstAccessDataSource.registrationAccount(
            accountRegistrationPayLoadRequest,
            inviteToken,
            akamaiSensorData)
    }
}