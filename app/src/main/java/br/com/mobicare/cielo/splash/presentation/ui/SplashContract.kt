package br.com.mobicare.cielo.splash.presentation.ui

import android.content.Intent

/**
 * Created by benhur.souza on 03/04/2017.
 */
interface SplashContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun showError(error: String?)
        fun changeActivity(activity: Class<*>?)
        fun changeActivityToFlowOPF()
        fun startDeepLinkActivity(token: String?)
    }

    interface Presenter {
        fun onResume()
        fun onPause()
        fun callAPI()
        fun callNextActivity()

        /**
         * Verificar se está recebendo algum deeplink
         * para enviar a campanha para o GA
         *
         * @param intent
         */
        fun checkDeepLink(intent: Intent?, openDeepLink: Boolean?): Boolean
    }
}