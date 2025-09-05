package br.com.mobicare.cielo.commons.data.dataSource.local

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domain.datasource.MenuLocalDataSource
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.main.domain.AppMenuResponse

class MenuLocalDataSourceImpl(
    private val userPreferences: UserPreferences
) : MenuLocalDataSource {

    private val error = CieloDataResult.APIError(
        CieloAPIException(actionErrorType = ActionErrorTypeEnum.LOGOUT_NEEDED_ERROR)
    )

    override suspend fun getMenu(): CieloDataResult<AppMenuResponse> {
        return userPreferences.appMenu?.let {
            CieloDataResult.Success(it)
        } ?: error
    }

    override suspend fun saveMenuLocalStorage(menu: AppMenuResponse): CieloDataResult<Boolean> {
        return try {
            userPreferences.saveMenuApp(menu)
            CieloDataResult.Success(true)
        } catch (e: Exception) {
            e.message.logFirebaseCrashlytics()
            error
        }
    }

    override suspend fun savePosVirtualWhiteList(isEligible: Boolean): CieloDataResult<Boolean> {
        return try {
            userPreferences.savePosVirtualWhiteList(isEligible)
            CieloDataResult.Success(true)
        } catch (e: Exception) {
            e.message.logFirebaseCrashlytics()
            error
        }
    }

}