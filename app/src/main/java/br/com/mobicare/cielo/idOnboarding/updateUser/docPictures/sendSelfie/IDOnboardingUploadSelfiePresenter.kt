package br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendSelfie

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.constants.INVALID_SELFIE
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.IDOnboardingRepository
import com.acesso.acessobio_android.services.dto.ResultCamera
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class IDOnboardingUploadSelfiePresenter(
    private val repository: IDOnboardingRepository,
    private val view: IDOnboardingUploadSelfieContract.View
): IDOnboardingUploadSelfieContract.Presenter {

    private var disposable = CompositeDisposable()
    private var retryCallback: (() -> Unit)? = null

    override fun uploadSelfie(result: ResultCamera?) {
        result?.let { selfie ->
            retryCallback = { uploadSelfie(result) }

            view.showLoading(R.string.loading_upload_selfie_picture)

            repository.uploadSelfie(photoBase64 = selfie.base64, jwt = selfie.encrypted)
            .configureIoAndMainThread()
            .subscribe({ onboardingStatus ->
                IDOnboardingFlowHandler.userStatus.onboardingStatus = onboardingStatus
                view.hideLoading(
                    successMessage = R.string.loading_upload_selfie_picture_success,
                    loadingSuccessCallback = { view.successSendingSelfie() }
                )
            },{
                view.hideLoading()
                processorError(ErrorMessage.fromThrowable(it))
            }).addTo(disposable)
        } ?: view.showError()
    }

    override fun uploadSelfie(image: String?, requestId: String?) {
        if (image.isNullOrEmpty().not() && requestId.isNullOrEmpty().not()) {
            retryCallback = { uploadSelfie(image, requestId) }

            view.showLoading(R.string.loading_upload_selfie_picture)

            repository.uploadSelfie(photoBase64 = image, jwt = requestId)
                .configureIoAndMainThread()
                .subscribe({ onboardingStatus ->
                    IDOnboardingFlowHandler.userStatus.onboardingStatus = onboardingStatus
                    view.hideLoading(
                        successMessage = R.string.loading_upload_selfie_picture_success,
                        loadingSuccessCallback = { view.successSendingSelfie() }
                    )
                }, {
                    view.hideLoading()
                    processorError(ErrorMessage.fromThrowable(it))
                }).addTo(disposable)
        } else {
            view.showError()
        }
    }

    override fun getStoneAgeToken() {
        view.basicLoading()

        repository.getStoneAgeToken()
            .configureIoAndMainThread()
            .subscribe({ response ->
                response.token?.let {
                    view.hideBasicLoading()
                    view.successStoneAgeToken(it)
                }
            }, {
                view.hideBasicLoading()
                view.errorStoneAgeToken()
            }).addTo(disposable)
    }

    private fun processorError(error: ErrorMessage){
        if (error.httpStatus == HTTP_ENHANCE && error.errorCode == INVALID_SELFIE)
            view.showErrorInvalidSelfie (error) { retry() }
        else
            view.showError(error) { retry() }
    }

    override fun retry() {
        onResume()
        retryCallback?.invoke()
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }

}