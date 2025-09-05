package br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.robot

/**
 * Created by Benhur on 04/09/17.
 */
//class EsqueciUsuarioRobot: ActivityInstrumentationTestCase2<EsqueciUsuarioAndEstabelecimentoActivity>(EsqueciUsuarioAndEstabelecimentoActivity::class.java) {
//
//    private lateinit var myActivity: EsqueciUsuarioAndEstabelecimentoActivity
//
//    @Before
//    @Throws(Exception::class)
//    fun setup() {
//        super.setUp()
//
//        // Injecting the Instrumentation instance is required
//        // for your test to run with AndroidJUnitRunner.
//        injectInstrumentation(InstrumentationRegistry.getInstrumentation())
//        instrumentation.waitForIdleSync()
//        myActivity = activity
//
//        testActivityNotNull()
//    }
//
//
//    @Test
//    fun testActivityNotNull() {
//        Assert.assertNotNull(myActivity)
//    }
//
//
//    fun showLoading(): EsqueciUsuarioRobot {
//        activity.runOnUiThread { activity.showProgress() }
//        return this
//    }
//
//    fun hideLoading(): EsqueciUsuarioRobot {
//        activity.runOnUiThread { activity.hideProgress() }
//        return this
//    }
//
//    fun clickEnviar(): EsqueciUsuarioRobot {
//        Espresso.onView(ViewMatchers.withId(R.id.button_esqueci_usuario_enviar))
//                .perform(ViewActions.onButtonClick())
//        return this
//    }
//
//    fun changeHint(hintId: Int): EsqueciUsuarioRobot {
//        activity.runOnUiThread { activity.managerField(hintId) }
//        return this
//    }
//
//    fun addMask(hintId: Int): EsqueciUsuarioRobot {
//        activity.runOnUiThread { activity.addMask(hintId) }
//        return this
//    }
//
//    fun showMessage(msg: String?, msgId: Int): EsqueciUsuarioRobot {
//        activity.runOnUiThread { activity.showMessage(msg, msgId, null) }
//        return this
//
//    }
//
//    fun changeLabels(titleId: Int, descriptionId: Int): EsqueciUsuarioRobot{
//        activity.runOnUiThread {
////            activity.changeLabels(titleId, descriptionId)
//        }
//        return this
//    }
//
//    fun showLocalError(@StringRes error: Int): EsqueciUsuarioRobot{
//        activity.runOnUiThread { activity.showLocalError(error) }
//        return this
//    }
//
//    fun fillField(text: String = ""): EsqueciUsuarioRobot {
//        Espresso.onView(ViewMatchers.withId(R.id.text_input_esqueci_usuario_ec))
//                .perform(ViewActions.typeText(text), ViewActions.closeSoftKeyboard())
//
////        Espresso.onView(ViewMatchers.withId(edtId))
////                .perform(ViewActions.typeText(text))
//        return  this
//    }
//
//
//    /**
//     * Retorna uma instancia do EsqueciUsuarioResult para
//     * fazer as verificações das ações na view
//
//     * @return
//     */
//    fun start(): EsqueciUsuarioResult {
//        return EsqueciUsuarioResult()
//    }
//
//
//}