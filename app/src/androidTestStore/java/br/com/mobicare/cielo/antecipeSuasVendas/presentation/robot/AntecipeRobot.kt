//package br.com.mobicare.cielo.antecipeSuasVendas.presentation.robot
//
//import android.support.test.InstrumentationRegistry
//import android.support.test.espresso.Espresso
//import android.support.test.espresso.action.ViewActions.click
//import android.support.test.espresso.matcher.ViewMatchers
//import android.support.test.runner.AndroidJUnit4
//import android.test.ActivityInstrumentationTestCase2
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.antecipeSuasVendas.domains.entities.Antecipacao
//import br.com.mobicare.cielo.antecipeSuasVendas.domains.entities.AntecipacaoAvulsaObj
//import br.com.mobicare.cielo.antecipeSuasVendas.domains.entities.AntecipacaoProgramadaObj
//import br.com.mobicare.cielo.antecipeSuasVendas.presentation.ui.fragments.AntecipeFragment
//import br.com.mobicare.cielo.commons.presentation.utils.FragmentUtilActivity
//import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//
///**
// * Created by benhur.souza on 20/07/2017.
// */
//
//@RunWith(AndroidJUnit4::class)
//class AntecipeRobot: ActivityInstrumentationTestCase2<FragmentUtilActivity>(FragmentUtilActivity::class.java) {
//
//    lateinit var fragment: AntecipeFragment
//
//    @Before
//    @Throws(Exception::class)
//    fun setup() {
//        super.setUp()
//        injectInstrumentation(InstrumentationRegistry.getInstrumentation())
//        fragment = AntecipeFragment()
//
//        MenuPreference.instance.saveLooseAntecipation(Antecipacao.ELIGIBLE)
//        MenuPreference.instance.saveScheduledAntecipation(Antecipacao.ELIGIBLE)
//
//        //adicionar fragment
//        activity.supportFragmentManager.beginTransaction().addInFrame(R.id.mainframe, fragment).commit()
//        instrumentation.waitForIdleSync()
//
//        testActivityNotNull()
//    }
//
//    @Test
//    fun testActivityNotNull() {
//        val myActivity = activity
//        Assert.assertNotNull(myActivity)
//    }
//
//    fun showLoading(): AntecipeRobot {
//        activity.runOnUiThread { fragment.showProgress() }
//        return this
//    }
//
//    fun showLoadingAvulsa(): AntecipeRobot {
//        activity.runOnUiThread { fragment.showAvulsaProgress() }
//        return this
//    }
//
//    fun showLoadingProgramada(): AntecipeRobot {
//        activity.runOnUiThread { fragment.showProgramadaProgress() }
//        return this
//    }
//
//
//    fun hideLoadingProgramada(): AntecipeRobot {
//        activity.runOnUiThread { fragment.hideProgramadaProgress() }
//        return this
//    }
//
//    fun hideLoadingAvulsa(): AntecipeRobot {
//        activity.runOnUiThread { fragment.hideAvulsaProgress() }
//        return this
//    }
//
//    fun clickCardAvulsa(): AntecipeRobot {
//        Espresso.onView(ViewMatchers.withId(R.id.cardview_antecipe_avulsa)).perform(click())
//        return this
//    }
//
//    fun clickCardProgramada(): AntecipeRobot {
//        Espresso.onView(ViewMatchers.withId(R.id.cardview_antecipe_programada)).perform(click())
//        return this
//    }
//
//    fun showContrato(): AntecipeRobot {
//        activity.runOnUiThread { fragment.loadContrato(R.string.antecipe_contrato) }
//        return this
//    }
//
//    fun clickContrato(): AntecipeRobot {
//        Espresso.onView(ViewMatchers.withId(R.id.layout_antecipe_contrato_check)).perform(click())
//        return this
//    }
//
//    fun showCardHorario(message: String): AntecipeRobot {
//        activity.runOnUiThread { fragment.showCardAvulsaAvisoHorario(message) }
//        return this
//    }
//
//    fun hideCardHorario(): AntecipeRobot {
//        activity.runOnUiThread { fragment.hideCardAvulsaAvisoHorario() }
//        return this
//    }
//
//    fun showCardProgramada(): AntecipeRobot {
//        activity.runOnUiThread { fragment.loadCardProgramada(AntecipacaoProgramadaObj()) }
//        return this
//    }
//
//    fun hideCardProgramada(): AntecipeRobot {
//        activity.runOnUiThread { fragment.hideCardProgramada() }
//        return this
//    }
//
//    fun showCardAvulsa(): AntecipeRobot {
//        activity.runOnUiThread { fragment.loadCardAvulsa(AntecipacaoAvulsaObj()) }
//        return this
//    }
//
//    fun hideCardAvulsa(): AntecipeRobot {
//        activity.runOnUiThread { fragment.hideCardAvulsa() }
//        return this
//    }
//
//    fun showCardAvulsaMessage(message: String): AntecipeRobot {
//        activity.runOnUiThread { fragment.showCardAvulsaMessage(message) }
//        return this
//    }
//
//    fun hideCardAvulsaMessage(): AntecipeRobot {
//        activity.runOnUiThread { fragment.hideCardAvulsaMessage() }
//        return this
//    }
//
//    fun showCardProgramadaMessage(message: String): AntecipeRobot {
//        activity.runOnUiThread { fragment.showCardProgramadaMessage(message) }
//        return this
//    }
//
//    fun showErroAvulsa(message: String): AntecipeRobot {
//        activity.runOnUiThread {
//            fragment.showErrorAvulsa(message)
//        }
//        return this
//    }
//
//    fun showErroProgramada(message: String): AntecipeRobot {
//        activity.runOnUiThread { fragment.showErrorProgramada(message) }
//        return this
//    }
//
//    fun hideCardProgramadaMessage(): AntecipeRobot {
//        activity.runOnUiThread { fragment.hideCardProgramadaMessage() }
//        return this
//    }
//
//    fun loadTaxasCardAvulsa(taxas: AntecipacaoAvulsaObj): AntecipeRobot {
//        activity.runOnUiThread {
//            fragment.loadCardAvulsa(taxas)
//        }
//        return this
//    }
//
//    fun loadTaxasCardProgramada(taxas: AntecipacaoProgramadaObj): AntecipeRobot {
//        activity.runOnUiThread {
//            fragment.loadCardProgramada(taxas)
//        }
//        return this
//    }
//
//    /**
//     * Retorna uma instancia do AntecipeResult para
//     * fazer as verificações das ações na view
//
//     * @return
//     */
//    fun start(): AntecipeResult {
//        return AntecipeResult()
//    }
//}