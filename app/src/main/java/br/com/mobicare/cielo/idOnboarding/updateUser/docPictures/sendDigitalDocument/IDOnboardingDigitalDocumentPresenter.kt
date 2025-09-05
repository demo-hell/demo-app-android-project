package br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendDigitalDocument

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Intent.FILE_TYPE_PDF
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.IDOnboardingRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.Scheduler

class IDOnboardingDigitalDocumentPresenter(
    private val repository: IDOnboardingRepository,
    private val view: IDOnboardingDigitalDocumentContract.View,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : IDOnboardingDigitalDocumentContract.Presenter {

    private var disposable = CompositeDisposable()
    private var retryCallback: (() -> Unit)? = null

    override fun getStoneAgeToken() {
        disposable.add(
            repository.getStoneAgeToken()
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({ response ->
                    response.token?.let {
                        view.successStoneAgeToken(it)
                    }
                }, {
                    view.errorStoneAgeToken()
                })
        )
    }

    override fun uploadDigitalDocument(digitalDocumentBase64: String?) {
        if (digitalDocumentBase64.isNullOrEmpty().not()) {

            retryCallback = { uploadDigitalDocument(digitalDocumentBase64) }

            view.showLoading(R.string.loading_upload_digital_doc_picture)

            disposable.add(
                repository.uploadDocument(
                    type = userStatus.documentType?.get(ZERO)?.uppercase(),
                    frontBase64 = digitalDocumentBase64,
                    backBase64 = null,
                    imageFileType = FILE_TYPE_PDF.uppercase()
                )
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .subscribe({ onboardingStatus ->
                        userStatus.onboardingStatus = onboardingStatus
                        view.hideLoading(successMessage = R.string.loading_upload_digital_doc_picture_success)
                        view.successSendingDigitalDocument()
                    }, {
                        view.hideLoading()
                        view.errorSendingDigitalDocument(ErrorMessage.fromThrowable(it))
                    })
            )
        }
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