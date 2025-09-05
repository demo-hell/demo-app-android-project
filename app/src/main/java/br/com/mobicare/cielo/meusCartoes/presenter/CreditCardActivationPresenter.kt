package br.com.mobicare.cielo.meusCartoes.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.meusCartoes.CreditCardsRepository
import br.com.mobicare.cielo.meusCartoes.presentation.ui.UnlockCreditCardContract
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import kotlin.properties.Delegates

class CreditCardActivationPresenter(val repository: CreditCardsRepository) :
        UnlockCreditCardContract.Presenter {

    private val subs = CompositeDisposable()

    var uiScheduler: Scheduler by Delegates.notNull()
    var ioScheduler: Scheduler by Delegates.notNull()

    private var view: UnlockCreditCardContract.UnlockCreditCardView? = null

    private var isActivation = false

    override fun setView(unlockCreditCardView:
                         UnlockCreditCardContract.UnlockCreditCardView) {
        this.view = unlockCreditCardView
    }

    override fun startCardActivation(serialNumber: String) {
        if (!isActivation) {
            isActivation = true
            MenuPreference.instance.getEC()?.let { ecNumber ->
                UserPreferences.getInstance().token.run {
                    subs.add(repository
                            .activateCreditCard(ecNumber, this, serialNumber)
                            .subscribeOn(ioScheduler)
                            .observeOn(uiScheduler)
                            .doOnSubscribe {
                                view?.let {
                                    if (it.isAttached()) {
                                        it.showLoading()
                                    }
                                }
                            }
                            .subscribe({ response ->
                                isActivation = false
                                view?.let {
                                    if (it.isAttached()) {
                                        it.hideLoading()
                                        if (response.isSuccessful) {
                                            it.showSuccessActivation()
                                        } else {
                                            it.showInvalidCardNumber()
                                        }
                                    }
                                }
                            }, {
                                isActivation = false
                                onError(it)
                            }))
                }
            }
        }
    }

    private fun onError(e: Throwable) {
        view?.let {
            it.hideLoading()

            val errorMessage = APIUtils.convertToErro(e)
            if (errorMessage.logout) {
                it.logout(errorMessage.message)
            } else {
                it.showError(errorMessage)
            }
        }
    }

}