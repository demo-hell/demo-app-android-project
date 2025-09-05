package br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.ui.activities

//import android.support.test.runner.AndroidJUnit4
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.esqueciUsuario.presentation.robot.EsqueciUsuarioResult
//import br.com.mobicare.cielo.esqueciUsuario.presentation.robot.EsqueciUsuarioRobot
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith

/**
 * Created by Benhur on 04/09/17.
 */

//@RunWith(AndroidJUnit4::class)
//class EsqueciUsuarioActivityTest{
//
//    lateinit var robot: EsqueciUsuarioRobot
//    var result: EsqueciUsuarioResult? = null

//    @Before
//    @Throws(Exception::class)
//    fun setUp() {
//        robot = EsqueciUsuarioRobot()
//        robot.setup()
//    }
//
//    /**
//     * Verifica se o loading nao aparece assim que a tela é carregada
//     */
//    @Test
//    @Throws(Exception::class)
//    fun testProgressIsHide() {
//        result = EsqueciUsuarioResult()
//        result?.loadingIsHide()
//    }

//    /**
//     * Verifica se o titulo e a descricao sao preenchidos corretamente
//     */
//    @Test
//    @Throws(Exception::class)
//    fun testEsqueciUserTitle() {
//        var title = R.string.ajuda_esqueci_usuario
//        var description = R.string.esqueci_usuario_ec_description
//
//        result = EsqueciUsuarioRobot().changeLabels(title, description).start()
//        result?.checkTitle(title)?.checkDescription(description)
//    }

//    /**
//     * Verifica se o titulo e a descricao sao preenchidos corretamente
//     */
//    @Test
//    @Throws(Exception::class)
//    fun testFildMask() {
//        var title = R.string.ajuda_esqueci_usuario
//        var description = R.string.esqueci_usuario_ec_description
//
//        result = EsqueciUsuarioRobot().fillField("Teste").start()
//        result?.compareFiled("Teste")
//    }
//}