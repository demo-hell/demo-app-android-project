package br.com.mobicare.cielo.commons.utils

import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.extensions.activity
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.newLogin.NewLoginActivity
import br.com.mobicare.cielo.splash.presentation.ui.activities.SplashActivity
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop

object SessionExpiredHandler {

    var sessionCalled: Boolean = false

    fun userSessionExpires(
        context: Context,
        closeOpenActivities: Boolean = false, isLoginScreen: Boolean = true
    ) {


        synchronized(this) {
            if (!sessionCalled) {
                sessionCalled = true

                if (closeOpenActivities) {
                    LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(
                            Intent(
                                BaseLoggedActivity
                                    .CLOSE_ACTIVITIES_FROM_BACKSTACK
                            )
                        )
                }

                LocalBroadcastManager.getInstance(context)
                    .sendBroadcast(
                        Intent(
                            MainBottomNavigationActivity
                                .HOME_SESSION_EXPIRED_ACTION
                        )
                    )
            }

            context.activity()?.run {
                val intent = if (isLoginScreen)
                    intentFor<NewLoginActivity>()
                else
                    intentFor<SplashActivity>()
                startActivity(
                    intent.clearTop().clearTask()
                )
            }
        }
    }
}