package br.com.mobicare.cielo.commons.data.clients.api

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences.Companion.MFA_SERVER_TIME
import br.com.mobicare.cielo.commons.utils.convertToCompleteDateTime
import br.com.mobicare.cielo.mfa.utils.DATE
import br.com.mobicare.cielo.mfa.utils.ELIGIBILITY
import br.com.mobicare.cielo.mfa.utils.MFA
import okhttp3.Request

abstract class ServicesBase() {
    protected val userPreferences = UserPreferences.getInstance()

    /**
     * Retrieves the server date and time from the response header of the MFA eligibility API call.
     * It then calculates the difference between the current device date and the server date.
     * This difference is stored in encryptedSharedPreferences for recovery during OTP generation.
     * This method is useful in scenarios where there might be a discrepancy between the device time and the server time.
     *
     * @param request The request object containing the URL segments.
     * @param response The response object from which the server date and time is extracted.
     */
    protected fun getMfaServerDateTime(request: Request, response: okhttp3.Response) {
        val urlSegments = request.url().pathSegments()
        if (urlSegments.contains(MFA) && urlSegments.contains(ELIGIBILITY)) {
            val serverDateTime = response.header(DATE)?.convertToCompleteDateTime()

            serverDateTime?.let { itServerDateTime ->
                userPreferences.put(
                    MFA_SERVER_TIME,
                    itServerDateTime.time,
                    isProtected = true
                )
            }
        }
    }
}