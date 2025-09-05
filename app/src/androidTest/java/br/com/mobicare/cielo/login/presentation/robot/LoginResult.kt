//package br.com.mobicare.cielo.login.presentation.robot
//
//import android.support.annotation.IntegerRes
//import android.support.annotation.StringRes
//import android.support.test.espresso.Espresso
//import android.support.test.espresso.assertion.ViewAssertions
//import android.support.test.espresso.matcher.ViewMatchers
//import android.support.test.espresso.web.assertion.WebViewAssertions
//import android.support.test.espresso.web.model.Atoms
//import android.support.test.espresso.web.sugar.Web
//import br.com.mobicare.cielo.R
//import org.hamcrest.Matchers
//
///**
// * Created by benhur.souza on 31/08/2017.
// */
//
//class LoginResult {
//
//    /**
//     * Verifica se o loading esta sendo exibido
//     * @return
//     */
//    fun loadingIsVisible(): LoginResult {
//        Espresso.onView(ViewMatchers.withId(R.id.progress_login_massiva_loading))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se o loading não esta sendo exibido
//     * @return
//     */
//    fun loadingIsHide(): LoginResult {
//        Espresso.onView(ViewMatchers.withId(R.id.progress_login_massiva_loading))
//                .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
//        return this
//    }
//
//    /**
//     * Verifica se o botao entrar esta desabilitado
//     * @return
//     */
//    fun buttonIsDisable(): LoginResult {
//        Espresso.onView(ViewMatchers.withId(R.id.button_login_entrar))
//                .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))
//        return this
//    }
//
//    /**
//     * Verifica se o dialog HelpEC esta sendo exibido
//     * @return
//     */
//    fun helpEcDialogIsVisible(): LoginResult {
//        helpDialogIsVisible(R.string.login_message_duvida_ec)
//        return this
//    }
//
//    /**
//     * Verifica se o dialog HelpUser esta sendo exibido
//     * @return
//     */
//    fun helpUserDialogIsVisible(): LoginResult {
//        helpDialogIsVisible(R.string.login_message_duvida_user)
//        return this
//    }
//
//    fun helpDialogIsVisible(@StringRes msgId: Int): LoginResult{
//        Espresso.onView(ViewMatchers.withId(R.id.custom_dialog_message))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//                .check(ViewAssertions.matches(ViewMatchers.withText(msgId)))
//        return this
//    }
//
//    /**
//     * Verifica se o campo está preenchido com o valor
//     */
//    fun compareFiled(@IntegerRes fieldId: Int, msg: String): LoginResult{
//        Espresso.onView(ViewMatchers.withText(msg))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
////                .check(ViewAssertions.matches(ViewMatchers.withText(msg)))
//        return this
//    }
//
//    /**
//     * Verifica se o dialog HelpEC nao esta sendo exibido
//     * @return
//     */
//    fun helpEcDialogIsHide(): LoginResult {
//        Espresso.onView(ViewMatchers.withId(R.id.custom_dialog_message))
//                .check(ViewAssertions.doesNotExist())
//        return this
//    }
//
//    /**
//     * Verifica se o dialog de erro esta sendo exibido
//     * @return
//     */
//    fun dialogErroIsShow(msgId: Int): LoginResult {
//        Espresso.onView(ViewMatchers.withId(R.id.custom_dialog_message))
//                .check(ViewAssertions.matches(ViewMatchers.withText(msgId)))
//
//        return this
//    }
//
//    /**
//     * Verifica se o dialog de erro nao esta sendo exibido
//     * @return
//     */
//    fun dialogErroIsHide(msgId: Int): LoginResult {
//        Espresso.onView(ViewMatchers.withText(msgId))
//                .check(ViewAssertions.doesNotExist())
//        return this
//    }
//
//    /**
//     * Verifica se foi para a tela Preciso de Ajuda
//     * @return
//     */
//    fun changeToRecoveryActivity(): LoginResult {
////        Espresso.onView(ViewMatchers.withText(R.string.ajuda_title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        Espresso.onView(ViewMatchers.withId(R.id.text_view_precisa_de_ajuda)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se foi para a tela Preciso de Ajuda com o alerta sendo exibido
//     * @return
//     */
//    fun changeToRecoveryActivityAlert(): LoginResult {
//        Espresso.onView(ViewMatchers.withText(R.string.ajuda_alert_title)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se o dialog de erro nao esta sendo exibido
//     * @return
//     */
//    fun showWebview(url: String): LoginResult {
////        onWebView().forceJavascriptEnabled()
//        Web.onWebView().check(WebViewAssertions.webMatches(Atoms.getCurrentUrl(), Matchers.containsString(url)))
//        return this
//    }
//
//
//
//}
