package br.com.mobicare.cielo.mfa.token.mapper

import br.com.mobicare.cielo.mfa.EnrollmentResponse
import br.com.mobicare.cielo.mfa.MfaEligibilityResponse

object CieloMfaTokenMapper {
    fun MfaEligibilityResponse.toEnrollmentResponse(): EnrollmentResponse {
        return EnrollmentResponse(
            status = status,
            type = type,
            typeCode = typeCode,
            statusCode = statusCode,
            statusTrace = statusTrace
        )
    }
}