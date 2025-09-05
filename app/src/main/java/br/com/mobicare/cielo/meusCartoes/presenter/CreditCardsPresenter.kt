package br.com.mobicare.cielo.meusCartoes.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.MY_CARDS_PAYMENT
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.MY_CARDS_TRANSFER
import br.com.mobicare.cielo.meusCartoes.CreditCardsRepository
import br.com.mobicare.cielo.meusCartoes.PrepaidRepository
import br.com.mobicare.cielo.meusCartoes.domains.entities.Card
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.SituationType
import br.com.mobicare.cielo.meusCartoes.presentation.ui.CreditCardsContract
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

private const val ERROR_403 = 403
private const val ERROR_420 = 420
private const val ACCESS_DENIED_ISSUER = "ACCESS_DENIED_ISSUER"
private const val ACCESS_DENIED = "ACCESS_DENIED"
private const val TRANSITORY_ACCOUNT = "TRANSITORY_ACCOUNT"

class CreditCardsPresenter(
    private val view: CreditCardsContract.CreditCardsView,
    private val repository: CreditCardsRepository,
    private val prepaidRepository: PrepaidRepository,
    private val userPreferences: UserPreferences,
    private val featureTogglePreference: FeatureTogglePreference,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : CreditCardsContract.Presenter {

    private var disposible = CompositeDisposable()

    override fun fetchCardInformation(isCardSuccess: Boolean) {
        disposible.add(
            prepaidRepository.getUserStatusPrepago(userPreferences.token)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .subscribe({ response ->
                    getAllUserCreditCards(response, isCardSuccess)
                }, { error ->
                    errorHandler(error)
                })
        )
    }

    private fun getAllUserCreditCards(prepaidResponse: PrepaidResponse, isCardSuccess: Boolean) {
        if (isCardSuccess.not()) {
            val card = prepaidResponse.cards?.first()
            card?.run {
                proxyNumber?.run {
                    disposible.add(
                        repository.getUserCardBalance(proxyNumber, userPreferences.token)
                            .observeOn(uiScheduler)
                            .subscribeOn(ioScheduler)
                            .subscribe({ balance ->
                                view.hideLoading()
                                card.balance = balance.amount
                                when (prepaidResponse.status?.type) {
                                    TRANSITORY_ACCOUNT -> view.showPix(prepaidResponse)
                                    else -> view.showCardsInformation(prepaidResponse)
                                }
                            }, { error ->
                                errorHandler(error)
                            })
                    )
                } ?: errorHandler()
            } ?: errorHandler()
        } else
            showBottomFragmentProcess(SituationType.DOCUMENT_SENT_OK.name)
    }

    private fun errorHandler(error: Throwable? = null) {
        view.hideLoading()
        if (error != null) {
            val errorMessage = APIUtils.convertToErro(error)
            if (errorMessage.logout) {
                view.showError()
                view.logout(errorMessage)
            }
            when (errorMessage.httpStatus) {
                ERROR_403 -> setupErrorCode403(errorMessage.errorCode, errorMessage)
                ERROR_420 -> view.showIneligible(errorMessage.errorMessage)
                else -> view.showError()
            }
        } else view.showError()
    }

    private fun setupErrorCode403(errorCode: String, errorMessage: ErrorMessage) {
        when (errorCode) {
            ACCESS_DENIED_ISSUER -> view.showErrorAccessDeniedIssuer()
            ACCESS_DENIED -> view.showNotOwnerError(errorMessage)
            else -> view.showError()
        }
    }

    override fun showBottomFragmentProcess(situationCode: String?, card: Card?, issuer: String) {
        when (situationCode) {
            SituationType.LICENSE_AGREEMENT_OK.name -> processAgreement(card)
            SituationType.DOCUMENT_PROCESSING.name -> view.startCardProcessingFWD()
            SituationType.DOCUMENT_NOK.name -> view.startCardProblemFWD()
            SituationType.DOCUMENT_READ_PROBLEM.name -> view.startCardReadProblemFWD()
            SituationType.DOCUMENT_OK.name -> lastTransactions(card, issuer, true)
            SituationType.DOCUMENT_OK_AND_OTHERS.name,
            SituationType.PIX.name, SituationType.TRANSITORY_ACCOUNT.name -> lastTransactions(
                card,
                issuer
            )
            SituationType.DOCUMENT_SENT_OK.name -> view.startCardSentSuccess()
        }
    }

    private fun lastTransactions(card: Card?, issuer: String, isDoc: Boolean = false) {
        if (isDoc && card?.cardSituation?.type == SituationType.WAITING_ACTIVATION.name)
            view.startCardActivation(issuer)
        else {
            val proxyNumber = card?.proxyNumber ?: ""
            val myCardsPayment = featureTogglePreference.getFeatureTogle(MY_CARDS_PAYMENT)
            val myCardsTransfer = featureTogglePreference.getFeatureTogle(MY_CARDS_TRANSFER)
            view.showLastTransactions(proxyNumber, myCardsTransfer, myCardsPayment)
        }
    }

    private fun processAgreement(card: Card?) {
        if (card?.cardSituation?.type == SituationType.WAITING_ACTIVATION.name)
            view.startCardAccountFWD()
        else
            view.startCardSentSuccess()
    }

    override fun onResume() {
        if (disposible.isDisposed) disposible = CompositeDisposable()
    }

    override fun onPause() {
        disposible.dispose()
    }
}