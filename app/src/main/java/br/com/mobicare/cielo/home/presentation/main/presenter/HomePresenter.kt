package br.com.mobicare.cielo.home.presentation.main.presenter

import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.enums.EnrollmentType
import br.com.mobicare.cielo.commons.helpers.MenuHelper
import br.com.mobicare.cielo.commons.router.Router.Companion.APP_ANDROID_POS_VIRTUAL
import br.com.mobicare.cielo.commons.router.Router.Companion.APP_ANDROID_TAP_PHONE
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.MFA_EC_STATUS_VALIDACAO
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POS_VIRTUAL_BUTTON_HOME
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POS_VIRTUAL_WHITE_LIST
import br.com.mobicare.cielo.home.presentation.main.BannersContract
import br.com.mobicare.cielo.home.presentation.main.MenuContract
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.IDOnboardingRepository
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.main.MenuRepository
import br.com.mobicare.cielo.main.UserInformationRepository
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.meusCartoes.PrepaidRepository
import br.com.mobicare.cielo.mfa.BankEnrollmentResponse
import br.com.mobicare.cielo.mfa.MfaAccount
import br.com.mobicare.cielo.mfa.MfaEligibilityResponse
import br.com.mobicare.cielo.mfa.MfaRepository
import br.com.mobicare.cielo.mfa.commons.EnrollmentStatus
import br.com.mobicare.cielo.mfa.commons.MerchantStatusMFA
import br.com.mobicare.cielo.migration.MigrationRepository
import br.com.mobicare.cielo.notification.NotificationRepository
import br.com.mobicare.cielo.pix.constants.EMPTY
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.net.HttpURLConnection
import java.util.Locale

class HomePresenter(
    private val homeView: BannersContract.View,
    private val menuView: MenuContract.View,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val prepaidRepository: PrepaidRepository,
    private val migrationRepository: MigrationRepository,
    private val repositoryNotification: NotificationRepository,
    private val menuRepository: MenuRepository,
    private val mfaRepository: MfaRepository,
    private val idRepository: IDOnboardingRepository,
    private val userInformationRepository: UserInformationRepository,
    private val featureTogglePreference: FeatureTogglePreference,
    private val userPreferences: UserPreferences,
    private val menuPreference: MenuPreference
) : BannersContract.Presenter {

    private val disposableHandler = CompositeDisposableHandler()

    override fun onResume() {
        disposableHandler.start()
        mfaRepository.onStart()
    }

    override fun onDestroy() {
        disposableHandler.destroy()
        mfaRepository.onDispose()
    }

    override fun loadUserStatus() {
        if (featureTogglePreference.isActivate(FeatureTogglePreference.MY_CARDS_BANNER)) {
            disposableHandler.compositeDisposable.add(
                prepaidRepository
                    .getUserStatusPrepago(userPreferences.token)
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .subscribe({ data ->
                        homeView.loadCardPrepago(data.status)
                    }, {
                        homeView.errorLoadStatus()
                    })
            )
        }
    }

    override fun vericationIfUserMigration() {
        disposableHandler.compositeDisposable.add(
            migrationRepository
                .getMigrationVefication(userPreferences.token)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    if (it.code() == HttpURLConnection.HTTP_OK) {
                        homeView.loadBannerMigration()
                    }
                }, {
                    homeView.errorLoadStatus()
                })
        )
    }

    override fun loadNotification(notificationCount: (Int) -> Unit) {
        disposableHandler.compositeDisposable.add(
            repositoryNotification.getNotificationsCount()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    notificationCount(it.totalElements ?: ZERO)
                }, {
                    notificationCount(ZERO)
                })
        )
    }

    override fun loadMenu() {
        menuPreference.getUserObj().let { userObj ->
            if (userObj?.mainRole != UserObj.CUSTOM) {
                getMenu()
            }
        }
    }

    private fun getMenu() {
        menuRepository.getMenu(userPreferences.token)
            ?.subscribeOn(ioScheduler)
            ?.observeOn(uiScheduler)
            ?.doOnSubscribe {
                homeView.showLoading()
            }?.subscribe({ menuResponse ->
                homeView.hideLoading()
                menuResponse?.menu?.let {
                    MenuHelper.getHome(it)?.let { itMenu ->
                        processMenu(itMenu)
                    }
                }
            }, {
                homeView.hideLoading()
            })?.addTo(disposableHandler.compositeDisposable)
    }

    private fun processMenu(menu: List<Menu>) {
        val ftPosVirtualWhiteList = featureTogglePreference.getFeatureTogle(POS_VIRTUAL_WHITE_LIST)
        val ftPosVirtualButtonHome =
            featureTogglePreference.getFeatureTogle(POS_VIRTUAL_BUTTON_HOME)
        val posVirtualWhiteList = userPreferences.isPosVirtualWhiteList

        var filteredMenu = when {
            ftPosVirtualWhiteList.not() && posVirtualWhiteList.not() -> removeTapOnPhoneOption(menu)
            posVirtualWhiteList.not() && ftPosVirtualWhiteList -> removePosVirtualOption(menu)
            else -> removeTapOnPhoneOption(menu)
        }

        if (ftPosVirtualButtonHome.not()) {
            filteredMenu = removePosVirtualOption(filteredMenu)
        }

        menuView.showMenu(filteredMenu)
    }

    private fun removePosVirtualOption(menu: List<Menu>) =
        menu.filter { it.code != APP_ANDROID_POS_VIRTUAL }

    private fun removeTapOnPhoneOption(menu: List<Menu>) =
        menu.filter { it.code != APP_ANDROID_TAP_PHONE }

    override fun getMerchantMFAStatus() {
        if (featureTogglePreference.isActivate(MFA_EC_STATUS_VALIDACAO)) {
            homeView.showLoadingMerchantStatusChallengeMFA()
            mfaRepository.checkEligibility(object :
                APICallbackDefault<MfaEligibilityResponse, String> {
                override fun onError(error: ErrorMessage) {
                    hideLoadingMFA()
                }

                override fun onSuccess(response: MfaEligibilityResponse) {
                    val statusTrace = EnrollmentStatus.fromString(
                        response.statusTrace?.uppercase(
                            Locale.getDefault()
                        ) ?: EMPTY
                    )

                    if (response.status == EnrollmentStatus.ACTIVE.status && statusTrace == EnrollmentStatus.NOT_MIGRATED) {
                        mfaRepository.postBankEnrollment(
                            MfaAccount(),
                            object : APICallbackDefault<BankEnrollmentResponse, String> {
                                override fun onError(error: ErrorMessage) {
                                    hideLoadingMFA()
                                    error.message.logFirebaseCrashlytics()
                                    getUserInformation(response)
                                }

                                override fun onSuccess(bankEnrollmentResponse: BankEnrollmentResponse) {
                                    hideLoadingMFA()
                                    getUserInformation(response)
                                }
                            })
                    } else {
                        hideLoadingMFA()
                    }
                }
            })
        }
    }

    private fun hideLoadingMFA() {
        homeView.hideLoadingMerchantStatusChallengeMFA()
    }

    private fun getUserInformation(mfaResponse: MfaEligibilityResponse) {
        disposableHandler.compositeDisposable.add(
            userInformationRepository.getUserInformation(
                accessToken = userPreferences.token,
                cacheAllowed = true
            ).observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    processMFAStatus(mfaResponse, it)
                }, {
                    hideLoadingMFA()
                })
        )
    }

    private fun processMFAStatus(response: MfaEligibilityResponse, meResponse: MeResponse) {
        meResponse.digitalId?.let { digital ->
            if (digital.mandatory == true)
                homeView.hideMerchantStatusChallengeMFA()
            else
                withoutP2(response)
        }
        hideLoadingMFA()
    }

    private fun withoutP2(response: MfaEligibilityResponse) {
        val status = response.status
        response.type?.let { type ->
            processMFATypeUserWithoutP2(type, status, response.statusTrace)
        }.run {
            processMFAStatusUserWithoutP2(status)
        }
    }

    private fun processMFATypeUserWithoutP2(type: String, status: String?, statusTrace: String?) {
        when (type) {
            EnrollmentType.CHALLENGE.name -> userWithoutP2MFAChallenge(status, statusTrace)
            EnrollmentType.ENROLLMENT.name -> processMFAErrorPennyDrop(status, statusTrace)
            else -> homeView.hideMerchantStatusChallengeMFA()
        }
    }

    private fun userWithoutP2MFAChallenge(status: String?, statusTrace: String?) {
        when (status) {
            MerchantStatusMFA.NOT_ACTIVE.name, MerchantStatusMFA.EXPIRED.name,
            MerchantStatusMFA.WAITING_ACTIVATION.name -> {
                statusTrace?.let {
                    processMFAErrorPennyDrop(status, statusTrace)
                } ?: homeView.showMerchantStatusChallengeMFA(status)
            }

            else -> homeView.hideMerchantStatusChallengeMFA()
        }
    }

    private fun processMFAErrorPennyDrop(status: String?, statusTrace: String?) {
        if (status == MerchantStatusMFA.WAITING_ACTIVATION.name &&
            statusTrace == MerchantStatusMFA.ERROR_PENNY_DROP.name
        )
            homeView.showMerchantStatusErroPennyDropAndNotEligibleChallengeMFA(
                R.string.mfa_error_penny_drop_status_home_label
            )
    }

    private fun processMFAStatusUserWithoutP2(status: String?) {
        when (status) {
            MerchantStatusMFA.PENDING.name -> homeView.showMerchantStatusPendingChallengeMFA()
            MerchantStatusMFA.NOT_ELIGIBLE.name -> {
                homeView.showMerchantStatusErroPennyDropAndNotEligibleChallengeMFA(
                    R.string.mfa_not_eligible_status_home_label
                )
            }

            else -> homeView.hideMerchantStatusChallengeMFA()
        }
    }

    fun verifyOnboardingCardStatus() {
        updateStatusAndCallIfSucceeded {
            val status = IDOnboardingFlowHandler.onboardingHomeCardStatus
            homeView.showIdOnboardingHomeStatusCard(status)
        }
    }

    private fun updateStatusAndCallIfSucceeded(callback: () -> Unit) {
        idRepository.getIdOnboardingStatus()
            .configureIoAndMainThread()
            .subscribe({ idStatus ->
                IDOnboardingFlowHandler.userStatus.onboardingStatus = idStatus
                callback.invoke()
            }, {
                it.printStackTrace()
                homeView.showError(ErrorMessage.fromThrowable(it))
            }).addTo(disposableHandler.compositeDisposable)
    }

    override fun checkFeatureToggleInteractBanner() {
        if (featureTogglePreference.isActivate(FeatureTogglePreference.INTERACT_BANNERS)) {
            homeView.showInteractBanner()
        }
    }

    override fun checkProfileType() {
        homeView.showLockedProfileScreen(getUserObj())
    }

    override fun getUserObj(): UserObj? {
        return menuPreference.getUserObj()
    }
}