package br.com.mobicare.cielo.main

import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.login.domain.SendDeviceTokenResponse
import br.com.mobicare.cielo.login.domain.TokenFCM
import br.com.mobicare.cielo.login.domains.entities.ActiveMerchantObj
import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
import br.com.mobicare.cielo.login.domains.entities.LoginObj
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.me.MeResponse
import io.reactivex.Observable

class UserInformationRepository(
    private val userInformationRemoteDataSource: UserInformationRemoteDataSource
) {

    fun getUserInformation(
        accessToken: String,
        cacheAllowed: Boolean = false
    ): Observable<MeResponse> {

        if (cacheAllowed) {
            UserPreferences.getInstance().userInformation?.let { userInfo ->
                return Observable.just(userInfo)
            }
        }

        return userInformationRemoteDataSource.getUserInformation(accessToken)
            .doOnNext { userInformationResponse ->
                saveUserInformation(userInformationResponse)
                Analytics.Update.updateUserId()
                Analytics.Update.updateUserProperties()
            }
    }

    private fun saveUserInformation(userInformationResponse: MeResponse) {
        UserPreferences.getInstance().saveUserInformation(userInformationResponse)
        UserPreferences.getInstance().saveCurrentUserName(userInformationResponse.username)

        val userLogged: UserObj = UserObj().apply {
            this.activeMerchant = ActiveMerchantObj(
                userInformationResponse.activeMerchant.id,
                userInformationResponse.activeMerchant.hierarchyLevel
            )
            this.cpf = userInformationResponse.identity?.cpf
            this.birthdayDate = userInformationResponse.birthDate.dateFormatToBr()
            this.ec = userInformationResponse.activeMerchant.id
            this.email = userInformationResponse.email
            this.nameLogin = userInformationResponse.login
            this.rg = userInformationResponse.identity?.rg
            this.mainRole = userInformationResponse.mainRole
            this.onboardingRequired = userInformationResponse.onboardingRequired ?: false
            this.digitalId = userInformationResponse.digitalId
            this.roles = userInformationResponse.roles
            this.profile = userInformationResponse.profile

        }

        UserPreferences.getInstance().saveUserLogged(
            userLogged,
            UserPreferences.getInstance().token,
            userInformationResponse.activeMerchant.id
        )

        val loginObj = LoginObj()
        loginObj.establishment = EstabelecimentoObj(
            userInformationResponse.activeMerchant.id,
            userInformationResponse.activeMerchant.tradingName,
            userInformationResponse.activeMerchant.cnpj?.number,
            userInformationResponse.activeMerchant.hierarchyLevel
        )

        loginObj.user = UserObj().apply {
            this.activeMerchant = ActiveMerchantObj(
                userInformationResponse.activeMerchant.id,
                userInformationResponse.activeMerchant.hierarchyLevel
            )
            this.cpf = userInformationResponse.identity?.cpf
            this.birthdayDate = userInformationResponse.birthDate.dateFormatToBr()
            this.ec = userInformationResponse.activeMerchant.id
            this.email = userInformationResponse.email
            this.nameLogin = userInformationResponse.login
            this.rg = userInformationResponse.identity?.rg
            this.onboardingRequired = userInformationResponse.onboardingRequired ?: false
            this.mainRole = userInformationResponse.mainRole
            this.roles = userInformationResponse.roles
            this.profile = userInformationResponse.profile
        }

        loginObj.token = UserPreferences.getInstance().token
        loginObj.isConvivenciaUser = UserPreferences.getInstance().isConvivenciaUser
        MenuPreference.instance.saveLoginObj(loginObj)

        UserPreferences.getInstance()
            .saveUserActionPermissions(HashSet<String>(userInformationResponse.roles))
    }

    fun sendTokenFCM(tokenFCM: TokenFCM): Observable<SendDeviceTokenResponse> {
        return userInformationRemoteDataSource.sendTokenFCM(tokenFCM)
    }

}
