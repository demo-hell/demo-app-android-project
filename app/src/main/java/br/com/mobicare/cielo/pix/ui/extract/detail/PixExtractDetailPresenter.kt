package br.com.mobicare.cielo.pix.ui.extract.detail

import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.pix.api.extract.PixExtractRepositoryContract
import br.com.mobicare.cielo.pix.api.extract.reversal.PixReversalRepository
import br.com.mobicare.cielo.pix.api.transfer.PixTransferRepositoryContract
import br.com.mobicare.cielo.pix.domain.CreditParty
import br.com.mobicare.cielo.pix.domain.DebitParty
import br.com.mobicare.cielo.pix.domain.ReversalDetailsResponse
import br.com.mobicare.cielo.pix.domain.ReversalReceiptsResponse
import br.com.mobicare.cielo.pix.domain.ScheduleCancelRequest
import br.com.mobicare.cielo.pix.domain.TransferDetailsResponse
import br.com.mobicare.cielo.pix.enums.PixExtractTypeEnum.REVERSAL_CREDIT
import br.com.mobicare.cielo.pix.enums.PixQRCodeOperationTypeEnum
import br.com.mobicare.cielo.pix.enums.PixTransactionStatusEnum.EXECUTED
import br.com.mobicare.cielo.pix.enums.PixTransactionStatusEnum.FAILED
import br.com.mobicare.cielo.pix.enums.PixTransactionStatusEnum.PENDING
import br.com.mobicare.cielo.pix.enums.PixTransferOriginEnum
import br.com.mobicare.cielo.pix.enums.PixTransferTypeEnum
import br.com.mobicare.cielo.pix.enums.TransactionTypeEnum
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixExtractDetailPresenter(
    private val view: PixExtractDetailContract.View,
    private val userPreferences: UserPreferences,
    private val pixTransferRepository: PixTransferRepositoryContract,
    private val pixExtractRepository: PixExtractRepositoryContract,
    private val pixReversalRepository: PixReversalRepository,
    private val featureTogglePreference: FeatureTogglePreference,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixExtractDetailContract.Presenter {

    private var disposable = CompositeDisposable()
    private var code: String? = null
    private var idEnd: String? = null
    private var mSchedulingCode: String? = null
    private var updatedDetailsResponse: TransferDetailsResponse? = null
    private var errorCancelTransactionScheduled: ErrorMessage? = null

    var transferDetails: TransferDetailsResponse? = null
    var reversalReceipts: ReversalReceiptsResponse? = null

    private val isExecutedTransaction get() = transferDetails?.transactionStatus == EXECUTED.name

    private val isExecutedRefund get() = reversalReceipts?.items?.firstOrNull()?.receipts?.firstOrNull()?.transactionStatus == EXECUTED.name

    private val isExecutedTransactionOrRefund get() = isExecutedTransaction || isExecutedRefund

    private val ftShowTransactionAnalyzeButton
        get() = featureTogglePreference.getFeatureTogle(
            FeatureTogglePreference.PIX_BUTTON_TRANSACTION_ANALYZE
        )

    override fun getUsername(): String = userPreferences.userName
    override fun getSchedulingCode(): String? = mSchedulingCode
    override fun getUpdatedDetailsResponse(): TransferDetailsResponse? = updatedDetailsResponse

    override fun isShowTransactionAnalyseButton(): Boolean {
        return mSchedulingCode.isNullOrEmpty()
                && ftShowTransactionAnalyzeButton
                && isExecutedTransactionOrRefund
    }

    override fun getDetails(
        transactionCode: String?,
        idEndToEnd: String?,
        schedulingCode: String?
    ) {
        code = transactionCode
        idEnd = idEndToEnd

        schedulingCode?.let {
            mSchedulingCode = it
            getScheduledTransfer(mSchedulingCode)
        } ?: run {
            getCompletedTransfers()
        }
    }

    private fun validateIfGetReversalReceipts(details: TransferDetailsResponse) {
        if (details.transactionType == TransactionTypeEnum.TRANSFER_CREDIT.name)
            getReceipts(idEnd)
    }

    override fun getReceipts(idEndToEnd: String?, isTryAgain: Boolean) {
        disposable.add(
            pixReversalRepository.receipts(idEndToEnd)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    showLoadingReversalFlow(isTryAgain)
                }
                .subscribe({
                    this.reversalReceipts = it
                    transferDetails?.let { itTransferDetails ->
                        view.setReversalData(
                            itTransferDetails
                        )
                    }
                    view.setReversalReceipts(it)

                    hideLoadingReversalFlow(isTryAgain)
                }, {
                    this.reversalReceipts = null
                    hideLoadingReversalFlow(isTryAgain)
                    view.showRefundsHistoryError(transferDetails)
                })
        )
    }

    private fun hideLoadingReversalFlow(isTryAgain: Boolean) {
        if (isTryAgain) view.hideReversalTryAgainLoading()
        else view.hideReversalReceiptsLoading()
    }

    private fun showLoadingReversalFlow(isTryAgain: Boolean) {
        if (isTryAgain) view.showReversalTryAgainLoading()
        else view.showReversalReceiptsLoading()
    }

    private fun getCompletedTransfers() {
        disposable.add(
            pixTransferRepository.getTransferDetails(idEnd, code)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .subscribe({
                    updatedDetailsResponse = it
                    transferDetails = it

                    processTransfer(it)
                    validateIfGetReversalReceipts(it)
                }, { error ->
                    transferDetails = null
                    view.hideLoading()
                    view.showError(APIUtils.convertToErro(error))
                })
        )
    }

    private fun getScheduledTransfer(mSchedulingCode: String?) {
        disposable.add(
            pixTransferRepository.getScheduleDetail(
                mSchedulingCode
            )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .subscribe({
                    view.onPixScheduled(it)
                    view.hideLoading()
                }, {
                    view.hideLoading()
                    view.showError(APIUtils.convertToErro(it))
                })
        )
    }

    override fun getReversalTransactionDetails(transactionCode: String) {
        disposable.add(
            pixReversalRepository.getReversalDetails(transactionCode)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .subscribe({
                    val transferDetailsResponse = castToTransferDetailsResponse(it)
                    defineReversalFlow(transferDetailsResponse)
                    view.hideLoading()
                }, {
                    view.hideLoading()
                    view.showError(APIUtils.convertToErro(it))
                })
        )
    }

    private fun castToTransferDetailsResponse(details: ReversalDetailsResponse) =
        TransferDetailsResponse(
            transactionStatus = details.transactionStatus,
            transactionType = details.transactionType,
            finalAmount = details.finalAmount,
            transactionDate = details.transactionDate,
            payerAnswer = details.payerAnswer,
            transactionCodeOriginal = details.transactionCodeOriginal,
            transactionCode = details.transactionCode,
            creditParty = CreditParty(
                name = details.creditParty?.name,
                bankName = details.creditParty?.bankName,
                nationalRegistration = details.creditParty?.nationalRegistration
            ),
            debitParty = DebitParty(
                name = details.debitParty?.name,
                bankName = details.debitParty?.bankName,
                nationalRegistration = details.debitParty?.nationalRegistration
            )
        )

    private fun defineReversalFlow(response: TransferDetailsResponse) {
        val wasReceived = response.transactionType == REVERSAL_CREDIT.name

        when (response.transactionStatus) {
            PENDING.name ->
                view.onPendingReversalTransaction(response, wasReceived)

            FAILED.name ->
                view.onFailedReversalTransaction(response, wasReceived)

            else ->
                view.onReversalCompletedTransaction(response, wasReceived)
        }
    }

    override fun cancelTransactionScheduled(
        schedulingCode: String?,
        otp: String,
        scheduleCancelRequest: ScheduleCancelRequest
    ) {
        disposable.add(
            pixTransferRepository.cancelTransactionScheduled(otp, scheduleCancelRequest)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    view.onTransactionCancelSuccess {
                        getScheduledTransfer(schedulingCode)
                    }
                }) {
                    errorCancelTransactionScheduled = APIUtils.convertToErro(it)
                    view.hideLoading()
                    view.onProcessError {
                        view.showError(errorCancelTransactionScheduled)
                    }
                }
        )
    }

    private fun isFeeTransaction(details: TransferDetailsResponse) =
        details.transactionType == TransactionTypeEnum.TRANSFER_DEBIT.name
                && details.pixType == PixQRCodeOperationTypeEnum.FEE.name

    private fun isAutomaticTransferTransaction(details: TransferDetailsResponse) =
        details.transactionType == TransactionTypeEnum.TRANSFER_DEBIT.name
                && details.pixType == PixQRCodeOperationTypeEnum.TRANSFER.name
                && details.transferOrigin == PixTransferOriginEnum.SETTLEMENT_V2.name

    private fun processTransfer(details: TransferDetailsResponse) {
        if (isFeeTransaction(details))
            processFeeTransaction(details)
        else if (isAutomaticTransferTransaction(details))
            processAutomaticTransferTransaction(details)
        else
            transactionStatus(details)
    }

    private fun processFeeTransaction(details: TransferDetailsResponse) {
        view.hideLoading()

        when (details.transactionStatus) {
            PENDING.name -> view.onFeeTransferInProcess(details)
            EXECUTED.name -> view.onFeeTransferSent(details)
            else -> view.onFeeTransferCancel(details)
        }
    }

    private fun processAutomaticTransferTransaction(details: TransferDetailsResponse) {
        view.hideLoading()

        when (details.transactionStatus) {
            PENDING.name -> view.onAutomaticTransferInProcess(details)
            EXECUTED.name -> view.onAutomaticTransferSent(details)
            else -> view.onAutomaticTransferCancel(details)
        }
    }

    private fun transactionStatus(details: TransferDetailsResponse) {
        when (details.transactionStatus) {
            PENDING.name -> processTransactionTypePending(details)
            EXECUTED.name -> processTransactionTypeExecuted(details)
            else -> processTransactionTypeCancel(details)
        }
    }

    private fun processTransactionTypePending(details: TransferDetailsResponse) {
        when (details.transferType) {
            PixTransferTypeEnum.QR_CODE_ESTATICO.code, PixTransferTypeEnum.QR_CODE_DINAMICO.code -> {
                transferQRCodePending(
                    details
                )
            }

            else -> {
                view.hideLoading()
                view.onTransferInProcess(details)
            }
        }
    }

    private fun transferQRCodePending(details: TransferDetailsResponse) {
        when (details.pixType) {
            PixQRCodeOperationTypeEnum.WITHDRAWAL.name -> {
                view.onQRCodeWithdrawalTransferInProcess(
                    details
                )
            }

            PixQRCodeOperationTypeEnum.CHANGE.name -> {
                view.onQRCodeChangePaymentInProcess(details)
            }

            else -> view.onQRCodeTransferInProcess(details)
        }
        view.hideLoading()
    }

    private fun processTransactionTypeExecuted(details: TransferDetailsResponse) {
        when (details.transferType) {
            PixTransferTypeEnum.QR_CODE_ESTATICO.code, PixTransferTypeEnum.QR_CODE_DINAMICO.code -> {
                transferQRCodeExecuted(
                    details
                )
            }

            else -> {
                typeTransaction(
                    details,
                    debitAction = { view.onTransferSent(details) },
                    creditAction = { view.onTransferReceived(details) })
            }
        }
    }

    private fun transferQRCodeExecuted(details: TransferDetailsResponse) {
        when (details.pixType) {
            PixQRCodeOperationTypeEnum.WITHDRAWAL.name -> {
                typeTransaction(
                    details,
                    debitAction = { view.onQRCodeWithdrawalTransferSent(details) },
                    creditAction = { view.onQRCodeWithdrawalTransferReceived(details) })
            }

            PixQRCodeOperationTypeEnum.CHANGE.name -> {
                typeTransaction(
                    details,
                    debitAction = { view.onQRCodeChangePaymentSent(details) },
                    creditAction = { view.onQRCodeChangeTransferReceived(details) })
            }

            else -> {
                typeTransaction(
                    details,
                    debitAction = { view.onQRCodeTransferSent(details) },
                    creditAction = { view.onQRCodeTransferReceived(details) })
            }
        }
    }

    private fun typeTransaction(
        details: TransferDetailsResponse,
        debitAction: () -> Unit,
        creditAction: () -> Unit
    ) {
        view.hideLoading()

        if (details.transactionType == TransactionTypeEnum.TRANSFER_CREDIT.name)
            creditAction.invoke()
        else
            debitAction.invoke()
    }

    private fun processTransactionTypeCancel(details: TransferDetailsResponse) {
        when (details.transferType) {
            PixTransferTypeEnum.QR_CODE_ESTATICO.code, PixTransferTypeEnum.QR_CODE_DINAMICO.code -> {
                transferQRCodeCancel(
                    details
                )
            }

            else -> {
                view.hideLoading()
                view.onTransferCancel(details)
            }
        }
    }

    private fun transferQRCodeCancel(details: TransferDetailsResponse) {
        when (details.pixType) {
            PixQRCodeOperationTypeEnum.WITHDRAWAL.name -> {
                view.onQRCodeWithdrawalTransferCancel(details)
            }

            PixQRCodeOperationTypeEnum.CHANGE.name -> {
                view.onQRCodeChangePaymentCancel(details)
            }

            else -> view.onQRCodeTransferCancel(details)
        }
        view.hideLoading()
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }

    override fun canGoToReversal(isHome: Boolean): Boolean {
        return (transferDetails?.expiredReversal?.not() == true
                && (reversalReceipts?.totalAmountPossibleReversal
            ?: ZERO_DOUBLE) > ZERO_DOUBLE)
                && isHome.not()
    }
}