package br.com.mobicare.cielo.accessManager.assignedUsers.details

import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.commons.constants.CPF_MASK
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.pix.constants.ACTIVE
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class AssignedUserDetailPresenter(
    private val repository: AccessManagerRepository,
    private val view: AssignedUserDetailContract.View,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val userPreferences: UserPreferences,
    private val featureTogglePreference: FeatureTogglePreference
) : AssignedUserDetailContract.Presenter {

    private var disposable = CompositeDisposable()
    override fun getUsername(): String = userPreferences.userName
    override fun getDocument(): String? = userPreferences.userInformation?.activeMerchant?.cnpj?.number
    override fun isCnpj(): Boolean {
        return getDocument()?.let {
            it.length > CPF_MASK.length
        } == true
    }

    override fun assignRole(userId: String, role: String, otpCode: String) {
        disposable.add(
            repository.assignRole(listOf(userId), role, otpCode)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    if (it.isSuccessful) {
                        view.onRoleAssigned()
                    } else {
                        showError(APIUtils.convertToErro(it))
                    }
                }, { error ->
                    showError(APIUtils.convertToErro(error))
                })
        )
    }

    override fun getCustomActiveProfiles(customProfileEnabled: Boolean) {
        if (featureTogglePreference.getFeatureTogle(FeatureTogglePreference.PERFIL_PERSONALIZADO)
            && customProfileEnabled){
            view.showLoading()
            disposable.add(
                repository.getProfiles(Text.CUSTOM, ACTIVE)
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .subscribe({ response ->
                        view.hideLoading()
                        view.showCustomProfiles(response)
                    }, {
                        view.hideLoading()
                        view.showErrorProfile()
                    })
            )
        }
    }

    override fun checkTechnicalToogle() {
        if (!featureTogglePreference.getFeatureTogle(FeatureTogglePreference.PERFIL_TECNICO)){
            view.hideTechnicalUser()
        }
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