package br.com.mobicare.cielo.notification.presenter

import br.com.mobicare.cielo.login.domains.entities.CieloInfoDialogContent

interface BiometricNotificationBottomSheetContract {

    interface View {

        fun showBiometricPrompt()
    }
}