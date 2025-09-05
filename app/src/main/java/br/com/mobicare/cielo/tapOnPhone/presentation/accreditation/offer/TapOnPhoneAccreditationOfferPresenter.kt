package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.offer

import android.app.Activity
import android.os.Build
import br.com.mobicare.cielo.antifraud.KYCAntiFraudContract
import br.com.mobicare.cielo.commons.utils.deviceHasNFC
import br.com.mobicare.cielo.commons.utils.getVersionAndroid
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.AUTOMATIC_RECEIPT_OPTIONAL
import br.com.mobicare.cielo.meuCadastroNovo.domain.Solution
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneAccount
import br.com.mobicare.cielo.tapOnPhone.domain.repository.TapOnPhoneAccreditationRepository
import br.com.mobicare.cielo.tapOnPhone.utils.TapOnPhoneConstants
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class TapOnPhoneAccreditationOfferPresenter(
    private val view: TapOnPhoneAccreditationOfferContract.View,
    private val repository: TapOnPhoneAccreditationRepository,
    private val KYCAntiFraudIntegration: KYCAntiFraudContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val featureTogglePreference: FeatureTogglePreference
) : TapOnPhoneAccreditationOfferContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun getOfferData() {
//        disposable.add(
//            Observable.zip(
//                KYCAntiFraudIntegration.analyzeUserSession().subscribeOn(ioScheduler),
//                repository.loadOffers(
//                    if (isEnabledAutomaticReceiptOptional()) {
//                        TapOnPhoneConstants.REFERENCE_RECEBA_RAPIDO
//                    } else {
//                        null
//                    }
//                ).subscribeOn(ioScheduler),
//                repository.loadAllBrands().subscribeOn(ioScheduler)
//            ) { sessionId, offer, brands ->
//                Triple(sessionId, offer, tapOnPhoneAccountsMap(brands))
//            }
//                .observeOn(uiScheduler)
//                .doOnSubscribe {
//                    view.showLoading()
//                }
//                .subscribe({
//                    view.onLoadDataSuccess(it.first, it.second, it.third)
//                    view.hideLoading()
//                }, { itError ->
//                    val error = APIUtils.convertToErro(itError)
//
//                    view.hideLoading()
//                    if (error.errorCode == REQUIRED_FIELDS) {
//                        view.onShowCallCenter(error)
//                    } else {
//                        view.showError(error)
//                    }
//                })
//        )
    }

    override fun reloadOffer(includeRR: Boolean) {
        disposable.add(
            repository
                .loadOffers(
                    if (includeRR) TapOnPhoneConstants.REFERENCE_RECEBA_RAPIDO else null
                )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoadingOffers()
                }
                .subscribe({ response ->
                    view.hideLoadingOffers()
                    view.onShowOffer(response)
                }, {
                    view.hideLoadingOffers()
                    view.showOfferError()
                })
        )
    }

    override fun isShowBSAlertDeviceIncompatibility(activity: Activity): Boolean {
        return activity.deviceHasNFC().not() || getVersionAndroid() < Build.VERSION_CODES.P
    }

    override fun isEnabledAutomaticReceiptOptional(): Boolean {
        return featureTogglePreference.getFeatureTogle(AUTOMATIC_RECEIPT_OPTIONAL)
    }

    private fun tapOnPhoneAccountsMap(solutions: List<Solution>): List<TapOnPhoneAccount> {
        return solutions.flatMap { solution ->
            solution.banks
        }.map {
            TapOnPhoneAccount(
                bankNumber = it.code,
                imgSource = it.imgSource,
                bankName = it.name.orEmpty(),
                agency = it.agency.orEmpty(),
                account = it.accountNumber.orEmpty(),
                accountDigit = it.accountDigit.orEmpty()
            )
        }
    }

    override fun onDestroy() {
        disposable.dispose()
    }

}