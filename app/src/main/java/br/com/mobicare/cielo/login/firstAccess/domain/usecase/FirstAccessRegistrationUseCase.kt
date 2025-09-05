package br.com.mobicare.cielo.login.firstAccess.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.login.firstAccess.data.model.request.FirstAccessRegistrationRequest
import br.com.mobicare.cielo.login.firstAccess.data.model.response.FirstAccessResponse
import br.com.mobicare.cielo.login.firstAccess.domain.repository.FirstAccessRepository
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixCreateNotifyInfringementRequest
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixCreateNotifyInfringementResponse

class FirstAccessRegistrationUseCase (private val repository: FirstAccessRepository) {

    suspend operator fun invoke (
        accountRegistrationPayLoadRequest: FirstAccessRegistrationRequest,
        inviteToken: String? = null,
        akamaiSensorData: String? = null
    ): CieloDataResult<FirstAccessResponse> {
        return repository.registrationAccount(
            accountRegistrationPayLoadRequest,
            inviteToken,
            akamaiSensorData
        )
    }
}