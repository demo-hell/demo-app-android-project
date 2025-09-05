package br.com.mobicare.cielo.commons.utils.token.data.dataSource

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.enums.MfaStatusEnums
import br.com.mobicare.cielo.mfa.token.CieloMfaTokenGenerator

class TokenDataSource(
    private val cieloMfaTokenGenerator: CieloMfaTokenGenerator,
    mfaUser: MfaUserInformation,
    val userPreferences: UserPreferences
) {

    private var lastToken: String? = null
    private val seed = mfaUser.getMfaUser(userPreferences.userName)?.mfaSeed

    fun getToken(): CieloDataResult<String> {
        var result: CieloDataResult<String> = CieloDataResult.Empty()

        getOtpCode()?.let {
            if (it != lastToken)
                result = CieloDataResult.Success(it)
            lastToken = it
        } ?: run {
            result = CieloDataResult.APIError(
                CieloAPIException(
                    actionErrorType = ActionErrorTypeEnum.MFA_TOKEN_ERROR_ACTION,
                    newErrorMessage = NewErrorMessage(mfaErrorCode = MfaStatusEnums.OTP_REQUIRED.mfaStatus)
                )
            )
            lastToken = null
        }

        return result
    }

    private fun getOtpCode() = cieloMfaTokenGenerator.getOtpCode(seed)
}