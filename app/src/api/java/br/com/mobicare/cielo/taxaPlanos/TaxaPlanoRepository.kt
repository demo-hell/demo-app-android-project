package br.com.mobicare.cielo.taxaPlanos

import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.home.presentation.incomingfast.model.EligibleOffer
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrandFees
import br.com.mobicare.cielo.taxaPlanos.domain.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class TaxaPlanoRepository(private val remoteDataSource: TaxaPlanoDataSource) : DisposableDefault {

    private var compositeDisp = CompositeDisposable()

    override fun disposable() {
        compositeDisp.dispose()
    }

    override fun onResume() {
        if (compositeDisp.isDisposed) compositeDisp = CompositeDisposable()
    }

    fun loadStatusPlan(token: String, callback: APICallbackDefault<TaxaPlanosStatusPlanResponse, String>) {
        compositeDisp.add(remoteDataSource.loadStatusPlan(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                }))
    }

    fun loadPlanDetails(planName: String, callback: APICallbackDefault<TaxaPlanosDetailsResponse, String>) {
        compositeDisp.add(remoteDataSource.loadPlanDetails(planName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                }))
    }

    fun loadOverview(token: String, type: String, callback: APICallbackDefault<TaxaPlanosOverviewResponse, String>) {
        compositeDisp.add(remoteDataSource.loadOverview(token, type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                }))
    }

    fun loadMachine(token: String, callback: APICallbackDefault<TaxaPlanosSolutionResponse, String>) {
        compositeDisp.add(remoteDataSource.loadMarchine(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                }))
    }

    fun getOfferIncomingFastDetail(callback: APICallbackDefault<OfferIncomingFastDetailResponse, String>) {
        compositeDisp.add(remoteDataSource.getOfferIncomingFastDetail()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                }))
    }

    fun getEligibleToOffer(callback: APICallbackDefault<EligibleOffer, String>) {
        compositeDisp.add(remoteDataSource.getEligibleToOffer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(it)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                }))
    }

    fun isEnabledIncomingFastFT() = remoteDataSource.isEnabledIncomingFastFT()

    fun isEnabledCancelIncomingFastFT() = remoteDataSource.isEnabledCancelIncomingFastFT()
}