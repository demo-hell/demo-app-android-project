//package br.com.mobicare.cielo.massiva.presentation.ui
//
//import android.support.test.runner.AndroidJUnit4
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.massiva.presentation.robot.LoginMassivaResult
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//
///**
// * Created by Benhur on 23/08/17.
// */
//@RunWith(AndroidJUnit4::class)
//class LoginMassivaFragmentTest {
//
//    lateinit var robot: LoginMassivaRobot
//    var result: LoginMassivaResult? = null
//
//    @Before
//    @Throws(Exception::class)
//    fun setUp() {
//        robot = LoginMassivaRobot()
//        robot.setup()
//    }
//
//    /**
//     * Verifica se o loading nao aparece assim que a tela é carregada
//     */
//    @Test
//    @Throws(Exception::class)
//    fun testProgressIsHide() {
//        result = LoginMassivaResult()
//        result?.loadingIsHide()?.helpEcDialogIsHide()
//    }
//
//    /**
//     * Verifica se o alerta do help é exibido
//     */
//    @Test
//    @Throws(Exception::class)
//    fun testAlertaHelp(){
//        result = robot.clickHelpEc().start()
//        result?.helpEcDialogIsVisible()
//    }
//
//
//    /**
//     * Verifica se o alerta fecha ao clicar no botão ok
//     */
//    @Test
//    @Throws(Exception::class)
//    fun testclickOkAlertaHelp(){
//        result = robot.clickHelpEc().clickDialogButton(R.string.ok).start()
//        result?.helpEcDialogIsHide()
//    }
//
//    /**
//     * Verifica se muda para Preciso de Ajuda ao clicar no botão recuperar
//     */
//    @Test
//    @Throws(Exception::class)
//    fun testclickRecuperarAlertaHelp(){
//        result = robot.clickHelpEc().clickDialogButton(R.string.login_recuperar_duvida_ec).start()
//        result?.changeToRecoveryActivityAlert()
//    }
//
//    /**
//     * Verifica se exibe a mensagem de erro quando o campo esta vazio
//     */
//    @Test
//    @Throws(Exception::class)
//    fun testFieldEmpty(){
//        result = robot.clickEntrar().start()
//        result?.dialogErroIsShow(R.string.login_ec_error)
//    }
//
//    /**
//     * Verifica se o dialog de erro some após clicar em ok
//     */
//    @Test
//    @Throws(Exception::class)
//    fun testFieldEmptyClickOk(){
//        result = robot.clickEntrar().clickDialogButton(R.string.ok).start()
//        result?.dialogErroIsHide(R.string.login_ec_error)
//    }
//
//    /**
//     * Verifica se vai para a tela Preciso de Ajuda
//     */
//    @Test
//    @Throws(Exception::class)
//    fun testclickPrecisoDeAjuda(){
//        result = robot.clickPrecisoDeAjuda().start()
//        result?.changeToRecoveryActivity()
//    }
//
////    /**
////     * Verifica se abre a webview do Seja Cliente
////     */
////    @Test
////    @Throws(Exception::class)
////    fun testclickSejaCliente(){
////        result = robot.clickSejaCliente().start()
////        result?.showWebview(ConfigurationPreference.getInstance().getConfigurationValue(robot.activity,  ConfigurationDef.URL_LOGIN_QUERO_SER_CLIENTE))
////    }
//
////    /**
////     * Verifica se abre a webview do Seja Cliente
////     */
////    @Test
////    @Throws(Exception::class)
////    fun testclickCriarUsusario(){
////        result = robot.clickCriarUsuario().start()
////        result?.showWebview(ConfigurationPreference.getInstance().getConfigurationValue(robot.activity,  ConfigurationDef.URL_LOGIN_CRIAR_USUARIO))
////    }
//
//
//}