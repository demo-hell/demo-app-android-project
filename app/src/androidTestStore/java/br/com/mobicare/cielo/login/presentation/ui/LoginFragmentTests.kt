//package br.com.mobicare.cielo.login.presentation.ui
//
//import android.support.test.runner.AndroidJUnit4
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.login.presentation.robot.LoginResult
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//
///**
// * Created by benhur.souza on 31/08/2017.
// */
//
//@RunWith(AndroidJUnit4::class)
//class LoginFragmentTests {
//
//    /*lateinit var robot: LoginRobot
//    var result: LoginResult? = null
//
//    @Before
//    @Throws(Exception::class)
//    fun setUp() {
//        robot = LoginRobot()
//        robot.setup()
//    }
//
//    *//**
//     * Verifica se o loading nao aparece assim que a tela é carregada
//     *//*
////    @Test
////    @Throws(Exception::class)
////    fun testProgressIsHide() {
////        result = LoginResult()
////        result?.loadingIsHide()?.helpEcDialogIsHide()
////    }
//
//    *//**
//     * Verifica se o alerta do help é exibido
//     *//*
//    @Test
//    @Throws(Exception::class)
//    fun testAlertaHelp(){
//        result = robot.clickHelpEc().start()
//        result?.helpEcDialogIsVisible()
//    }
//
//    *//**
//     * Verifica se o alerta fecha ao clicar no botão ok
//     *//*
//    @Test
//    @Throws(Exception::class)
//    fun testclickOkAlertaHelp(){
//        result = robot.clickHelpEc().clickDialogButton(R.string.ok).start()
//        result?.helpEcDialogIsHide()
//    }
//
//    *//**
//     * Verifica se muda para Preciso de Ajuda ao clicar no botão recuperar
//     *//*
//    @Test
//    @Throws(Exception::class)
//    fun testclickRecuperarAlertaHelp(){
//        result = robot.clickHelpEc().clickDialogButton(R.string.login_recuperar_duvida_ec).start()
//        result?.changeToRecoveryActivityAlert()
//    }
//
//    *//**
//     * Verifica se exibe a mensagem de erro quando o campo esta vazio
//     *//*
////    @Test
////    @Throws(Exception::class)
////    fun testFieldEmpty(){
////        result = robot.fillField(R.id.edit_text_login_ec).clickEntrar().start()
////        result?.dialogErroIsShow(R.string.login_ec_error)
////    }
//
//    *//**
//     * Verifica se o dialog de erro some após clicar em ok
//     *//*
////    @Test
////    @Throws(Exception::class)
////    fun testFieldEmptyClickOk(){
////        result = robot.clickEntrar().clickDialogButton(R.string.ok).start()
////        result?.dialogErroIsHide(R.string.login_ec_error)
////    }
//
//    *//**
//     * Verifica se exibe a mensagem de erro quando o campo usuario esta vazio
//     *//*
////    @Test
////    @Throws(Exception::class)
////    fun testFieldUserEmpty(){
////        result = robot.fillField(R.id.edit_text_login_ec, "1234567890").clickEntrar().start()
////        result?.dialogErroIsShow(R.string.error_login_user)
////    }
//
//    *//**
//     * Verifica se exibe a mensagem de erro quando o campo usuario esta vazio e fecha apos ok
//     *//*
////    @Test
////    @Throws(Exception::class)
////    fun testFieldUserEmptyClickOk(){
////        result = robot
////                .fillField(R.id.edit_text_login_ec, "1234567890")
////                .clickEntrar()
////                .clickDialogButton(R.string.ok)
////                .start()
////        result?.dialogErroIsHide(R.string.error_login_user)
////    }
//
//
//    *//**
//     * Verifica se exibe a mensagem de erro quando o campo senha esta vazio
//     *//*
////    @Test
////    @Throws(Exception::class)
////    fun testFieldPasswordEmpty(){
////        result = robot
////                .fillField(R.id.edit_text_login_ec, "1234567890")
////                .fillField(R.id.edit_text_login_user_name, "testesitecielo")
////                .clickEntrar()
////                .start()
////        result?.dialogErroIsShow(R.string.error_login_password)
////    }
//
//    *//**
//     * Verifica se exibe a mensagem de erro quando o campo senha esta vazio e fecha apos ok
//     *//*
////    @Test
////    @Throws(Exception::class)
////    fun testFieldPasswordEmptyClickOk(){
////        result = robot
////                .fillField(R.id.edit_text_login_ec, "1234567890")
////                .fillField(R.id.edit_text_login_user_name, "testesitecielo")
////                .clickEntrar()
////                .clickDialogButton(R.string.ok)
////                .start()
////        result?.dialogErroIsHide(R.string.error_login_password)
////    }
//
//    *//**
//     * Verifica se o alerta do user help é exibido
//     *//*
//    @Test
//    @Throws(Exception::class)
//    fun testAlertaUserHelp(){
//        result = robot.clickHelpUser().start()
//        result?.helpUserDialogIsVisible()
//    }
//
//    *//**
//     * Verifica se ao clicar no ver senha a mesma é exibida
//     *//*
//    @Test
//    @Throws(Exception::class)
//    fun testShowPassword(){
//        result = robot
//                .fillField(R.id.edit_text_login_password, "123456")
//                .clickShowPassword()
//                .start()
//
//        result?.compareFiled(R.id.edit_text_login_password, "123456")
//    }
//
//    *//**
//     * Verifica se vai para a tela Preciso de Ajuda
//     *//*
////    @Test
////    @Throws(Exception::class)
////    fun testclickPrecisoDeAjuda(){
////        result = robot.clickPrecisoDeAjuda().start()
////        result?.changeToRecoveryActivity()
////    }*/
//
//}