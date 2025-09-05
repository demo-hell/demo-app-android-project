package br.com.mobicare.cielo.featureToggle.presenter

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.featureToggle.FeatureToggleContract
import br.com.mobicare.cielo.featureToggle.data.managers.FeatureToggleRepository
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggle
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleParams
import io.reactivex.disposables.CompositeDisposable

class FeatureTogglePresenter(
    private val view: FeatureToggleContract.View,
    private val repository: FeatureToggleRepository
) : FeatureToggleContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun callAPI() {
        repository.getFeatureToggle(
            disposable,
            FeatureToggleParams.getParams(),
            object : APICallbackDefault<List<FeatureToggle>, ErrorMessage> {
                override fun onError(error: ErrorMessage) {
                    view.onFeatureToogleError()
                }

                override fun onSuccess(response: List<FeatureToggle>) {
                    view.onFeatureToogleSuccess()
                }

            })
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}