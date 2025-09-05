package br.com.mobicare.cielo.pix.ui.qrCode.decode.summary

import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.getTotalValueChange
import br.com.mobicare.cielo.pix.api.qrCode.PixQRCodeRepositoryContract
import br.com.mobicare.cielo.pix.api.transfer.PixTransferRepositoryContract
import br.com.mobicare.cielo.pix.domain.*
import br.com.mobicare.cielo.pix.enums.*
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixDecodeQRCodeSummaryPresenter(
    private val view: PixDecodeQRCodeSummaryContract.View,
    private val userPreferences: UserPreferences,
    private val repository: PixTransferRepositoryContract,
    private val qrCodeRepository: PixQRCodeRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
) : PixDecodeQRCodeSummaryContract.Presenter {

    private var disposable = CompositeDisposable()

    private var _qrcode: String? = null
    private var _scheduling: String? = null

    override fun getUsername(): String = userPreferences.userName

    override fun getBankName(decode: QRCodeDecodeResponse): String? {
        return when (decode.pixType) {
            PixQRCodeOperationTypeEnum.CHANGE.name -> decode.ispbChangeName
            PixQRCodeOperationTypeEnum.WITHDRAWAL.name -> decode.ispbWithDrawName
            else -> decode.participantName
        }
    }

    override fun getTransferValue(decode: QRCodeDecodeResponse): Double {
        return when (decode.pixType) {
            PixQRCodeOperationTypeEnum.CHANGE.name -> decode.changeAmount ?: ZERO_DOUBLE
            PixQRCodeOperationTypeEnum.WITHDRAWAL.name -> decode.withDrawAmount ?: ZERO_DOUBLE
            else -> decode.finalAmount ?: ZERO_DOUBLE
        }
    }

    override fun isAllowedChangeValue(decode: QRCodeDecodeResponse): Boolean {
        val allows = if (decode.type == PixQRCodeTypeEnum.DYNAMIC_COBV.name)
            PixAllowsChangeValueEnum.NOT_ALLOWED.name else {
            when (decode.pixType) {
                PixQRCodeOperationTypeEnum.CHANGE.name -> decode.modalityAltChange
                PixQRCodeOperationTypeEnum.WITHDRAWAL.name -> decode.modalityAltWithDraw
                else -> decode.modalityAlteration
            }
        }
        return allows == PixAllowsChangeValueEnum.ALLOWED.name
    }

    override fun onDecode(qrcode: String?, scheduling: String?) {
        qrcode?.let { _qrcode = it }
        scheduling?.let { _scheduling = it }
        _qrcode?.let {
            disposable.add(
                qrCodeRepository.decodeQRCode(QRCodeDecodeRequest(it, _scheduling))
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .doOnSubscribe {
                        view.showLoading()
                    }
                    .subscribe({ decode ->
                        view.hideLoading()
                        view.onSuccessDecode(decode, _scheduling)

                    }, { error ->
                        view.hideLoading()
                        view.onErrorDecode(APIUtils.convertToErro(error))
                    })
            )
        } ?: run {
            view.onErrorDecode()
        }
    }

    override fun onPayQRCode(
        otp: String,
        message: String?,
        date: String?,
        amount: Double?,
        decodeResponse: QRCodeDecodeResponse?,
        fingerprint: String
    ) {
        val body = request(message, date, amount, fingerprint, decodeResponse)
        if (body == null)
            showError()
        else
            disposable.add(
                repository.transfer(otp, body)
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .subscribe({ response ->
                        validateTransfer(response)
                    }, { error ->
                        showError(APIUtils.convertToErro(error))
                    })
            )
    }

    private fun request(
        message: String?,
        date: String?,
        amount: Double?,
        fingerprint: String,
        decodeResponse: QRCodeDecodeResponse?,
    ): TransferRequest? {

        if (amount == null || decodeResponse == null)
            return null

        val transferType = if (decodeResponse.type == PixQRCodeTypeEnum.STATIC.name)
            PixTransferTypeEnum.QR_CODE_ESTATICO.name else PixTransferTypeEnum.QR_CODE_DINAMICO.name

        val request = TransferRequest(
            idEndToEnd = decodeResponse.endToEndId,
            message = message,
            payee = Payee(key = decodeResponse.key, keyType = decodeResponse.keyType),
            pixType = decodeResponse.pixType,
            transferType = transferType,
            idTx = decodeResponse.idTx,
            fingerprint = fingerprint,
            schedulingDate = date
        )

        when (decodeResponse.pixType) {
            PixQRCodeOperationTypeEnum.CHANGE.name -> {
                request.changeAmount = amount.toString()

                request.purchaseAmount = decodeResponse.originalAmount?.toString()
                request.agentMode = decodeResponse.modalityChangeAgent
                request.agentWithdrawalIspb = decodeResponse.ispbChange

                val totalValue = getTotalValueChange(amount, decodeResponse.originalAmount)
                request.amount = totalValue
            }
            PixQRCodeOperationTypeEnum.WITHDRAWAL.name -> {
                request.agentMode = decodeResponse.modalityWithDrawAgent
                request.agentWithdrawalIspb = decodeResponse.ispbWithDraw
                request.amount = amount
            }

            else -> request.amount = amount
        }

        return request
    }

    private fun validateTransfer(
        transferResponse: PixTransferResponse
    ) {
        when (transferResponse.transactionStatus) {
            PixTransactionStatusEnum.EXECUTED.name -> view.onSuccessPayQRCode {
                if (transferResponse.schedulingCode != null)
                    view.onSuccessScheduling(
                        transferResponse
                    )
                else
                    view.onSuccessfulPayment(
                        transferResponse
                    )
            }

            PixTransactionStatusEnum.PENDING.name -> view.onSuccessPayQRCode {
                view.onTransactionInProcess()
            }
            PixTransactionStatusEnum.NOT_EXECUTED.name -> view.onSuccessPayQRCode {
                view.onPaymentError()
            }
            else -> view.onSuccessPayQRCode {
                view.onTransactionInProcess()
            }
        }
    }

    private fun showError(errorMessage: ErrorMessage? = null) {
        view.onErrorPayQRCode(onGenericError = {
            view.showError(errorMessage)
        })
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}