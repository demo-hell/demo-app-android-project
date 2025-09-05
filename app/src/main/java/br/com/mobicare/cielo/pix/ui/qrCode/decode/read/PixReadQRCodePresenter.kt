package br.com.mobicare.cielo.pix.ui.qrCode.decode.read

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.pix.api.qrCode.PixQRCodeRepositoryContract
import br.com.mobicare.cielo.pix.domain.QRCodeDecodeRequest
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixReadQRCodePresenter(
    private val view: PixReadQRCodeContract.View,
    private val userPreferences: UserPreferences,
    private val repository: PixQRCodeRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixReadQRCodeContract.Presenter {

    private var disposible = CompositeDisposable()

    override fun isFirstTimeAskCameraPermission() = userPreferences.cameraPermissionCheck

    override fun onUpdateAskCameraPermission() {
        userPreferences.setCameraPermissionCheck(false)
    }

    override fun onValidateQRCode(qrcode: String) {
        disposible.add(
            repository.decodeQRCode(
                QRCodeDecodeRequest(qrcode)
            )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .subscribe({ response ->
                    view.hideLoading()
                    view.onSuccessValidateQRCode(response)
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