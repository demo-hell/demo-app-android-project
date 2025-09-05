package br.com.mobicare.cielo.sobreApp.presentation.presenter

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.sobreApp.presentation.ui.SobreAppContract

/**
 * Created by silvia.miranda on 25/04/2017.
 */

class SobreAppPresenter(private val mView: SobreAppContract.View) : SobreAppContract.Presenter {

    override fun retrieveAppVersion() {
        mView.fillAppVersion(BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
    }
}