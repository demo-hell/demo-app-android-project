package br.com.mobicare.cielo.pix.ui.extract.reversal.receipt

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.pix.api.extract.reversal.PixReversalRepositoryContract
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixReversalReceiptPresenter(
    private val view: PixReversalReceiptContract.View,
    private val reversalRepository: PixReversalRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixReversalReceiptContract.Presenter {

    private var disposible = CompositeDisposable()

    override fun getReversalDetails(
        transactionCode: String?
    ) {
        disposible.add(
            reversalRepository.getReversalDetailsFull(transactionCode)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .subscribe({
                    view.hideLoading()
                    view.onShowReversalData(it)
                }, { error ->
                    view.hideLoading()
                    view.showError(APIUtils.convertToErro(error))
                })
        )
    }

    override fun onResume() {
        if (disposible.isDisposed) disposible = CompositeDisposable()
    }

    override fun onPause() {
        disposible.dispose()
    }
}