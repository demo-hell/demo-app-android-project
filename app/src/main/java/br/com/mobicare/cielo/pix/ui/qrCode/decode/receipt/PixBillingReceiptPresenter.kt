package br.com.mobicare.cielo.pix.ui.qrCode.decode.receipt

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.pix.api.transfer.PixTransferRepositoryContract
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixBillingReceiptPresenter(
    private val view: PixBillingReceiptContract.View,
    private val repository: PixTransferRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixBillingReceiptContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun onGetReceipt(
        transactionCode: String?,
        idEndToEnd: String?
    ) {
        disposable.add(
            repository.getTransferDetails(idEndToEnd, transactionCode)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .subscribe({
                    view.hideLoading()
                    view.onShowReceipt(it)
                }, { error ->
                    view.hideLoading()
                    view.showError(APIUtils.convertToErro(error))
                })
        )
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }

}