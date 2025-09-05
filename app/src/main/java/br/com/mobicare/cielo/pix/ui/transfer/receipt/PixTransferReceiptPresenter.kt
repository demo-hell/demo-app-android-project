package br.com.mobicare.cielo.pix.ui.transfer.receipt

import androidx.annotation.VisibleForTesting
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.pix.api.transfer.PixTransferRepositoryContract
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
import br.com.mobicare.cielo.pix.enums.PixQRCodeOperationTypeEnum
import br.com.mobicare.cielo.pix.enums.PixTransferTypeEnum
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixTransferReceiptPresenter(
    private val view: PixTransferReceiptContract.View,
    private val repository: PixTransferRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixTransferReceiptContract.Presenter {

    private var disposible = CompositeDisposable()

    override fun onValidateDetails(
        transactionCode: String?,
        idEndToEnd: String?,
        detailsTransfer: TransferDetailsResponse?
    ) {
        view.showLoading()
        detailsTransfer?.let {
            view.hideLoading()
            showPaymentFlow(it)
        } ?: run {
            onGetDetails(
                transactionCode = transactionCode,
                idEndToEnd = idEndToEnd,
                isShowLoading = false
            )
        }
    }

    @VisibleForTesting
    fun showPaymentFlow(detailsTransfer: TransferDetailsResponse) {
        when (detailsTransfer.transferType) {
            PixTransferTypeEnum.MANUAL.code -> view.onShowManualTransferReceipt(detailsTransfer)
            PixTransferTypeEnum.CHAVE.code -> view.onShowCommonTransfer(detailsTransfer)
            PixTransferTypeEnum.QR_CODE_ESTATICO.code, PixTransferTypeEnum.QR_CODE_DINAMICO.code -> showQrCodePaymentFlow(
                detailsTransfer
            )
        }
    }

    private fun showQrCodePaymentFlow(detailsTransfer: TransferDetailsResponse) {
        detailsTransfer.pixType?.let { itPixType ->
            when (itPixType) {
                PixQRCodeOperationTypeEnum.WITHDRAWAL.name -> view.onShowWithdrawReceipt(
                    detailsTransfer
                )
                PixQRCodeOperationTypeEnum.TRANSFER.name -> view.onShowQrCodePaymentReceipt(
                    detailsTransfer
                )
                PixQRCodeOperationTypeEnum.CHANGE.name -> view.onShowChangeReceipt(detailsTransfer)
            }
        }
    }

    override fun onGetDetails(
        transactionCode: String?,
        idEndToEnd: String?,
        isShowLoading: Boolean
    ) {
        disposible.add(
            repository.getTransferDetails(idEndToEnd, transactionCode)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    if (isShowLoading)
                        view.showLoading()
                }
                .subscribe({
                    view.hideLoading()
                    showPaymentFlow(it)
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