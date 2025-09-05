package br.com.mobicare.cielo.service

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.splash.presentation.ui.activities.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.salesforce.marketingcloud.messages.push.PushMessageManager
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk
import java.util.*

private const val requestCode = 1251
private const val channelId = "0"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var identificator: Int = ZERO

    override fun onNewToken(tokenFCM: String) {
        super.onNewToken(tokenFCM)

        val token: String? = UserPreferences.getInstance().tokenFCM
        if (TextUtils.isEmpty(token) || token != tokenFCM) {
            UserPreferences.getInstance().saveTokenFCM(tokenFCM)
            UserPreferences.getInstance().saveTokenFcmSent(false)
        }
    }

    override fun onMessageReceived(rm: RemoteMessage) {
        super.onMessageReceived(rm)
        if (PushMessageManager.isMarketingCloudPush(rm)) {
            SFMCSdk.requestSdk { sdk ->
                sdk.mp {
                    it.pushMessageManager.handleMessage(rm)
                }
            }
        } else {
            displayCustomNotificationForOrders(rm.notification?.title, rm.notification?.body)
        }
    }

    private fun displayCustomNotificationForOrders(title: String?, description: String?) {
        identificator = UUID.randomUUID().leastSignificantBits.toInt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            displayCustomNotificationGreaterEqual26(title, description)
        } else {
            displayCustomNotificationSmaller26(title, description)
        }
    }


    private fun createPendingIntent(intent: Intent): PendingIntent {
        return PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun displayCustomNotificationGreaterEqual26(title: String?, textDescription: String?) {
        if (title != null && textDescription != null) {
            val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val intent = Intent(this, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }

            configureChannel(title, textDescription, notifyManager)


            val builder = NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setSmallIcon(getNotificationIcon())
                .setContentText(textDescription)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(createPendingIntent(intent))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            val notification = builder.build()
            notifyManager.notify(identificator, notification)
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun configureChannel(
        title: String,
        textDescription: String,
        notifManager: NotificationManager
    ) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        NotificationChannel(channelId, title, importance).apply {
            description = textDescription
            enableVibration(true)
            notifManager.createNotificationChannel(this)
        }
    }

    private fun displayCustomNotificationSmaller26(title: String?, description: String?) {
        if (title != null && description != null) {
            val intent = Intent(this, SplashActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(description)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(baseContext, R.color.colorPrimary))
                .setSound(defaultSoundUri)
                .setSmallIcon(getNotificationIcon())
                .setContentIntent(createPendingIntent(intent))
                .setStyle(
                    NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(description)
                )

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(identificator, notificationBuilder.build())
        }
    }

    private fun getNotificationIcon(): Int {
        return R.drawable.ic_cielo_notification
    }
}