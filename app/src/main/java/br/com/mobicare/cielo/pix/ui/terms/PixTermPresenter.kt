package br.com.mobicare.cielo.pix.ui.terms

import androidx.annotation.VisibleForTesting
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.onboarding.PixRepositoryContract
import retrofit2.Response

class PixTermPresenter(private val view: PixTermContract.View,
                       private val repository: PixRepositoryContract
) : PixTermContract.Presenter {


    override fun sentTermPix(isPartner: Boolean) {
        view.showLoading()
        if (isPartner) sentTermPixPartner()
        else sentTerm()
    }

    @VisibleForTesting
    fun sentTerm() {
        repository.sendTerm(object : APICallbackDefault<Response<Void>, String> {
            override fun onSuccess(response: Response<Void>) {
                view.hideLoading()
                view.successTermPix()
            }

            override fun onError(error: ErrorMessage) {
                view.hideLoading()
                view.showError(error)
            }
        })
    }

    @VisibleForTesting
    fun sentTermPixPartner() {
        repository.sendTermPixPartner(object : APICallbackDefault<Response<Void>, String> {
            override fun onSuccess(response: Response<Void>) {
                view.hideLoading()
                view.successTermPix()
            }

            override fun onError(error: ErrorMessage) {
                view.hideLoading()
                view.showError(error)
            }
        })
    }

    override fun onResume() {
        repository.createDisposable()
    }

    override fun onPause() {
        repository.destroyDisposable()
    }
}