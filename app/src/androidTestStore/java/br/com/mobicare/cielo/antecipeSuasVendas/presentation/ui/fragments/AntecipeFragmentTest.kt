//package br.com.mobicare.cielo.antecipeSuasVendas.presentation.ui.fragments
//
//import android.support.test.runner.AndroidJUnit4
//import br.com.mobicare.cielo.antecipeSuasVendas.domains.entities.AntecipacaoAvulsaObj
//import br.com.mobicare.cielo.antecipeSuasVendas.domains.entities.AntecipacaoProgramadaObj
//import br.com.mobicare.cielo.antecipeSuasVendas.presentation.robot.AntecipeResult
//import br.com.mobicare.cielo.antecipeSuasVendas.presentation.robot.AntecipeRobot
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//
///**
// * Created by benhur.souza on 20/07/2017.
// */
//@RunWith(AndroidJUnit4::class)
//class AntecipeFragmentTest {
//
//    lateinit var robot: AntecipeRobot
//    var result: AntecipeResult? = null
//
//    @Before
//    @Throws(Exception::class)
//    fun setUp() {
//        robot = AntecipeRobot()
//        robot.setup()
//    }
//
//    /**
//     * Verifica se o loading aparece assim que a tela é carregada
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testProgressIsHide() {
//        result = AntecipeResult()
//        result?.loadingIsHide()
//    }
//
//    /**
//     * Verifica se o loading avulsa aparece assim que a tela é carregada
//     * */
////    @Test
////    @Throws(Exception::class)
////    fun testProgressAvulsaIsVisible() {
////        result = robot.showLoadingAvulsa().start()
////        result?.cardAvulsaMessageIsVisible()?.loadingAvulsaIsVisible()
////    }
//
//    /**
//     * Verifica o estado da card após ser clicado
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testClickAvulsa() {
//        result = AntecipeResult()
//        result?.loadingIsHide()
//    }
//
//    /**
//     * Verifica se o Card avulsa fica selecionado depois do onButtonClick
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testCheckAvulsaCard() {
//        result = robot.hideLoadingAvulsa().showCardAvulsa().clickCardAvulsa().start()
//        result?.cardAvulsaIsCheck()
//    }
//
//    /**
//     * Verifica se o Card avulsa fica deselecionado depois do segundo onButtonClick
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testUncheckAvulsaCard() {
//        result = robot.hideLoadingAvulsa().showCardAvulsa().clickCardAvulsa().clickCardAvulsa().start()
//        result?.cardAvulsaIsNotCheck()
//    }
//
//    /**
//     * Verifica se o Card programada fica selecionado depois do onButtonClick
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testCheckProgramadaCard() {
//        result = robot.hideLoadingProgramada().showCardProgramada().clickCardProgramada().start()
//        result?.cardProgramadaIsCheck()
//    }
//
//    /**
//     * Verifica se o Card programada fica deselecionado depois do segundo onButtonClick
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testUncheckProgramadaCard() {
//        result = robot.hideLoadingProgramada().showCardProgramada().clickCardProgramada().clickCardProgramada().start()
//        result?.cardProgramdaIsNotCheck()
//    }
//
//    /**
//     * Verifica se o contrato nâo esta marcado inicialmente
//     * */
////    @Test
////    @Throws(Exception::class)
////    fun testUncheckContrato() {
////        result = AntecipeResult()
////        result?.contratoIsNotCheck()
////    }
//
//    /**
//     * Verifica se o contrato é marcado apos o onButtonClick
//     * */
////    @Test
////    @Throws(Exception::class)
////    fun testClickContrato() {
////        result = robot.showContrato().clickContrato().start()
////        result?.contratoIsCheck()
////    }
//
//    /**
//     * Verifica se o contrato é desmarcado apos o onButtonClick
//     * */
////    @Test
////    @Throws(Exception::class)
////    fun testClickUncheckContrato() {
////        result = robot.showContrato().clickContrato().clickContrato().start()
////        result?.contratoIsNotCheck()
////    }
//
//    /**
//     * Verifica se o card de aviso é exibido
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testCardAvisoShow() {
//        result = robot.showCardHorario("").start()
//        result?.avisoIsVisible()
//    }
//
//    /**
//     * Verifica se o card de aviso fica hideUserBonus
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testCardAvisoHide() {
//        result = robot.hideCardHorario().start()
//        result?.avisoIsInvisible()
//    }
//
//    /**
//     * Verifica se o card de aviso é exibido
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testCardAvulsaShow() {
//        result = robot.showCardAvulsa().start()
//        result?.cardAvulsaIsVisible()
//    }
//
//    /**
//     * Verifica se o card de avulsa fica hideUserBonus
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testCardAvulsaHide() {
//        result = robot.hideCardAvulsa().start()
//        result?.cardAvulsaIsInvisible()
//    }
//
//    /**
//     * Verifica se o card programada é exibido
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testCardProgramadaShow() {
//        result = robot.showCardProgramada().start()
//        result?.cardProgramadaIsVisible()
//    }
//
//    /**
//     * Verifica se o card programada fica hideUserBonus
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testCardProgramadaHide() {
//        result = robot.hideCardProgramada().start()
//        result?.cardProgramadaIsInvisible()
//    }
//
//    /**
//     * Verifica se o card programada message é exibido
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testCardProgramadaMessageShow() {
//        var message = "Message"
//        result = robot
//                .hideLoadingProgramada()
//                .showCardProgramadaMessage(message)
//                .start()
//        result?.cardProgramadaMessageIsVisible()?.checkCardProgramadaMessage(message)
//    }
//
//    /**
//     * Verifica se o card programada message fica hideUserBonus
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testCardProgramadaMessageHide() {
//        result = robot.hideCardProgramadaMessage().start()
//        result?.cardProgramadaMessageIsInvisible()
//    }
//
//    /**
//     * Verifica se o card de aviso message é exibido
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testCardAvulsaMessageShow() {
//        var message = "Message"
//
//        result = robot
//                .hideLoadingAvulsa()
//                .showCardAvulsaMessage(message).start()
//
//        result?.cardAvulsaMessageIsVisible()?.checkCardAvulsaMessage(message)
//    }
//
//    /**
//     * Verifica se o card de avulsa fica hideUserBonus
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testCardAvulsaMessageHide() {
//        result = robot.hideCardAvulsa().start()
//        result?.cardAvulsaMessageIsInvisible()
//    }
//
//    /**
//     * Verifica se o card de avulsa esta sendo preenchido corretamente
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testCardAvulsaTaxas() {
//        var obj = AntecipacaoAvulsaObj()
//        obj.amountToReceive = 5605.70
//        obj.anticipationAmount = 5800.0
//        obj.operationTax = 7.00
//
//        result = robot
//                .showCardAvulsa()
//                .loadTaxasCardAvulsa(obj)
//                .start()
//        result?.cardAvulsaIsVisible()?.checkCardAvulsaTaxas(obj)
//    }
//
//    /**
//     * Verifica se o card de avulsa esta sendo preenchido corretamente
//     * */
//    @Test
//    @Throws(Exception::class)
//    fun testCardAvulsaProgramadas() {
//        var obj = AntecipacaoProgramadaObj()
//        obj.promotionalTax = 2.87
//        obj.defaultTax = 4.90
//
//        result = robot
//                .showCardProgramada()
//                .loadTaxasCardProgramada(obj)
//                .start()
//        result?.cardProgramadaIsVisible()?.checkCardProgramadaTaxas(obj)
//    }
//
//    /**
//     * Verifica se o erro do avulsa esta sendo exibido corretamente
//     */
//    @Test
//    @Throws(Exception::class)
//    fun testErroAvulsaMessage(){
//        var error = "Error"
//        result = robot.showErroAvulsa(error).start()
//        result?.cardAvulsaErroIsVisible()?.checkCardAvulsaMessage(error)
//    }
//
//    /**
//     * Verifica se o erro do programada esta sendo exibido corretamente
//     */
//    @Test
//    @Throws(Exception::class)
//    fun testErroProgramadaMessage(){
//        var error = "Error"
//        result = robot.showErroProgramada(error).start()
//        result?.cardProgramadaErroIsVisible()?.checkProgramadaErroMessage(error)
//    }
//
//    /**
//     * Verifica se o texto do contrato esta sendo exibido corretamente,
//     * quando somente é elegível a Avulsa
//     */
//    @Test
//    @Throws(Exception::class)
//    fun testMessageContratoAvulsa(){
//        result = robot.showCardAvulsa().hideCardProgramada().start()
//        result?.checkContratoMessageNotClickHere()
//    }
//
////    /**
////     * Verifica se o texto do contrato esta sendo exibido corretamente,
////     * quando somente é elegível a Programada
////     */
////    @Test
////    @Throws(Exception::class)
////    fun testMessageContratoProgramada(){
////        result = robot.showCardProgramada().hideCardAvulsa().start()
////        result?.checkContratoMessageClickHere()
////    }
////
////    /**
////     * Verifica se o texto do contrato esta sendo exibido corretamente,
////     * quando é elegível a Programada e avulsa
////     */
////    @Test
////    @Throws(Exception::class)
////    fun testMessageContrato(){
////        result = robot.showCardAvulsa().showCardProgramada().start()
////        result?.checkContratoMessageClickHere()
////    }
//
//
//}