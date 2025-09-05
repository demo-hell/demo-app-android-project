package br.com.mobicare.cielo.pix.ui.extract.receipt

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.pix.api.transfer.PixTransferRepositoryContract
import br.com.mobicare.cielo.pix.domain.SchedulingDetailResponse
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixScheduledTransactionReceiptPresenter(
    private val view: PixScheduledTransactionReceiptContract.View,
    private val repository: PixTransferRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixScheduledTransactionReceiptContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun onGetScheduling(pixSchedulingDetail: SchedulingDetailResponse?, schedulingCode: String?) {
        pixSchedulingDetail?.let {
            view.onShowScheduledTransactionReceipt(it)
        } ?: disposable.add(
            repository.getScheduleDetail(
                schedulingCode
            )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .subscribe({
                    view.onShowScheduledTransactionReceipt(it)
                    view.hideLoading()
                }, {
                    view.hideLoading()
                    view.showError(APIUtils.convertToErro(it))
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