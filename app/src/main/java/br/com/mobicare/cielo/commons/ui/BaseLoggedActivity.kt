package br.com.mobicare.cielo.commons.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager

@SuppressLint("Registered")
open class BaseLoggedActivity : BaseActivity() {

    private val closeOpenActivities: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            finish()
        }
    }

    companion object {
        const val CLOSE_ACTIVITIES_FROM_BACKSTACK = "br.com.cielo.base.closeActivityFromBackstack"
        const val MFA_TOKEN_ERROR_ACTION = "MFA_TOKEN_ERROR_ACTION"
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            closeOpenActivities,
            IntentFilter(CLOSE_ACTIVITIES_FROM_BACKSTACK)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(closeOpenActivities)
    }
}
