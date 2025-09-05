package br.com.mobicare.cielo.pix.ui.extract.home

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.meusCartoes.CreditCardsRepository
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidResponse
import br.com.mobicare.cielo.pix.api.onboarding.PixRepositoryContract
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixExtractPresenter(
    private val view: PixExtractContract.View,
    private val repository: CreditCardsRepository,
    private val pixRepository: PixRepositoryContract,
    private val userPreferences: UserPreferences,
    private val menuPreference: MenuPreference,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixExtractContract.Presenter {

    private var disposable = CompositeDisposable()
    private var prepaid: PrepaidResponse? = null

    private val isShowBalanceValue: Boolean
        get() {
            return userPreferences.isShowBalanceValue
        }

    override fun onGetUserData() {
        val establishment = menuPreference.getEstablishment()
        val userName = establishment?.tradeName
        val document = establishment?.cnpj
        val ec = establishment?.ec

        if (document?.isNotEmpty() == true && ec?.isNotEmpty() == true)
            view.onUserData(userName, document, ec)
        else {
            if (document.isNullOrEmpty() && ec?.isNotEmpty() == true)
                view.onUserDataHideDocument(userName, ec)
            else {
                if (document?.isNotEmpty() == true && ec.isNullOrEmpty())
                    view.onUserDataHideEC(userName, document)
                else
                    view.onUserDataHideDocumentAndEC(userName)
            }
        }
    }

    override fun onGetCard() {
        view.onShowLoadingBalance()
        if (prepaid != null)
            onGetPixBalance(prepaid)
        else {
            pixRepository.statusPix(object : APICallbackDefault<PrepaidResponse, String> {
                override fun onSuccess(response: PrepaidResponse) {
                    prepaid = response
                    onGetPixBalance(prepaid)
                }

                override fun onError(error: ErrorMessage) {
                    onErrorBalance(error)
                }
            }, userPreferences.token)
        }
    }

    private fun onGetPixBalance(prepaid: PrepaidResponse?) {
        val card = prepaid?.cards?.firstOrNull()
        card?.run {
            proxyNumber?.run {
                disposable.add(
                    repository.getUserCardBalance(proxyNumber, userPreferences.token)
                        .observeOn(uiScheduler)
                        .subscribeOn(ioScheduler)
                        .subscribe({ balance ->
                            card.balance = balance.amount
                            view.onHideLoadingBalance()
                            view.onSuccessGetPixBalance(
                                card,
                                isShowBalanceValue
                            )
                        }, { error ->
                            onErrorBalance(APIUtils.convertToErro(error))
                        })
                )
            } ?: onErrorBalance()
        } ?: onErrorBalance()
    }

    private fun onErrorBalance(errorMessage: ErrorMessage? = null) {
        view.onHideLoadingBalance()
        view.onErrorGetPixBalance(errorMessage)
    }

    override fun onSaveShowBalanceValue(isShow: Boolean) {
        userPreferences.saveShowBalanceValue(isShow)
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}