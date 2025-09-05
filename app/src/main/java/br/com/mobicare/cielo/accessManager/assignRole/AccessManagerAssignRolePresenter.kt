package br.com.mobicare.cielo.accessManager.assignRole

import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.login.domains.entities.UserObj
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class AccessManagerAssignRolePresenter(
    private val repository: AccessManagerRepository,
    private val view: AccessManagerAssignRoleContract.View
): AccessManagerAssignRoleContract.Presenter {

    private var disposable = CompositeDisposable()
    private var retryCallback: (() -> Unit)? = null

    fun assignRole(
        idList: List<String>,
        role: String,
        otpCode: String,
    ) {
        retryCallback = { assignRole(idList, role, otpCode) }

        view.showLoading()

        repository.assignRole(idList, role, otpCode)
            .configureIoAndMainThread()
            .subscribe({
                view.hideLoading()
                if (it.isSuccessful) {
                    view.roleAssigned(idList.size, role)
                } else {
                    view.showError(APIUtils.convertToErro(it)) { retry() }
                }
            },{
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