package br.com.mobicare.cielo.commons.data.dataSource.local

import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.mapper.MapperLoginObj
import br.com.mobicare.cielo.commons.data.mapper.MapperUserObj
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.me.MeResponse

class UserInformationLocalDataSource(
    private val menuPreference: MenuPreference,
    private val userPreferences: UserPreferences,
) {
    fun getUserObj(): CieloDataResult<UserObj> {
        return menuPreference.getUserObj()?.let {
            CieloDataResult.Success(it)
        } ?: CieloDataResult.APIError(
            CieloAPIException(
                actionErrorType = ActionErrorTypeEnum.LOGOUT_NEEDED_ERROR,
            ),
        )
    }

    fun getUserInformation(): CieloDataResult<MeResponse> {
        return try {
            userPreferences.userInformation?.let { userInformation ->
                CieloDataResult.Success(userInformation)
            } ?: CieloDataResult.Empty()
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            CieloDataResult.Empty()
        }
    }

    fun saveMeInformation(meResponse: MeResponse): CieloDataResult<MeResponse> {
        return try {
            userPreferences.saveUserInformation(meResponse)
            userPreferences.saveCurrentUserName(meResponse.username)

            val userLogged = MapperUserObj.mapToUser(meResponse)

            userPreferences.saveUserLogged(
                userLogged,
                userPreferences.token,
                meResponse.activeMerchant.id,
            )

            val loginObj = MapperLoginObj.mapToLoginObj(meResponse)

            loginObj.token = userPreferences.token
            loginObj.isConvivenciaUser = userPreferences.isConvivenciaUser
            menuPreference.saveLoginObj(loginObj)
            userPreferences.saveUserActionPermissions(HashSet<String>(meResponse.roles))

            CieloDataResult.Success(meResponse)
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            CieloDataResult.Success(meResponse)
        }
    }

    fun getUserViewHistory(key: String): CieloDataResult<Boolean> {
        return try {
            val value = userPreferences.get(key = key, defaultValue = false, isProtected = true)
            return CieloDataResult.Success(value = value)
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            deleteUserViewHistory(key)
        }
    }

    fun saveUserViewHistory(
        key: String,
        value: Boolean,
    ): CieloDataResult<Boolean> {
        return try {
            userPreferences.put(key = key, value = value, isProtected = true)
            CieloDataResult.Success(value = true)
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            deleteUserViewHistory(key)
        }
    }

    fun deleteUserViewHistory(key: String): CieloDataResult<Boolean> {
        try {
            userPreferences.delete(key = key)
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
        }
        return CieloDataResult.Empty()
    }

    fun getUserViewCounter(key: String): CieloDataResult<Int> {
        return try {
            val value = userPreferences.get(key = key, defaultValue = ZERO, isProtected = true)
            return CieloDataResult.Success(value = value)
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            deleteUserViewCounter(key)
        }
    }

    fun saveUserViewCounter(key: String): CieloDataResult<Int> {
        return try {
            userPreferences.run {
                val value = get(key = key, defaultValue = ZERO, isProtected = true) + ONE
                put(key = key, value = value, isProtected = true)
                CieloDataResult.Success(value = value)
            }
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
            deleteUserViewCounter(key)
        }
    }

    fun deleteUserViewCounter(key: String): CieloDataResult<Int> {
        try {
            userPreferences.delete(key = key)
        } catch (ex: Exception) {
            ex.message.logFirebaseCrashlytics()
        }
        return CieloDataResult.Empty()
    }
}
