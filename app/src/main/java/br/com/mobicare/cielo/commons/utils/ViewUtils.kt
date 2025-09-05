package br.com.mobicare.cielo.commons.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.DimenRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.GLOBAL
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.ui.barcode.PaymentsBarCodeScanActivity
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.BillsPaymentActivity
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.DirectElectronicTransferActivity
import br.com.mobicare.cielo.research.presentation.ui.ResearchActivityDialog
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

fun Context.showMessage(
    message: String = "",
    title: String = "", customBuilder:
    (AlertDialogCustom.Builder.() -> Unit)? = null
) {


    if (customBuilder == null) {

        val dialogBuilder = AlertDialogCustom.Builder(this, title)
            .setMessage(message)
            .setBtnRight(getString(R.string.ok))
            .setCancelable(false)


        if (title.isNotBlank()) {
            dialogBuilder.setTitle(title)
        }

        dialogBuilder.show()
    } else {

        val dialogBuilder = AlertDialogCustom
            .Builder(this, title)
            .setMessage(message)

        if (title.isNotBlank()) {
            dialogBuilder.setTitle(title)
        }

        dialogBuilder.customBuilder()

        if (this is AppCompatActivity) {
            if (isFinishing) return
        }
        dialogBuilder.show()
    }

    Analytics.trackEvent(
        category = listOf(Category.APP_CIELO, if (!title.isNullOrEmpty()) title else GLOBAL),
        action = listOf(Action.GERAL),
        label = listOf(Label.MENSAGEM, "erro", message ?: "")
    )

}

fun Activity.showExpiredSessionAndLogout() {
    this.showMessage(
        getString(R.string.text_session_timeout_message),
        getString(R.string.text_transfer_error_title)
    ) {
        setBtnRight(getString(R.string.ok))
        setCancelable(true)
        setOnclickListenerRight {
            this@showExpiredSessionAndLogout.finish()
            Utils.logout(this@showExpiredSessionAndLogout)
        }
    }
}

fun Activity.showExpiredSession() {
    this.showMessage(getString(R.string.text_session_timeout_message), "") {
        setBtnRight(getString(R.string.ok))
        setCancelable(true)
        setOnclickListenerRight {
            this@showExpiredSession.finish()
            Utils.logout(this@showExpiredSession)
        }
    }
}

fun Int.dp(): Int = (this * Resources.getSystem().displayMetrics.density).roundToInt()

val countDownTimer: (context: Context, supportFragmentManager: FragmentManager?, screen: String) ->
TimerTask = { context, fragmentManager, screen ->

    object : TimerTask() {
        override fun run() {
            val isResearchToggle = FeatureTogglePreference.instance
                .getFeatureTogle(FeatureTogglePreference.RESEARCHES_SATISFACTION)


            if (isResearchToggle
                && fragmentManager?.fragments.isNullOrEmpty().not()
            ) {
                if (isNotActivityNamedBy(DirectElectronicTransferActivity::class.java.simpleName) &&
                    isNotActivityNamedBy(PaymentsBarCodeScanActivity::class.java.simpleName) &&
                    isNotActivityNamedBy(BillsPaymentActivity::class.java.simpleName)
                ) {
                    ResearchActivityDialog.create(context as Activity, screen)
                    timer.cancel()
                }
            } else {
                timer.cancel()
            }
        }

        private fun isNotActivityNamedBy(activityName: String): Boolean {
            return ActivityDetector.openActivities.isNotEmpty() &&
                    ActivityDetector.openActivities.last() != activityName
        }

    }
}

var startedCountdownResearch: Boolean = false
val timer = Timer()

// PESQUISA DE SATISFACAO
fun Context.initCountDownResearch(
    context: Context, supportFragmentManager: FragmentManager?,
    screen: String
) {
    UserPreferences.getInstance().researchData.apply {
        this.notNull {

            if (!startedCountdownResearch) {
                timer.schedule(
                    countDownTimer(context, supportFragmentManager, screen),
                    TimeUnit.MINUTES.toMillis(1)
                )
                startedCountdownResearch = true
            }
        }
    }

}


fun View.afterLayoutConfiguration(func: () -> Unit) {
    viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver?.removeOnGlobalLayoutListener(this)
            func()
        }
    })
}


fun View.setMargins(
    @DimenRes marginStart: Int? = null,
    @DimenRes topMargin: Int? = null,
    @DimenRes marginEnd: Int? = null,
    @DimenRes bottomMargin: Int? = null
) {
    this.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        marginStart?.let { resources.getDimensionPixelSize(it) }?.let { this.marginStart = it }
        topMargin?.let { resources.getDimensionPixelSize(it) }?.let { this.topMargin = it }
        marginEnd?.let { resources.getDimensionPixelSize(it) }?.let { this.marginEnd = it }
        bottomMargin?.let { resources.getDimensionPixelSize(it) }?.let { this.bottomMargin = it }
    }
}

fun View.hideKeyBoard(activity: Activity) {
    post {
        activity.hideSoftKeyboard()
    }
}