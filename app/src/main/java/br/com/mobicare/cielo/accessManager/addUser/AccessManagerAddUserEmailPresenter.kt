package br.com.mobicare.cielo.accessManager.addUser

import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.accessManager.addUser.model.AccessManagerValidateEmailRequest
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class AccessManagerAddUserEmailPresenter(
    private val repository: AccessManagerRepository,
    private val view: AccessManagerAddUserEmailContract.View
) : AccessManagerAddUserEmailContract.Presenter {

    private var disposable = CompositeDisposable()
    private var retryCallback: (() -> Unit)? = null

    override fun validateEmail(cpf: String?, email: String?, foreign: Boolean) {
        retryCallback = { validateEmail(cpf, email,foreign) }
        view.showLoading()

        repository.validateEmail(
            AccessManagerValidateEmailRequest(cpf= cpf, email = email, foreign = foreign),
            UserPreferences.getInstance().token
        )
            .configureIoAndMainThread()
            .subscribe({
                if (it.isSuccessful) {
                    view.showSuccess(it)
                } else {
                    view.showError(APIUtils.convertToErro(it)) { retry() }
                }
            }, {
                view.hideLoading()
                view.showError(ErrorMessage.fromThrowable(it)) { retry() }
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