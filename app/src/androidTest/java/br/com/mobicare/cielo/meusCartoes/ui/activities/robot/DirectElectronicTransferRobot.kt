package br.com.mobicare.cielo.meusCartoes.ui.activities.robot

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import br.com.mobicare.cielo.R

class DirectElectronicTransferRobot {


    fun fillTranferBankInfo() {
        Espresso.onView(ViewMatchers.withId(R.id.textInputEditAgencyNumber))
                .perform(ViewActions.typeText("12345"))
        Espresso.onView(ViewMatchers.withId(R.id.textInputEditAccount))
                .perform(ViewActions.typeText("12345"))
        Espresso.onView(ViewMatchers.withId(R.id.textInputEditAccountDigit))
                .perform(ViewActions.typeText("10"))
    }

    fun checkButtonTransferBankInfoDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.buttonDirectElectronicTransferNext))
                .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    fun clickNextBankInfoTranfer() {
        Espresso.onView(ViewMatchers.withId(R.id.buttonDirectElectronicTransferNext))
                .perform(ViewActions.click())
    }

    fun checkTargetPersonInfoScreenDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.textInputEditName))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    fun transferBankToPersonInfoStep() {

        fillTranferBankInfo()

        sleep(1000)

        checkButtonTransferBankInfoDisplayed()
        clickNextBankInfoTranfer()

        sleep(1000)

        checkTargetPersonInfoScreenDisplayed()

        sleep(5000)
    }

    fun transferPersonInfoToValueStep() {

        fillPersonInfo()

        sleep(1000)

        checkButtonPersonInfoDisplayed()
        clickNextPersonInfo()

        sleep(1000)

        checkValueInfoScreenDisplayed()

        sleep(5000)
    }


    fun transferValueStepToConfirmationStep() {

        fillValue()

        sleep(1000)

    }

    private fun fillValue() {
        Espresso.onView(ViewMatchers.withId(R.id.textValueMoney))
                .perform(ViewActions.typeText("200"))
    }

    private fun checkValueInfoScreenDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.textValueMoney))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun clickNextPersonInfo() {
        Espresso.onView(ViewMatchers.withId(R.id.buttonTransferIndentifyNext))
                .perform(ViewActions.click())
    }

    private fun checkButtonPersonInfoDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.buttonTransferIndentifyNext))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun fillPersonInfo() {

        Espresso.onView(ViewMatchers.withId(R.id.textInputEditName))
                .perform(ViewActions.typeText("Bruce Dickinson"))
        Espresso.onView(ViewMatchers.withId(R.id.textInputEditCpfCnpj))
                .perform(ViewActions.typeText("07884993783"))
        Espresso.onView(ViewMatchers.withId(R.id.textInputEditDescription))
                .perform(ViewActions.typeText(""))

    }

    fun sleep(millis: Long) {
        Thread.sleep(millis)
    }
}