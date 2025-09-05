package br.com.mobicare.cielo.pix.ui.qrCode.charge.generate

import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.getFormattedKey
import br.com.mobicare.cielo.pix.api.qrCode.PixQRCodeRepositoryContract
import br.com.mobicare.cielo.pix.domain.QRCodeChargeRequest
import br.com.mobicare.cielo.pix.enums.PixClaimTypeEnum
import br.com.mobicare.cielo.pixMVVM.data.model.response.PixKeysResponse
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.net.HttpURLConnection

class PixGenerateQRCodePresenter(
    private val view: PixGenerateQRCodeContract.View,
    private val userPreferences: UserPreferences,
    private val repository: PixQRCodeRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixGenerateQRCodeContract.Presenter {

    private var disposible = CompositeDisposable()
    private var myKey: String? = null

    private var amount: Double? = null
    private var message: String? = null
    private var identifier: String? = null
    private var expirationDate: String? = null

    override fun getUsername(): String = userPreferences.userName

    override fun onGetData() {
        view.onShowData(amount, message, expirationDate, identifier)
    }

    override fun onSaveData(
        amount: Double?,
        message: String?,
        expirationDate: String?,
        identifier: String?
    ) {
        this.amount = amount
        this.message = message
        this.expirationDate = expirationDate
        this.identifier = identifier
    }

    override fun onValidateKey(myKeys: List<PixKeysResponse.KeyItem>?): String? {
        myKeys?.let {
            val key = getFirstActiveKey(it)
            myKey = key.key
            return myKey?.let { itKey ->
                getFormattedKey(itKey, key.keyType)
            }
        }
        return null
    }

    override fun getFirstActiveKey(keys: List<PixKeysResponse.KeyItem>): PixKeysResponse.KeyItem {
        return keys.first { key ->
            key.claimType != PixClaimTypeEnum.PORTABILITY.name
                    && key.claimType != PixClaimTypeEnum.OWNERSHIP.name
        }
    }

    override fun onGenerateQRCode(
        amount: Double?,
        message: String?,
        expirationDate: String?,
        identifier: String?,
        otp: String
    ) {
        disposible.add(
            repository.chargeQRCode(
                otp,
                QRCodeChargeRequest(
                    key = myKey,
                    message = message,
                    expirationDate = expirationDate,
                    originalAmount = amount,
                    txId = identifier
                )
            )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({ response ->
                    view.onSuccessGenerateQRCode(response)
                }, {
                    val error = APIUtils.convertToErro(it)
                    view.onErrorGenerateQRCode(onGenericError = {
                        view.showError(error)
                    })
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