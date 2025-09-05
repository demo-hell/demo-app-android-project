package br.com.mobicare.cielo.mfa.activation.repository

import br.com.mobicare.cielo.mfa.api.EnrollmentBankResponse
import io.reactivex.Observable

interface PutValueInteractor {

    fun activationCode(activationCode: String): Observable<PutValueResponse>
    fun saveMfaUserInformation(putValueResponse: PutValueResponse)
    fun hasActiveMfaUser(): Boolean
    fun fetchActiveBank(): Observable<EnrollmentBankResponse>

}