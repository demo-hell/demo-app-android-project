package br.com.mobicare.cielo.mfa.analytics

import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ADMIN
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ANALYST
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.MASTER
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.READER
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.TECHNICAL
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRoleEnum
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.pix.constants.EMPTY

class MfaAnalytics {

    fun logScreenView(screenName: String, screenClass: Class<Any>) {
        Analytics.trackScreenView(screenName, screenClass)
    }

    fun logMFACallbackSuccess(){
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.TOKEN),
            action = listOf(CONFIG_DEVICE, getRole(), Action.CALLBACK),
            label = listOf(Label.SUCESSO)
        )
    }

    fun logMFACallbackError(errorCode: String?, errorMessage: String?){
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.TOKEN),
            action = listOf(CONFIG_DEVICE, getRole(), Action.CALLBACK),
            label = listOf(Label.ERRO, errorMessage ?: EMPTY, errorCode ?: EMPTY)
        )
    }

    fun logMFAShowBottomSheet(titleBottomSheet: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.TOKEN),
            action = listOf(Action.MODAL, getRole(), Action.EXIBICAO),
            label = listOf(titleBottomSheet)
        )
    }

    fun logMFAClickBottomSheet(titleBottomSheet: String, labelButton: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, Category.TOKEN),
            action = listOf(titleBottomSheet, getRole(), Action.CLIQUE),
            label = listOf(Label.BOTAO, labelButton)
        )
    }

    private fun getRole(): String {
        return MenuPreference.instance.getUserObj()?.mainRole?.let {
            when (it.uppercase()) {
                ADMIN -> MainRoleEnum.ADMIN.description
                READER -> MainRoleEnum.READER.description
                MASTER.uppercase() -> MainRoleEnum.MASTER.description
                ANALYST -> MainRoleEnum.ANALYST.description
                TECHNICAL -> MainRoleEnum.TECHNICAL.description
                else -> it
            }
        } ?: EMPTY
    }

    companion object {

        const val ANALYTICS_MFA_CONFIG_DEVICE = "/token/configurando o dispositivo"

        const val CONFIG_DEVICE = "configurando o dispositivo"
        const val CONFIRM_ID = "confirmar identidade"
        const val ACCESS_OTHER_CELLPHONE = "acessando de outro celular"
        const val USER_NOT_ELEGIBLE = "usuario nao elegivel"

    }

}