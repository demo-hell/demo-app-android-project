//package br.com.mobicare.cielo.massiva.presentation.robot
//
//import android.support.test.espresso.Espresso.onView
//import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
//import android.support.test.espresso.assertion.ViewAssertions.matches
//import android.support.test.espresso.matcher.ViewMatchers.*
//import android.support.test.espresso.web.assertion.WebViewAssertions.webMatches
//import android.support.test.espresso.web.model.Atoms.getCurrentUrl
//import android.support.test.espresso.web.sugar.Web.onWebView
//import br.com.mobicare.cielo.R
//import org.hamcrest.Matchers.containsString
//import org.hamcrest.Matchers.not
//
//
///**
// * Created by Benhur on 13/07/17.
// */
//
//class LoginMassivaResult {
//
//    /**
//     * Verifica se o loading esta sendo exibido
//     * @return
//     */
//    fun loadingIsVisible(): LoginMassivaResult {
//        onView(withId(R.id.progress_login_massiva_loading))
//                .check(matches(isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se o loading não esta sendo exibido
//     * @return
//     */
//    fun loadingIsHide(): LoginMassivaResult {
//        onView(withId(R.id.progress_login_massiva_loading))
//                .check(matches(not(isDisplayed())))
//        return this
//    }
//
//    /**
//     * Verifica se o botao entrar esta desabilitado
//     * @return
//     */
//    fun buttonIsDisable(): LoginMassivaResult {
//        onView(withId(R.id.button_login_entrar))
//                .check(matches(not(isEnabled())))
//        return this
//    }
//
//    /**
//     * Verifica se o dialog HelpEC esta sendo exibido
//     * @return
//     */
//    fun helpEcDialogIsVisible(): LoginMassivaResult {
//        onView(withId(R.id.custom_dialog_message))
//                .check(matches(isDisplayed()))
//        return this
//    }
//
//
//    /**
//     * Verifica se o dialog HelpEC nao esta sendo exibido
//     * @return
//     */
//    fun helpEcDialogIsHide(): LoginMassivaResult {
//        onView(withId(R.id.custom_dialog_message))
//                .check(doesNotExist())
//        return this
//    }
//
//    /**
//     * Verifica se o dialog de erro esta sendo exibido
//     * @return
//     */
//    fun dialogErroIsShow(msgId: Int): LoginMassivaResult {
//        onView(withId(R.id.custom_dialog_message))
//                .check(matches(withText(msgId)))
//        return this
//    }
//
//    /**
//     * Verifica se o dialog de erro nao esta sendo exibido
//     * @return
//     */
//    fun dialogErroIsHide(msgId: Int): LoginMassivaResult {
//        onView(withText(msgId))
//                .check(doesNotExist())
//        return this
//    }
//
//    /**
//     * Verifica se foi para a tela Preciso de Ajuda
//     * @return
//     */
//    fun changeToRecoveryActivity(): LoginMassivaResult {
//        onView(withText(R.string.ajuda_title)).check(matches(isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se foi para a tela Preciso de Ajuda com o alerta sendo exibido
//     * @return
//     */
//    fun changeToRecoveryActivityAlert(): LoginMassivaResult {
//        onView(withText(R.string.ajuda_alert_title)).check(matches(isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se o dialog de erro nao esta sendo exibido
//     * @return
//     */
//    fun showWebview(url: String): LoginMassivaResult {
////        onWebView().forceJavascriptEnabled()
//        onWebView().check(webMatches(getCurrentUrl(), containsString(url)))
//        return this
//    }
//
//
//
//}
