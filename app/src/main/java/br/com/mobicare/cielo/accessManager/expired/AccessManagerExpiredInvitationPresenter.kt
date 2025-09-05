package br.com.mobicare.cielo.accessManager.expired

import androidx.annotation.VisibleForTesting
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.accessManager.model.AccessManagerExpiredInviteResponse
import br.com.mobicare.cielo.accessManager.model.Item
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.net.HttpURLConnection

class AccessManagerExpiredInvitationPresenter(
    private val view: AccessManagerExpiredInvitationContract.View,
    private val repository: AccessManagerRepository,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : AccessManagerExpiredInvitationContract.Presenter {

    private var disposable = CompositeDisposable()

    @VisibleForTesting
    var isFirstPage = true
    private var isLastPage = false
    private var control = false

    override fun getExpiredInvites(isLoading: Boolean, pageNumber: Int) {
        if (isLastPage || control) return
        disposable.add(
            repository.getExpiredInvites(pageNumber = pageNumber)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    control = true
                    showLoading(isLoading)
                }
                .doFinally {
                    control = false
                    hideLoading(isLoading)
                }
                .subscribe({
                    processInvites(it)
                }, { error ->
                    showErrorGetExpiredInvites(APIUtils.convertToErro(error))
                })
        )
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading)
            view.showLoading()
        else view.showLoadingMore()
    }

    private fun hideLoading(isLoading: Boolean) {
        if (isLoading)
            view.hideLoading()
        else view.hideLoadingMore()
    }

    private fun processInvites(expiredInviteResponse: AccessManagerExpiredInviteResponse) {
        isLastPage = expiredInviteResponse.pagination.lastPage
        isFirstPage = expiredInviteResponse.pagination.firstPage

        val isUpdate = expiredInviteResponse.items?.firstOrNull() != null
        if (isUpdate)
            view.onShowExpiredInvites(expiredInviteResponse, isUpdate)
        else
            showErrorGetExpiredInvites()
    }

    private fun showErrorGetExpiredInvites(error: ErrorMessage? = null) {
        if (isFirstPage)
            if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                    OTP
                )
            )
                view.onErrorGetExpiredInvites(error)
    }

    override fun onResendInvite(users: List<Item>, otpCode: String) {
        val usersDocument = createRequest(users)
        if (users.firstOrNull() != null && usersDocument.firstOrNull() != null)
            disposable.add(
                repository.resendInvite(usersDocument, otpCode)
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .doOnSubscribe {
                        view.showLoading(loadingMessage = R.string.access_manager_resend_invite_loading_message)
                    }
                    .doFinally {
                        view.hideLoading()
                    }
                    .subscribe({
                        if (it.code() in HttpURLConnection.HTTP_OK..HttpURLConnection.HTTP_NO_CONTENT)
                            view.onSuccessResendInvite(usersDocument.size)
                        else
                            showError(APIUtils.convertToErro(it))

                    }, { error ->
                        showError(APIUtils.convertToErro(error))
                    })
            )
        else
            showError()
    }

    override fun onDeleteInvite(users: List<Item>, otpCode: String) {
        val usersDocument = createRequest(users)
        if (users.firstOrNull() != null && usersDocument.firstOrNull() != null)
            disposable.add(
                repository.deleteInvite(usersDocument, otpCode)
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .doOnSubscribe {
                        view.showLoading(loadingMessage = R.string.access_manager_delete_invite_loading_message)
                    }
                    .doFinally {
                        view.hideLoading()
                    }
                    .subscribe({
                        if (it.code() in HttpURLConnection.HTTP_OK..HttpURLConnection.HTTP_NO_CONTENT)
                            view.onSuccessDeleteInvite(usersDocument.size)
                        else
                            showError(APIUtils.convertToErro(it))

                    }, { error ->
                        showError(APIUtils.convertToErro(error))
                    })
            )
        else
            showError()
    }

    private fun createRequest(users: List<Item>): ArrayList<String> {
        val usersId: ArrayList<String> = ArrayList()
        users.forEach { user ->
            user.id?.let {
                usersId.add(it)
            }
        }
        return usersId
    }

    private fun showError(error: ErrorMessage? = null) {
        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                OTP
            )
        )
            view.showError(error)
    }

    override fun resetPagination() {
        isLastPage = false
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}