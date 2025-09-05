package br.com.mobicare.cielo.sobreApp.presentation.ui


/**
 * Created by silvia.miranda on 25/04/2017.
 */

interface SobreAppContract {

    interface View {
        fun fillAppVersion(versionName:String, versionCode:Int)
    }

    interface Presenter {
        fun retrieveAppVersion()
    }
}