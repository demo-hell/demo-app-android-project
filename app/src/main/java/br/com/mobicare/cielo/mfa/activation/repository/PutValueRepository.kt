package br.com.mobicare.cielo.mfa.activation.repository

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.mfa.api.EnrollmentBankResponse
import io.reactivex.Observable

class PutValueRepository(private val api: CieloAPIServices) {

    fun activationCode(activationCode: String) = api.activationCode(activationCode)

    fun fetchActiveBank(): Observable<EnrollmentBankResponse> {
        return api.fetchEnrollmentActiveBank()
    }
}