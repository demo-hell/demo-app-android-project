package br.com.mobicare.cielo.notification.list

import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.notification.domain.NotificationItem

class ListNotificationContract {
    interface Presenter: BasePresenter<View> {
        fun getAllNotifications()
        fun onResume()
        fun onDestroy()
    }

    interface View: BaseView, IAttached {
        fun showProgress()
        fun hideProgress()
        fun showNotifications(notifications: List<NotificationItem>)
        fun showEmptyNotifications()
    }
}