package br.com.mobicare.cielo.featureToggle.data.managers

import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.featureToggle.api.FeatureToggleAPIDataSource
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.MODAL_DINAMICA
import br.com.mobicare.cielo.featureToggle.domain.*
import br.com.mobicare.cielo.featureToggle.utils.saveFeatureToggleLocally
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FeatureToggleRepository(
    val remoteDataSource: FeatureToggleAPIDataSource
) {

    fun getFeatureToggle(
        disposable: CompositeDisposable,
        params: FeatureToggleParams,
        callback: APICallbackDefault<List<FeatureToggle>, ErrorMessage>
    ) {
        disposable.add(
            getFeaturePage(params, ZERO)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({ features ->
                    saveFeatureToggleLocally(features)
                    callback.onSuccess(features)
                }, { error ->
                    callback.onError(APIUtils.convertToErro(error))
                })
        )
    }

    private fun getFeaturePage(params: FeatureToggleParams, page: Int): Observable<List<FeatureToggle>> {
        return remoteDataSource.getFeatureToggle(params, page).flatMap { featureToggleResponse ->
            return@flatMap if (featureToggleResponse.last == true) {
                Observable.just(featureToggleResponse.content)
            } else {
                Observable.zip(getFeaturePage(params, page + ONE ), Observable.just(featureToggleResponse.content))
                { nextPage, currentPage ->
                    currentPage + nextPage
                }
            }
        }
    }
}