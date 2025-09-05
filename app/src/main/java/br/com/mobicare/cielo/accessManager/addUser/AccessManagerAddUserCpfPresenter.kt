package br.com.mobicare.cielo.accessManager.addUser

import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.accessManager.addUser.model.AccessManagerValidateCpfRequest
import br.com.mobicare.cielo.commons.constants.HTTP_INTERNAL_SERVER_ERROR
import br.com.mobicare.cielo.commons.constants.HTTP_UNKNOWN
import br.com.mobicare.cielo.commons.constants.INVITE_WITH_CPF_EXISTS
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.pix.constants.EMPTY
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class AccessManagerAddUserCpfPresenter(
    private val repository: AccessManagerRepository,
    private val view: AccessManagerAddUserCpfContract.View
) : AccessManagerAddUserCpfContract.Presenter {
    private var disposable = CompositeDisposable()
    private var retryCallback: (() -> Unit)? = null

    override fun validateCpf(cpf: String?) {
        retryCallback = { validateCpf(cpf) }
        view.showLoading()

        repository.validateCpf(
            AccessManagerValidateCpfRequest(cpf = cpf?.replace("[^0-9]".toRegex(), EMPTY)),
            UserPreferences.getInstance().token
        )
            .configureIoAndMainThread()
            .subscribe({
                if (it.isSuccessful) {
                    view.showSuccess(it)
                } else {
                    showError(APIUtils.convertToErro(it))
                }
            }, {
                showError(ErrorMessage.fromThrowable(it))
            }).addTo(disposable)
    }

    private fun showError(error: ErrorMessage?) {
        view.hideLoading()
        when{
            error?.httpStatus == HTTP_INTERNAL_SERVER_ERROR || error?.httpStatus == HTTP_UNKNOWN ->
                view.showErrorGeneric(error)
            error?.errorCode == INVITE_WITH_CPF_EXISTS -> view.onErrorCpfDuplicated()
            else -> view.showErrorLabel(error){ retry() }
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