package br.com.mobicare.cielo.meusCartoes

import android.os.SystemClock.sleep
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.UnlockCreditCardActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class UnlockCreditCardActivityTest {


    @get:Rule
    var rule: ActivityTestRule<UnlockCreditCardActivity> =
            ActivityTestRule(UnlockCreditCardActivity::class.java)

    @Test
    fun testSuccessfulyStart() {
        onView(withId(R.id.frameBackCardContent)).check(matches(isDisplayed()))
        sleep(TimeUnit.MINUTES.toMillis(1))
    }

}