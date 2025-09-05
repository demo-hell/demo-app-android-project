package br.com.mobicare.cielo.login.analytics

import android.content.Context
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Analytics.GoogleAnalytics4Tracking
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Click
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Exception
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Navigation
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Share
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.UserAndImpersonate
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Values.BUTTON
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.constants.USER_INPUT_CPF_TRACK
import br.com.mobicare.cielo.commons.constants.USER_INPUT_EC_TRACK
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.AppsFlyerConstants.AF_SCREEN_NAME
import br.com.mobicare.cielo.commons.utils.AppsFlyerConstants.AF_SCREEN_VIEW
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import com.appsflyer.AppsFlyerLib

class LoginAnalytics {

    fun logFirstAccessButton() {
        Analytics.trackEvent(
            category = listOf(APP_CIELO, LOGIN),
            action = listOf(ACTION_ID_USER, CLIQUE),
            label = listOf(BOTAO, LABEL_GA_BUTTON_FIRST_ACESS)
        )
    }

    fun logKeepData(userInputType: Int) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, LOGIN),
            action = listOf(
                userName(userInputType, UserPreferences.getInstance().userName),
                CHECK_BOX
            ),
            label = listOf(REMEMBER_USER, Utils.getLoginType(userInputType))
        )
    }

    fun logErrorFirstStepNextButtonClicked(userInputType: Int, userInput: String) {
        Analytics.trackEvent(
            category = listOf(APP_CIELO, LOGIN),
            action = listOf(
                CALLBACK,
                userName(userInputType, userInput),
                Utils.getLoginType(userInputType)
            ),
            label = listOf(ERRO, ALGO_ERRADO)
        )
    }

    fun logSuccessFirstStepNextButtonClicked(userInputType: Int, userInput: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, LOGIN),
            action = listOf(
                Action.CALLBACK,
                userName(userInputType, userInput),
                Utils.getLoginType(userInputType)
            ),
            label = listOf(Label.SUCESSO)
        )
    }

    fun logFirstStepNextButtonClicked(userInputType: Int) {
        Analytics.trackEvent(
            category = listOf(APP_CIELO, LOGIN),
            action = listOf(
                userName(userInputType, UserPreferences.getInstance().userName),
                Utils.getLoginType(userInputType),
                CLIQUE
            ),
            label = listOf(BOTAO, LABEL_GA_BUTTON_CONTINUE)
        )
    }

    fun logForgotButtonClicked(userInputType: Int) {
        Analytics.trackEvent(
            category = listOf(APP_CIELO, LOGIN),
            action = listOf(
                userName(userInputType, UserPreferences.getInstance().userName),
                Utils.getLoginType(userInputType),
                CLIQUE
            ),
            label = listOf(BOTAO, LABEL_GA_BUTTON_FORGOT_PASSWORD)
        )
    }

    fun logLoginButtonClicked(userInputType: Int, userInput: String) {
        Analytics.trackEvent(
            category = listOf(APP_CIELO, LOGIN),
            action = listOf(
                userName(userInputType, userInput),
                Utils.getLoginType(userInputType),
                Action.CLIQUE
            ),
            label = listOf(BOTAO, LABEL_GA_BUTTON_ENTER)
        )
    }

    fun logLoginProcessError(userInputType: Int, errosCode: String, userInput: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, LOGIN),
            action = listOf(
                Action.CALLBACK,
                userName(userInputType, userInput),
                Utils.getLoginType(userInputType)
            ),
            label = listOf(Label.ERRO, ALGO_ERRADO, errosCode)
        )
    }

    fun logLoginProcessSucess(userInputType: Int, userInput: String) {
        Analytics.trackEvent(
            category = listOf(APP_CIELO, LOGIN),
            action = listOf(
                CALLBACK,
                userName(userInputType, userInput),
                Utils.getLoginType(userInputType)
            ),
            label = listOf(Label.SUCESSO)
        )
    }

    fun logLoginScreenViewGa(className: Class<Any>) {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = ScreenView.SCREEN_VIEW_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to GA_LOGIN_PATH,
                Navigation.FIREBASE_SCREEN to className.simpleName.toLowerCasePTBR(),
            )
        )
    }

    fun logScreenView(screenName: String) {
        GoogleAnalytics4Tracking.trackScreenView(screenName)
    }

    fun logLoginScreenViewAppsFlyer(context: Context){
        val eventParameters = HashMap<String, Any>()
        eventParameters[AF_SCREEN_NAME] = GA_LOGIN_PATH
        AppsFlyerLib.getInstance().logEvent(context, AF_SCREEN_VIEW, eventParameters)
    }

    fun logLoginButtonClickedGa(userInputType: Int) {
        val typeMethod = if (Utils.getLoginType(userInputType).equals(USER_INPUT_EC_TRACK))
            GA_NUMBER_ESTABLISHMENT
        else
            Utils.getLoginType(userInputType).toLowerCasePTBR()

        GoogleAnalytics4Tracking.trackEvent(
            eventName = UserAndImpersonate.LOGIN_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap = mapOf(
                Share.METHOD to typeMethod,
                ScreenView.SCREEN_NAME to GA_LOGIN_PATH,
            )
        )
    }

    fun logLoginHidePassword(visible: Boolean, className: Class<Any>) {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap = mapOf(
                Navigation.CONTENT_COMPONENT to GA_HIDE_PASSWORD,
                Navigation.CONTENT_TYPE to GA_ICON,
                Navigation.CONTENT_NAME to if (visible) GA_VERDADEIRO else GA_FALSO,
                ScreenView.SCREEN_NAME to GA_LOGIN_PATH,
                Navigation.FIREBASE_SCREEN to className.simpleName.toLowerCasePTBR(),
            )
        )
    }

    fun logLoginButtonCreateAccountClickedGa() {
        Analytics.GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap = mapOf(
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to CREATE_ACCOUNT,
                ScreenView.SCREEN_NAME to GA_LOGIN_PATH,
            )
        )
    }

    fun logLoginDisplayContent(description: String) {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = Navigation.DISPLAY_CONTENT_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to GA_LOGIN_PATH,
                Exception.DESCRIPTION to description,
                Navigation.CONTENT_TYPE to GA_MESSAGE
            )
        )
    }

    fun logLoginForgotButtonClickedGa() {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap = mapOf(
                Navigation.CONTENT_TYPE to GA_LINK,
                Navigation.CONTENT_NAME to GA_FORGOT_PASSWORD,
                ScreenView.SCREEN_NAME to GA_LOGIN_PATH,
            )
        )
    }

    fun logButtonClickedGa(buttonName: String) {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to GA_LOGIN_PATH,
                Navigation.CONTENT_NAME to buttonName.normalizeToLowerSnakeCase(),
                Navigation.CONTENT_TYPE to BUTTON
            )
        )
    }

    fun logLoginFirstAccessButtonGa() {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = UserAndImpersonate.SIGNUP_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to GA_LOGIN_PATH,
                UserAndImpersonate.STEP to GA_FIRST_ACCESS,
            )
        )
    }

    fun logFirstAccessButtonDoLoginGa(doLogin: Boolean) {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = UserAndImpersonate.SIGNUP_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to GA_LOGIN_PATH,
                UserAndImpersonate.STEP to if (doLogin) GA_DO_LOGIN else GA_CONTINUE_FIST_ACCESS,
            )
        )
    }

    fun logFirstAccessCommonClientScreenViewGa(className: Class<Any>){
        GoogleAnalytics4Tracking.trackEvent(
            eventName = ScreenView.SCREEN_VIEW_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to GA_FIRST_ACCESS_NOT_CLIENT_PATH,
                Navigation.FIREBASE_SCREEN to className.simpleName.toLowerCasePTBR(),
            )
        )
    }

    fun logAppUnavailableScreenViewGa(message: String) {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = Exception.EXCEPTION_EVENT,
            eventsMap = mapOf(
                ScreenView.SCREEN_NAME to GA_LOGIN_PATH,
                Exception.DESCRIPTION to message.normalizeToLowerSnakeCase(),
                Exception.STATUS_CODE to Text.EMPTY,
            )
        )
    }

    fun logClickButton(contentName: String, screenName: String) {
        GoogleAnalytics4Tracking.trackEvent(
            eventName = Click.CLICK_EVENT,
            isLoginOrImpersonateFlow = true,
            eventsMap = mapOf(
                Navigation.CONTENT_TYPE to BUTTON,
                Navigation.CONTENT_NAME to contentName,
                ScreenView.SCREEN_NAME to screenName
            )
        )
    }

    private fun userName(userInputType: Int, userInput: String): String {
        return if (Utils.getLoginType(userInputType) == USER_INPUT_CPF_TRACK) ""
        else userInput
    }

    companion object {
        const val CLIQUE = "clique"
        const val BOTAO = "botao"
        const val LABEL_GA_BUTTON_CONTINUE = "continuar"
        const val LABEL_GA_BUTTON_FIRST_ACESS = "primeiro acesso"
        const val ALGO_ERRADO = "algo deu errado"
        const val LABEL_GA_BUTTON_ENTER = "entrar"
        const val LABEL_GA_BUTTON_FORGOT_PASSWORD = "esqueceu a senha"
        const val REMEMBER_USER = "lembrar usuario"
        const val LOGIN = "login"
        const val ACTION_ID_USER = "identificacao de usuario"
        const val CHECK_BOX = "checkbox"
        const val APP_CIELO = "app cielo"
        const val ERRO = "Erro"
        const val CALLBACK = "callback"
        const val GA_LOGIN_PATH = "/login"
        const val GA_FIRST_ACCESS_NOT_CLIENT_PATH = "/login/primeiro_acesso/quero_ser_cliente"
        const val ADD_INTERNAL_USER_PATH = "/login/usuario_interno"
        const val GA_MESSAGE = "message"
        const val GA_VERDADEIRO = "verdadeiro"
        const val GA_FALSO = "falso"
        const val GA_ICON = "icon"
        const val GA_HIDE_PASSWORD = "ocultar_senha"
        const val GA_LINK = "link"
        const val GA_FORGOT_PASSWORD = "esqueci_minha_senha"
        const val GA_FIRST_ACCESS = "primeiro_acesso"
        const val GA_DO_LOGIN = "fazer_login"
        const val GA_CONTINUE_FIST_ACCESS = "continuar_com_primeiro_acesso"
        const val GA_NUMBER_ESTABLISHMENT = "n_do_estabelecimento"
        const val TOKEN = "token"
        const val CREATE_ACCOUNT = "criar_conta"
    }
}