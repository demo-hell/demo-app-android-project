package br.com.mobicare.cielo.accessManager.home

import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.extensions.orZero
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ADMIN
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ANALYST
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.READER
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.TECHNICAL
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class AccessManagerHomePresenter(
    private val repository: AccessManagerRepository,
    private val view: AccessManagerHomeContract.View,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val userPreferences: UserPreferences,
    private val featureTogglePreference: FeatureTogglePreference
) : AccessManagerHomeContract.Presenter {

    private var disposable = CompositeDisposable()
    private var retryCallback: (() -> Unit)? = null
    private var foreignFlowAllowed: Boolean = false
    private var customProfileEnabled: Boolean = false

    fun getNoRoleUsers() {
        retryCallback = { getNoRoleUsers() }

        view.showLoading()

        repository.getNoRoleUsers()
            .configureIoAndMainThread()
            .subscribe({ accessManagerUsersResponse ->
                view.showNoRoleUsers(accessManagerUsersResponse)
                view.hideLoading()
            }, {
                view.hideLoading()
                view.showError(ErrorMessage.fromThrowable(it)) { retry() }
            }).addTo(disposable)
    }

    fun getUsers() {
        retryCallback = { getUsers() }

        view.showLoading()

        repository.getUsersWithRole()
            .configureIoAndMainThread()
            .subscribe({ accessManagerUsersResponse ->

                val userList = removeYourself(accessManagerUsersResponse)

                val admins = userList?.filter { it.profile?.id == ADMIN }
                val readers = userList?.filter { it.profile?.id == READER }
                val analyst = userList?.filter { it.profile?.id == ANALYST }
                val technical = userList?.filter { it.profile?.id == TECHNICAL }

                view.showAdminUsers(admins)
                view.showReaderUsers(readers)
                view.showAnalystUsers(analyst)
                validateTechnicalUsers(technical)
                view.hideLoading()

                userPreferences.setAccessManagerFirstView(true)
            }, {
                view.hideLoading()
                view.showError(ErrorMessage.fromThrowable(it)) { retry() }
            }).addTo(disposable)
    }

    private fun validateTechnicalUsers(technical: List<AccessManagerUser>?) {
        if (featureTogglePreference.getFeatureTogle(FeatureTogglePreference.PERFIL_TECNICO)){
            view.showTechnicalUsers(technical)
        } else {
            view.hideTechnicalUsers()
        }
    }

    fun getCustomUsers(customProfileEnabled: Boolean) {
        retryCallback = { getCustomUsers(customProfileEnabled) }

        if (featureTogglePreference.getFeatureTogle(FeatureTogglePreference.PERFIL_PERSONALIZADO)
            && customProfileEnabled){
            view.showLoading()
            disposable.add(
                repository.getCustomUsersWithRole(true, EMPTY)
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .subscribe({ accessManagerUsersResponse ->
                        view.hideLoading()
                        view.showCustomUsers(accessManagerUsersResponse)
                        userPreferences.setAccessManagerFirstView(true)
                    }, {
                        view.hideLoading()
                        view.showError(ErrorMessage.fromThrowable(it))
                    })
            )
        }
    }

    override fun getExpiredInvites() {
        repository.getExpiredInvites()
            .configureIoAndMainThread()
            .subscribe({ expiredInvites ->
                expiredInvites.summary?.totalQuantity?.let { numberExpiredInvitations ->
                    if (numberExpiredInvitations > ZERO)
                        view.showExpiredInvitation(numberExpiredInvitations)
                }
            }, {
                view.hideExpiredInvitation()
            }).addTo(disposable)
    }

    override fun getForeignUsers() {
        repository.getPendingForeignUsers()
            .configureIoAndMainThread()
            .subscribe({ response ->
                if (response.items?.size.orZero > 0)
                    view.showForeignUsers(response.items)
                else
                    view.hideForeignUsers()
            },{
                view.hideForeignUsers()
            }).addTo(disposable)
    }

    override fun getCustomerSettings() {
        view.showLoading()
        retryCallback = { getCustomerSettings() }
        disposable.add(
            repository.getIdOnboardingCustomerSettings()
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({ customerSettings ->
                    view.hideLoading()
                    customerSettings.foreignFlowAllowed?.let{ foreignFlowAllowed = it}
                    customerSettings.customProfileEnabled?.let{ customProfileEnabled = it}
                    view.getCustomUsers(customProfileEnabled)
                },{
                    view.hideLoading()
                    foreignFlowAllowed = false
                    customProfileEnabled = false
                    view.showError(ErrorMessage.fromThrowable(it))
                })
        )
    }

    private fun removeYourself(accessManagerUsersResponse: List<AccessManagerUser>?): List<AccessManagerUser>? {
        return if (userPreferences.userInformation?.identity?.foreigner == true) {
            accessManagerUsersResponse?.filter { userPreferences.userInformation?.email != it.email }
        } else {
            accessManagerUsersResponse?.filter { userPreferences.userInformation?.identity?.cpf != it.cpf }
        }
    }

    override fun getCustomProfileEnabled(): Boolean = customProfileEnabled
    override fun getForeignFlowAllowed(): Boolean = foreignFlowAllowed

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