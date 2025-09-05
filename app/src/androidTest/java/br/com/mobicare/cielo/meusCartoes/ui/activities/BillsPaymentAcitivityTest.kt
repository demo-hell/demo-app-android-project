package br.com.mobicare.cielo.meusCartoes.ui.activities

import android.content.Intent
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.BillsPaymentActivity
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class BillsPaymentAcitivityTest {


    var activityTestRule: ActivityTestRule<BillsPaymentActivity> =
            ActivityTestRule(BillsPaymentActivity::class.java, true,
                    false)


    @Test
    fun startBillsPaymentActivityWithSuccess() {
        activityTestRule.launchActivity(Intent())
        sleep(5000)
    }


}