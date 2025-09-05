package br.com.mobicare.cielo.pix.ui.extract.account.management

import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.PHONE_WITHOUT_MASK_LENGTH
import br.com.mobicare.cielo.extensions.onlyDigits
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.main.UserInformationRepository
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.pix.api.account.PixAccountRepositoryContract
import br.com.mobicare.cielo.pix.domain.PixProfileRequest
import br.com.mobicare.cielo.pix.enums.RoleEnum
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.net.HttpURLConnection

class PixTransitoryAccountManagementPresenter(
    private val view: PixTransitoryAccountManagementContract.View,
    private val userPreferences: UserPreferences,
    private val userInformationRepository: UserInformationRepository,
    private val repository: PixAccountRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixTransitoryAccountManagementContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun getUsername(): String = userPreferences.userName

    override fun getMerchant() {
        disposable.add(
            repository.getMerchant()
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.onShowLoadingMerchant()
                }
                .doFinally {
                    view.onHideMerchant()
                }
                .subscribe({ response ->
                    view.onSuccessMerchant(response)
                }, { error ->
                    view.onErrorMerchant(APIUtils.convertToErro(error))
                })
        )
    }

    override fun getUserInformation(isShowLoading: Boolean) {
        disposable.add(
            userInformationRepository.getUserInformation(
                accessToken = userPreferences.token
            )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    if (isShowLoading)
                        view.showLoading()
                }
                .subscribe({
                    processDigitalIdentityMigration(it)
                }, { error ->
                    view.hideLoading()
                    view.showError(APIUtils.convertToErro(error))
                })
        )
    }

    private fun processDigitalIdentityMigration(user: MeResponse?) {
        if (user?.mainRole == RoleEnum.READER.name){
            processRoleReader(user)
        }else{
            validateMFA()
        }
    }

    private fun processRoleReader(user: MeResponse?) {
        saveUserData(user)
        if (user?.onboardingRequired == true)
            showDigitalIdentityMigration()
        else
            notAdmin()
    }

    private fun saveUserData(user: MeResponse?) {
        IDOnboardingFlowHandler.userStatus.run {
            cpf = user?.identity?.cpf
            isForeigner = user?.identity?.foreigner
            name = user?.username
            email = user?.email
            if (user?.phoneNumber.onlyDigits().length == PHONE_WITHOUT_MASK_LENGTH) {
                cellphone = user?.phoneNumber
            }
        }
    }

    private fun showDigitalIdentityMigration() {
        view.hideLoading()
        view.onShowIDOnboarding()
    }

    private fun notAdmin() {
        view.hideLoading()
        view.onNotAdmin()
    }

    private fun validateMFA() {
        view.hideLoading()
        view.onValidateMFA()
    }

    override fun changePixAccount(otp: String) {
        disposable.add(
            repository.updateProfile(
                otp, PixProfileRequest(settlementActive = false)
            )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    if (it.code() in 200..204)
                        view.onSuccessChangePixAccount()
                    else
                        view.onErrorChangePixAccount { view.showError() }

                }) {
                    val error = APIUtils.convertToErro(it)
                    view.onErrorChangePixAccount { view.showError(error) }
                }
        )
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}