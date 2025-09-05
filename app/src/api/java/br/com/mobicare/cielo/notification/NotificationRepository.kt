package br.com.mobicare.cielo.notification

import br.com.mobicare.cielo.notification.domain.NotificationCountResponse
import br.com.mobicare.cielo.notification.domain.NotificationResponse
import io.reactivex.Observable

class NotificationRepository(private val notificationDatasource: NotificationDatasource) {

    fun getAllNotiifcations(): Observable<NotificationResponse> {
        return notificationDatasource.getAlNotifications()
    }

    fun getNotificationsCount(): Observable<NotificationCountResponse> {
        return notificationDatasource.getNotificationCount()
    }
}