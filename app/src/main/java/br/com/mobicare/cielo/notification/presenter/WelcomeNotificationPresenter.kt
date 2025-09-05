package br.com.mobicare.cielo.notification.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.login.domains.entities.CieloInfoDialogContent
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class WelcomeNotificationPresenter(val welcomeNotificationView: WelcomeNotificationContract.View,
                                   val userPreferences: UserPreferences,
                                   val uiScheduler: Scheduler,
                                   val ioScheduler: Scheduler) {

    private var compositeDisp = CompositeDisposable()



    fun buildWelcomeInfoNotificationPage(): CieloInfoDialogContent.PageContent? {

        if (userPreferences.isShowOnboardingAppNewsPushNotification) {
            return welcomeNotificationView.configureWelcomeInfoPage()
        }

        return null
    }

    fun buildFingerprintInfoNotificationPage(): CieloInfoDialogContent.PageContent? {
        if (!userPreferences.fingerprintRecorded &&
                userPreferences.keepLogin &&
                welcomeNotificationView.canAuthenticateWithBiometrics() &&
                userPreferences.fingerprintCloseCount() < 3) {
            return welcomeNotificationView.configureFingerprintInfoPage()
        }

        return null
    }

}