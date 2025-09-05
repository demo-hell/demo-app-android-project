package br.com.mobicare.cielo.notification.presenter

import br.com.mobicare.cielo.login.domains.entities.CieloInfoDialogContent

interface WelcomeNotificationContract {

    interface Presenter {

    }


    interface View {

        fun configureWelcomeInfoPage(): CieloInfoDialogContent.PageContent
        fun configureFingerprintInfoPage(): CieloInfoDialogContent.PageContent

        fun canAuthenticateWithBiometrics(): Boolean
    }

}