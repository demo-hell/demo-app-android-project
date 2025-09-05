package br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendDocument

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Intent.FILE_TYPE_JPG
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.IDOnboardingRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class IDOnboardingUploadDocumentPresenter(
    private val repository: IDOnboardingRepository,
    private val view: IDOnboardingUploadDocumentContract.View
) : IDOnboardingUploadDocumentContract.Presenter {

    private var disposable = CompositeDisposable()
    private var retryCallback: (() -> Unit)? = null

    override fun uploadDocument(frontBase64: String?, backBase64: String?) {
        if (frontBase64.isNullOrEmpty().not() && backBase64.isNullOrEmpty().not()) {

            retryCallback = { uploadDocument(frontBase64, backBase64) }

            view.showLoading(R.string.loading_upload_doc_picture)

            repository.uploadDocument(
                type = userStatus.documentType?.get(ZERO)?.uppercase(),
                frontBase64 = frontBase64,
                backBase64 = backBase64,
                imageFileType = FILE_TYPE_JPG.uppercase()
            )
                .configureIoAndMainThread()
                .subscribe({ onboardingStatus ->
                    userStatus.onboardingStatus = onboardingStatus
                    view.hideLoading(
                        successMessage = R.string.loading_upload_doc_picture_success,
                        loadingSuccessCallback = { view.successSendingDocument() }
                    )
                }, {
                    view.hideLoading()
                    view.showError(ErrorMessage.fromThrowable(it)) { retry() }
                }).addTo(disposable)
        }
    }

    override fun getStoneAgeToken() {
        view.showLoading()

        repository.getStoneAgeToken()
            .configureIoAndMainThread()
            .subscribe({ response ->
                response.token?.let {
                    view.successStoneAgeToken(it)
                }
            }, {
                view.hideLoading()
                view.errorStoneAgeToken(ErrorMessage.fromThrowable(it))
            }).addTo(disposable)
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