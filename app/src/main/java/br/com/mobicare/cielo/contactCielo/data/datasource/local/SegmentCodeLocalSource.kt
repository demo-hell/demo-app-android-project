package br.com.mobicare.cielo.contactCielo.data.datasource.local

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences

class SegmentCodeLocalSource(private val userPreferences: UserPreferences) {

    fun getLocalSegmentCode(): CieloDataResult<String> {
        return try {
            userPreferences.getSegmentCode()?.let { CieloDataResult.Success(it) }
                ?: CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))
        } catch (ex: Exception) {
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))
        }
    }

    fun setLocalSegmentCode(segmentCode: String) {
        userPreferences.putSegmentCode(segmentCode)
    }

    fun removeLocalSegmentCode() {
        userPreferences.removeSegmentCode()
    }
}