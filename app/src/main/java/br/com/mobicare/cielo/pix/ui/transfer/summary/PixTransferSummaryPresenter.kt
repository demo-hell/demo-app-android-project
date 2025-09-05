package br.com.mobicare.cielo.pix.ui.transfer.summary

import br.com.mobicare.cielo.commons.constants.ERROR_CODE_TOO_MANY_REQUESTS
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.pix.api.extract.PixExtractRepositoryContract
import br.com.mobicare.cielo.pix.api.transfer.PixTransferRepositoryContract
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.domain.*
import br.com.mobicare.cielo.pix.enums.PixTransactionStatusEnum
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixTransferSummaryPresenter(
    private val view: PixTransferSummaryContract.View,
    private val userPreferences: UserPreferences,
    private val pixTransferRepository: PixTransferRepositoryContract,
    private val pixExtractRepository: PixExtractRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixTransferSummaryContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun getUsername(): String = userPreferences.userName

    override fun onTransfer(
        otp: String,
        response: ValidateKeyResponse?,
        amount: Double,
        message: String?,
        fingerprintAllowme: String,
        chosenScheduleDate: String?
    ) {
        response?.let {
            newTransfer(
                otp,
                createRequest(
                    it,
                    amount,
                    message,
                    fingerprint = fingerprintAllowme,
                    chosenScheduleDate
                )
            )
        } ?: run {
            view.showError()
            return
        }
    }

    override fun onBankTransfer(otp: String, request: PixManualTransferRequest?) {
        request?.let { it ->
            disposable.add(
                pixTransferRepository.transferToBankAccount(otp, it)
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .subscribe({ response ->
                        followFlowFromTransactionType(request.schedulingDate, response)
                    }, { transferError ->
                        val error = (APIUtils.convertToErro(transferError))
                        if(error.errorCode == ERROR_CODE_TOO_MANY_REQUESTS){
                            view.onPixManyRequestsError(error)
                        }else{
                            view.showError(error)
                        }
                    })
            )
        } ?: run {
            view.showError()
            return
        }
    }

    private fun followFlowFromTransactionType(
        schedulingDate: String?,
        response: PixTransferResponse
    ) {
        if (schedulingDate.isNullOrEmpty())
            getDetails(response)
        else
            getScheduledDetails(response.schedulingCode)
    }

    private fun createRequest(
        validateKey: ValidateKeyResponse,
        amount: Double,
        message: String?,
        fingerprint: String?,
        chosenScheduleDate: String?
    ): TransferRequest {
        val mAmount = if (amount == ZERO_DOUBLE) null else amount
        return TransferRequest(
            amount = mAmount,
            idEndToEnd = validateKey.endToEndId,
            message = message,
            payee = Payee(key = validateKey.key, keyType = validateKey.keyType),
            fingerprint = fingerprint,
            schedulingDate = chosenScheduleDate
        )
    }

    private fun newTransfer(otp: String, request: TransferRequest) {
        disposable.add(
            pixTransferRepository.transfer(otp, request)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({ response ->
                    followFlowFromTransactionType(request.schedulingDate, response)
                }, { transferError ->
                    val error = (APIUtils.convertToErro(transferError))
                    if(error.errorCode == ERROR_CODE_TOO_MANY_REQUESTS){
                        view.onPixManyRequestsError(error)
                    }else{
                        view.showError(error)
                    }
                })
        )
    }

    private fun getScheduledDetails(schedulingCode: String?) {
        disposable.add(
            pixExtractRepository.getExtract(
                PixExtractFilterRequest(
                receiptsTab = ReceiptsTab.SCHEDULES,
                schedulingCode = schedulingCode
            )
         ).observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({ response ->
                    view.onSuccessFlow {
                        view.showBottomSheetScheduledTransaction(response)
                    }
                }, { error ->
                    view.showError(APIUtils.convertToErro(error))
                })
        )
    }

    private fun getDetails(transfer: PixTransferResponse) {
        disposable.add(
            pixTransferRepository.getTransferDetails(transfer.idEndToEnd, transfer.transactionCode)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    validateTransfer(it, transfer)
                }, {
                    view.onTransactionInProcess()
                })
        )
    }

    private fun validateTransfer(
        details: TransferDetailsResponse,
        transferResponse: PixTransferResponse
    ) {
        when (details.transactionStatus) {
            PixTransactionStatusEnum.EXECUTED.name -> view.onSuccessFlow {
                view.showBottomSheetSuccessfulTransaction(details, transferResponse)
            }
            PixTransactionStatusEnum.PENDING.name -> view.onTransactionInProcess()
            PixTransactionStatusEnum.NOT_EXECUTED.name -> view.onError(
                details.errorMessage ?: EMPTY
            )
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