package br.com.mobicare.cielo.meusCartoes.presenter

import android.text.TextUtils
import android.util.Base64
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.meusCartoes.BankTransactionRepository
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BankTransferRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.TransferAuthorization
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.TransferResponse
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.net.HttpURLConnection
import java.util.*

private const val TRANSFERENCE_STATUS_CONFIRMED = "CONFIRMED"

class BankAccountToTransferInputPresenter(
    private var view:
    BankAccountToTransferInputContract.View,
    val bankTransactionRepository: BankTransactionRepository,
    val uiScheduler: Scheduler, val ioScheduler: Scheduler
) :
    BankAccountToTransferInputContract.Presenter {


    private var compositeDisp = CompositeDisposable()


    //TODO fazer a chamada para iniciar a transação
    override fun beginTransfer(
        cardProxy: String?,
        avaiableAmount: Double,
        bankTransferRequest: BankTransferRequest
    ) {


        cardProxy?.let {
            if (bankTransferRequest.amount > avaiableAmount) {
                view.unavaiableAmount()
            } else {
                compositeDisp.add(bankTransactionRepository.beginTransfer(
                    cardProxy,
                    userToken(),
                    bankTransferRequest
                )
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .doOnSubscribe {
                        view.showProgress()
                    }
                    .subscribe({ transferResp ->
                        view.hideProgress()
                        view.nextStep(transferResp)

                    }, { transferError ->

                        view.hideProgress()
                        errorHandler(transferError)

                    })
                )
            }
        } ?: view.showError()

    }

    private fun errorHandler(error: Throwable?) {

        if (view.isAttached()) {
            view.let {
                val errorMessage = APIUtils.convertToErro(error!!)
                when (errorMessage.code.toInt()) {
                    in HttpURLConnection.HTTP_BAD_REQUEST..499 -> {
                        view.showWrongInputDataError()
                    }
                    in HttpURLConnection.HTTP_INTERNAL_ERROR..599 -> {
                        view.showUnavaibleServer()
                    }
                    else -> {
                        if (errorMessage.logout) {
                            it.logout(errorMessage)
                        } else {
                            it.showError()
                        }
                    }
                }
            }
        }
    }

    private fun generateXAuthorization(
        cardProxy: String,
        transferAuthorization: TransferAuthorization
    ): String {
        return with(transferAuthorization.authorization) {
            val proxyNew = "$cardProxy:${cvv}:${expiryDate.replace("/", ":")}"
            Base64.encodeToString(proxyNew.toByteArray(), Base64.NO_WRAP)
        }
    }

    fun confirmTransaction(
        cardProxy: String?, transferAuthorization: TransferAuthorization,
        bankTransferResponse: TransferResponse
    ) {
        cardProxy?.let {
            val splitedDates = transferAuthorization.authorization.expiryDate
                .split("/")

            val transferAuthorizationMonth = splitedDates
                .first()
            val transferAuthorizationYear = splitedDates.last()

            val cal = GregorianCalendar.getInstance()
            val currentYear = cal.get(Calendar.YEAR).toString().substring(2, 4)

            if (TextUtils.isEmpty(transferAuthorization.authorization.expiryDate) ||
                (transferAuthorizationMonth.toInt() > 12 ||
                        transferAuthorizationMonth.toInt() < 1 ||
                        transferAuthorizationYear < currentYear)
            ) {

                view.showWrongExpirationDate()

            } else {

                if (!TextUtils.isEmpty(transferAuthorization.authorization.cvv)) {
                    compositeDisp.add(bankTransactionRepository.confirmTransfer(
                        cardProxy,
                        userToken(),
                        bankTransferResponse.transferId,
                        generateXAuthorization(cardProxy, transferAuthorization)
                    )
                        .subscribeOn(ioScheduler)
                        .observeOn(uiScheduler)
                        .doOnSubscribe {
                            view.showProgress()
                        }.subscribe({ transferConfirmResponse ->
                            view.hideProgress()

                            if (transferConfirmResponse.status == TRANSFERENCE_STATUS_CONFIRMED) {
                                view.transferSuccess(transferConfirmResponse.message)
                            } else {
                                view.wrongTransfer(transferConfirmResponse.message)
                            }

                        }, { transferError ->
                            view.hideProgress()
                            errorHandler(transferError)

                        })
                    )
                } else {
                    view.showEmptyCvv()
                }
            }
        } ?: view.showError()
    }

    private fun userToken(): String {
        return UserPreferences.getInstance().token
    }

    override fun onResume() {
        if (compositeDisp.isDisposed) {
            compositeDisp = CompositeDisposable()
        }
    }

    override fun onDestroy() {
        compositeDisp.dispose()
    }


}