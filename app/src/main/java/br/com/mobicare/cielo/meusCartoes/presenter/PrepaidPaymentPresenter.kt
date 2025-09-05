package br.com.mobicare.cielo.meusCartoes.presenter

import android.util.Base64
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.codebar.CodebarUtils
import br.com.mobicare.cielo.commons.utils.format
import br.com.mobicare.cielo.meusCartoes.PrepaidRepository
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.TransferAuthorization
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentRequest
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PrepaidPaymentPresenter(
    val view: PaymentContract.View,
    val paymentRepository: PrepaidRepository,
    val uiScheduler: Scheduler, val ioScheduler: Scheduler
) :
    PaymentContract.Presenter {

    private var compositeDisp = CompositeDisposable()

    private var cde = CodebarUtils()

    override fun createPayment(cardProxy: String?, paymentRequest: PrepaidPaymentRequest) {
        cardProxy?.run {
            compositeDisp.add(paymentRepository
                .createPayment(this, UserPreferences.getInstance().token, paymentRequest)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnSubscribe {
                    if (view.isAttached())
                        view.showProgress()
                }
                .subscribe({ response ->
                    if (view.isAttached()) {
                        view.hideProgress()
                        view.finishCreatePayment(response)
                    }

                }, { throwable ->
                    if (view.isAttached()) {
                        view.hideProgress()
                        errorHandler(throwable)
                    }
                })
            )

        } ?: view.showError(ErrorMessage())
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

    override fun confirmPayment(
        cardProxy: String?,
        paymentId: String?,
        transferAuthorization: TransferAuthorization
    ) {
        cardProxy?.run {
            paymentId?.run {
                compositeDisp.add(paymentRepository
                    .confirmPayment(
                        cardProxy,
                        paymentId,
                        UserPreferences.getInstance().token,
                        generateXAuthorization(cardProxy, transferAuthorization)
                    )
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .doOnSubscribe {
                        if (view.isAttached()) {
                            view.showProgress()
                        }
                    }
                    .subscribe({
                        if (view.isAttached()) {
                            view.hideProgress()
                            view.responseSucess()
                        }
                    }, { throwable ->
                        if (view.isAttached()) {
                            view.hideProgress()
                            errorHandler(throwable)
                        }
                    })
                )

            } ?: view.showError(ErrorMessage())
        } ?: view.showError(ErrorMessage())
    }

    private fun errorHandler(error: Throwable?) {

        if (view.isAttached()) {
            view.let {
                val errorMessage = APIUtils.convertToErro(error!!)
                if (errorMessage.logout) {
                    it.logout(errorMessage)
                } else {
                    it.showError(errorMessage)
                }
            }
        }
    }

    override fun onResume() {
        if (compositeDisp.isDisposed) {
            compositeDisp = CompositeDisposable()
        }
    }

    override fun onDestroy() {
        compositeDisp.dispose()
    }

    /**
     * method to return value of barcode
     * @param barcode
     * @return
     * */

    override fun validationBarcode(barcode: String): String {
        return cde.getDigitableCodeFrom(barcode).codebarLine
    }

    /**
     * method to return value of the Colletion or Ticket
     * @param barcode
     * @return
     * */
    override fun validationValueCollectionOrTicket(barcode: String): String {
        return cde.getDigitableCodeFrom(barcode).codebarValue.toString()
    }


    /**
     * method to return value of the Date Maturity
     * @param barcode
     * @return
     * */
    override fun validationValueDateMaturity(barcode: String): String? {
        return cde.getDigitableCodeFrom(barcode).cadebarDate?.let {
            it.format()
        }
    }

    /**
     * method to return value of the Bank
     * @param barcode
     * @return
     * */
    override fun validationNameBank(barcode: String): String? {
        return cde.getDigitableCodeFrom(barcode).codebarBank
    }

    /**
     * method to return value of the Agreement
     * @param barcode
     * @return
     * */
    override fun validationNameAgreement(barcode: String): String? {
        return cde.getDigitableCodeFrom(barcode).codebarAgreement
    }


    /**
     * method to return validation of barcode if is valid
     * @param barcode
     * @return
     * */
    override fun validationBarcodeValid(barcode: String): Boolean {
        return cde.getDigitableCodeFrom(barcode).codevarValid
    }

}