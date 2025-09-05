package br.com.mobicare.cielo.accessManager.addUser

import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.accessManager.addUser.model.AccessManagerCountries
import br.com.mobicare.cielo.accessManager.addUser.model.AccessManagerValidateCpfRequest
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.pix.constants.EMPTY
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class AccessManagerAddUserNationalityPresenter(
    private val repository: AccessManagerRepository,
    private val view: AccessManagerAddUserNationalityContract.View
) : AccessManagerAddUserNationalityContract.Presenter {

    private var disposable = CompositeDisposable()
    private var retryCallback: (() -> Unit)? = null

    override fun getCountries() {
        retryCallback = { getCountries() }
        view.showLoading()

        repository.getCountries(
            UserPreferences.getInstance().token
        )
            .configureIoAndMainThread()
            .subscribe({
                view.showSuccess(it)
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