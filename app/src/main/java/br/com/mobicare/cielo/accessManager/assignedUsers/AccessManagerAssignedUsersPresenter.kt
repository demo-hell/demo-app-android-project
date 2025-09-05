package br.com.mobicare.cielo.accessManager.assignedUsers

import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.accessManager.UnlinkUserReason
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.login.domains.entities.UserObj
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class AccessManagerAssignedUsersPresenter(
    private val repository: AccessManagerRepository,
    private val view: AccessManagerAssignedUsersContract.View,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val userPreferences: UserPreferences
) : AccessManagerAssignedUsersContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun getUsername(): String = userPreferences.userName

    override fun unlinkUser(
        userId: String,
        reason: UnlinkUserReason,
        otpCode: String
    ) {
        disposable.add(
            repository.unlinkUser(userId, reason, otpCode)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    view.onUserUnlinked(userId)
                }, { error ->
                    showError(APIUtils.convertToErro(error))
                })
        )
    }

    override fun canUserBeRemoved(userId: String): Boolean {
        val isLoggedUser = userPreferences.userInformation?.id == userId
        if (isLoggedUser.not())
            return true
        val isLoggedUsedAMaster = userPreferences.userInformation?.roles?.contains(UserObj.MASTER)
        return isLoggedUsedAMaster?.not() ?: false
    }

    private fun showError(error: ErrorMessage) {
        view.showError(error)
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}