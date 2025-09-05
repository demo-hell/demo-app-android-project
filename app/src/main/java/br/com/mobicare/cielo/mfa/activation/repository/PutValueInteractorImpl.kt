package br.com.mobicare.cielo.mfa.activation.repository

import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.mfa.api.EnrollmentBankResponse
import io.reactivex.Observable

class PutValueInteractorImpl(
    private val repository: PutValueRepository,
    private val mfaUserInformation: MfaUserInformation
) : PutValueInteractor {

    override fun activationCode(activationCode: String): Observable<PutValueResponse> {
        return repository.activationCode(activationCode)
    }

    override fun saveMfaUserInformation(putValueResponse: PutValueResponse) {
        mfaUserInformation.saveMfaUserInformation(putValueResponse)
    }

    override fun hasActiveMfaUser(): Boolean {
        return mfaUserInformation.hasActiveMfaUser()
    }

    override fun fetchActiveBank(): Observable<EnrollmentBankResponse> {
        return repository.fetchActiveBank()
    }
}