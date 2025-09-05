package br.com.mobicare.cielo.notification.list

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.notification.NotificationRepository
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class ListNotificationPresenter(
    private val notificationRepository: NotificationRepository,
    val uiScheduler: Scheduler,
    val ioScheduler: Scheduler) : ListNotificationContract.Presenter {

    private var compositeDisp = CompositeDisposable()
    private lateinit var view: ListNotificationContract.View

    override fun setView(view: ListNotificationContract.View) {
        this.view = view
    }

    override fun getAllNotifications() {
        view.showProgress()

        compositeDisp.add(notificationRepository.getAllNotiifcations()
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    view.hideProgress()

                    if (it.notifications?.isNotEmpty() == true) {
                        view.showNotifications(it.notifications)
                    } else {
                        view.showEmptyNotifications()
                    }
                }, {
                    view.hideProgress()
                    onErrorDefault(it)
                })
        )
    }

    private fun onErrorDefault(error: Throwable) {
        view.let {
            if (it.isAttached()) {
                val errorMessage = APIUtils.convertToErro(error)
                if (errorMessage.logout) {
                    it.logout(errorMessage)
                } else {
                    it.showError(errorMessage)
                }
            }
        }
    }

    override fun onResume() {
        if (compositeDisp.isDisposed) compositeDisp = CompositeDisposable()
    }

    override fun onDestroy() {
        compositeDisp.dispose()
    }
}