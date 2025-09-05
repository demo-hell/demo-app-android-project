package br.com.mobicare.cielo.notification

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.notification.domain.NotificationCountResponse
import br.com.mobicare.cielo.notification.domain.NotificationResponse
import io.reactivex.Observable

class NotificationDatasource(val context: Context) {

    val api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun getAlNotifications() : Observable<NotificationResponse>  {
        return api.getAllNotification()
    }

    fun getNotificationCount(): Observable<NotificationCountResponse> {
        return api.getNotificationCount()
    }
}