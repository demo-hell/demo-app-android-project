package br.com.mobicare.cielo.meusCartoes.ui.activities

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.DirectElectronicTransferActivity
import br.com.mobicare.cielo.meusCartoes.ui.activities.robot.DirectElectronicTransferRobot
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers.pathContains
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DirectElectronicTransferActivityTest {

    private var activityTestRule: ActivityTestRule<DirectElectronicTransferActivity> =
            ActivityTestRule(DirectElectronicTransferActivity::class.java,
                    true, false)

    private val directTransferRobot = DirectElectronicTransferRobot()

    //INFO dá para utilizar o padrão Robot
    //directElectronicTransferRobot

    @Test
    fun start_directElectronicTransferWithSuccess() {

        RESTMockServer.whenGET(pathContains("appcielo/v1/banks"))
                .thenReturnFile(200, "meusCartoes/banks.json")

        activityTestRule.launchActivity(Intent())
        onView(withId(R.id.textInputEditAccount)).check(matches(isDisplayed()))
        sleep(5000)
    }

    @Test
    fun fill_inputsTransferInfosAndProceedToTargetInfoStep() {

        RESTMockServer.whenGET(pathContains("appcielo/v1/banks"))
                .thenReturnFile(200, "meusCartoes/banks.json")

        sleep(1000)

        activityTestRule.launchActivity(Intent())

        sleep(1000)

        directTransferRobot.transferBankToPersonInfoStep()
    }


    @Test
    fun fill_inputsTargetInfoStepAndProceedToValueStep() {

        RESTMockServer.whenGET(pathContains("appcielo/v1/banks"))
                .thenReturnFile(200, "meusCartoes/banks.json")

        sleep(1000)

        activityTestRule.launchActivity(Intent())

        sleep(1000)

        directTransferRobot.transferBankToPersonInfoStep()

        sleep(1000)

        directTransferRobot.transferPersonInfoToValueStep()

        sleep(1000)

//        RESTMockServer.whenPOST(pathEndsWith("/transfer"))
//                .thenReturnFile()

        directTransferRobot.transferValueStepToConfirmationStep()

        sleep(1000)
    }

    fun sleep(millis: Long) {
        Thread.sleep(millis)
    }



}