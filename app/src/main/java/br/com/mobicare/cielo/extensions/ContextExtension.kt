package br.com.mobicare.cielo.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.pixMVVM.presentation.extract.PixNewExtractNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.router.PixRouterNavigationFlowActivity
import org.jetbrains.anko.*

fun Context?.backToHome(vararg extras: Pair<String, Any?>) {
    this.activity()?.run {
        startActivity(
            intentFor<MainBottomNavigationActivity>(*extras).singleTop().clearTop().apply {
                if (extras.isNotEmpty()) putExtras(bundleOf(*extras))
            }
        )
    }
}

fun Context?.moveToHome(
    vararg extras: Pair<String, String>,
    homeIndex: Int? = null
) {

    if (homeIndex != null) {
        this?.let {
            LocalBroadcastManager.getInstance(it)
                .sendBroadcast(
                    Intent(
                        MainBottomNavigationActivity
                            .NAVIGATE_TO_ACTION
                    ).apply {
                        this.putExtra(
                            MainBottomNavigationActivity.HOME_INDEX_KEY,
                            MainBottomNavigationActivity.HOME_INDEX
                        )
                    })

            LocalBroadcastManager.getInstance(it)
                .sendBroadcast(Intent(BaseLoggedActivity.CLOSE_ACTIVITIES_FROM_BACKSTACK))
        }
    } else {
        this.activity()?.run {
            startActivity(
                Intent(this, MainBottomNavigationActivity::class.java).apply {
                    this.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    this.putExtras(bundleOf(*extras))
                }
            )
        }
    }
}

fun Context?.toHomePix(vararg extras: Pair<String, Any?>) {
    this.activity()?.run {
        startActivity(
            intentFor<PixRouterNavigationFlowActivity>(*extras).clearTop().clearTask().apply {
                if (extras.isNotEmpty()) putExtras(bundleOf(*extras))
            }
        )
    }
}

fun Context?.toPixHomeExtract(vararg extras: Pair<String, Any?>) {
    this.activity()?.run {
        startActivity(
            intentFor<PixNewExtractNavigationFlowActivity>(*extras).clearTop().clearTask().apply {
                if (extras.isNotEmpty()) putExtras(bundleOf(*extras))
            }
        )
    }
}

fun Context?.activity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.activity()
        else -> null
    }
}

fun Context.showToast(text: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, length).show()
}