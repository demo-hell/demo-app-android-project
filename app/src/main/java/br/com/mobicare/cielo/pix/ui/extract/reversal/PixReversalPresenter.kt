package br.com.mobicare.cielo.pix.ui.extract.reversal

import android.os.Handler
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.pix.api.extract.reversal.PixReversalRepositoryContract
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.domain.PixReversalResponse
import br.com.mobicare.cielo.pix.domain.ReversalDetailsResponse
import br.com.mobicare.cielo.pix.domain.ReversalRequest
import br.com.mobicare.cielo.pix.enums.PixTransactionStatusEnum
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

const val DELAY: Long = 2000

class PixReversalPresenter(
    private val view: PixReversalContract.View,
    private val userPreferences: UserPreferences,
    private val repository: PixReversalRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val handler: Handler
) : PixReversalContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun getUsername(): String = userPreferences.userName

    override fun onReverse(
        otp: String,
        idEndToEnd: String?,
        amount: Double,
        message: String?,
        fingerprintAllowme: String,
        idTx: String?
    ) {
        newReversal(
            otp,
            createRequest(idEndToEnd, amount, message, fingerprint = fingerprintAllowme, idTx)
        )
    }

    private fun createRequest(
        idEndToEnd: String?,
        amount: Double,
        message: String?,
        fingerprint: String?,
        idTx: String?
    ): ReversalRequest {
        return ReversalRequest(
            amount = amount,
            idEndToEnd = idEndToEnd,
            reversalReason = message,
            idTx = idTx,
            payerAnswer = EMPTY,
            fingerprint = fingerprint
        )
    }

    private fun newReversal(otp: String, request: ReversalRequest) {
        disposable.add(
            repository.reverse(otp, request)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({ response ->
                    handler.postDelayed({
                        getDetails(response)
                    }, DELAY)
                }, { error ->
                    view.showError(APIUtils.convertToErro(error))
                })
        )
    }

    private fun getDetails(reversal: PixReversalResponse) {
        disposable.add(
            repository.getReversalDetails(reversal.transactionCode)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    validateReversal(it)
                }, {
                    view.onTransactionInProcess()
                })
        )
    }

    private fun validateReversal(
        details: ReversalDetailsResponse
    ) {
        when (details.transactionStatus) {
            PixTransactionStatusEnum.REVERSAL_EXECUTED.name -> view.onShowSuccessReversal(
                details
            )
            PixTransactionStatusEnum.PENDING.name -> view.onTransactionInProcess()
            PixTransactionStatusEnum.NOT_EXECUTED.name -> view.onError()
            else -> view.onTransactionInProcess()
        }
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}