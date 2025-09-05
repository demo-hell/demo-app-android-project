package br.com.mobicare.cielo.main.presentation.presenter

import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.constants.NEW_DEVICE_DETECTED
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences.ONBOARDING.Companion.MFA
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domain.repository.PosVirtualWhiteListRepository
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.enums.EnrollmentType
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POS_VIRTUAL_WHITE_LIST
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleModal
import br.com.mobicare.cielo.lgpd.domain.repository.LgpdRepository
import br.com.mobicare.cielo.login.domain.TokenFCM
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.main.MenuRepository
import br.com.mobicare.cielo.main.UserInformationRepository
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.main.presentation.ui.MainBottomNavigationContract
import br.com.mobicare.cielo.main.presentation.util.PriorityWarningUtil
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.merchant.data.MerchantRepositoryImpl
import br.com.mobicare.cielo.merchant.domain.entity.ACTIVE
import br.com.mobicare.cielo.merchant.domain.entity.PENDING
import br.com.mobicare.cielo.merchant.domain.entity.WAITING
import br.com.mobicare.cielo.mfa.MfaEligibilityResponse
import br.com.mobicare.cielo.mfa.MfaRepository
import br.com.mobicare.cielo.mfa.commons.EnrollmentStatus
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers

const val ERROR_BUSINESS = 420
const val SUCCESS = 204

class MainBottomNavigationPresenter(
    private val mainBottomNavigationView: MainBottomNavigationContract.View,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val userInformationRepository: UserInformationRepository,
    private val tapRepository: PosVirtualWhiteListRepository,
    private val menuRepository: MenuRepository,
    private val mfaRepository: MfaRepository,
    private val lgpdRepository: LgpdRepository,
    private val merchantRepository: MerchantRepositoryImpl,
    private val userPreferences: UserPreferences,
    private val featureTogglePreference: FeatureTogglePreference,
    private val menuPreference: MenuPreference
) :
    MainBottomNavigationContract.Presenter {

    private var isCancelOnboardingCalled: Boolean = false
    private val compositeDisposableHandler = CompositeDisposableHandler()
    private lateinit var userInformationResponse: MeResponse

    override fun getUserInformation(isImpersonate: Boolean) {
        compositeDisposableHandler.compositeDisposable.add(
            userInformationRepository
                .getUserInformation(
                    accessToken = UserPreferences.getInstance().token,
                    cacheAllowed = isImpersonate.not()
                )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    if (isImpersonate)
                        getPosVirtualWhiteList(it)
                    else
                        processUserInfoSuccess(
                            meResponse = it,
                            isImpersonate = false,
                            isPosWhiteList = true
                        )
                }, {
                    if (isImpersonate) {
                        mainBottomNavigationView.onLogout()
                        userPreferences.savePosVirtualWhiteList()
                    }
                })
        )
    }

    private fun processUserInfoSuccess(
        meResponse: MeResponse,
        isImpersonate: Boolean = true,
        isPosWhiteList: Boolean
    ) {
        userInformationResponse = meResponse
        isCancelOnboardingCalled = false

        if (isImpersonate)
            userPreferences.savePosVirtualWhiteList(isPosWhiteList)

        try {
            showWarningModal(meResponse.activeMerchant.id, isImpersonate)
        } catch (undeliverableException: UndeliverableException) {
            undeliverableException.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        } catch (castException: ClassCastException) {
            castException.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        } catch (ex: Exception) {
            ex.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
        }
        mainBottomNavigationView.verifyNeedsOnboarding(
            meResponse,
            isImpersonate
        )
    }

    private fun getPosVirtualWhiteList(meResponse: MeResponse) {
        if (featureTogglePreference.getFeatureTogle(POS_VIRTUAL_WHITE_LIST)) {
            compositeDisposableHandler.compositeDisposable.add(
                tapRepository.getPosVirtualWhiteList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ posWhiteList ->
                        processUserInfoSuccess(
                            meResponse = meResponse,
                            isImpersonate = true,
                            isPosWhiteList = posWhiteList.eligible
                        )
                    }, {
                        processUserInfoSuccess(
                            meResponse = meResponse,
                            isImpersonate = true,
                            isPosWhiteList = false
                        )
                    })
            )
        } else {
            processUserInfoSuccess(
                meResponse = meResponse,
                isImpersonate = true,
                isPosWhiteList = true
            )
        }
    }

    override fun checkDeeplink() {
        val deepLinkModel = userPreferences.deepLinkModel
        val mktExternalDeeplinkUrl = userPreferences.mktExternalDeeplink.orEmpty()
        val deeplinkOpenFinanceIntentId = userPreferences.holderIntentId.orEmpty()
        val deeplinkOpenFinanceRedirectUri = userPreferences.holderRedirectUri.orEmpty()
        val deeplinkRequestIdOPF = userPreferences.requestIdOPF.orEmpty()

        if (deepLinkModel != null) {
            mainBottomNavigationView.startDeeplinkFlow(deepLinkModel)
        } else if (mktExternalDeeplinkUrl.isNotEmpty()) {
            userPreferences.deleteMktExternalDeeplink()
            mainBottomNavigationView.startMktDeeplink(mktExternalDeeplinkUrl)
        }else if (deeplinkOpenFinanceIntentId.isNullOrEmpty()
            .not() && deeplinkOpenFinanceRedirectUri.isNullOrEmpty().not()){
            mainBottomNavigationView.startDeeplinkOpenFinance()
        }else if(deeplinkRequestIdOPF.isNullOrEmpty().not()){
            mainBottomNavigationView.startDeeplinkConclusionShareOPF()
        }
    }

    override fun updateAppMenus(accessToken: String) {
        menuRepository.getMenu(accessToken)
            ?.configureIoAndMainThread()
            ?.subscribe({ menuResponse ->
                if (UserPreferences.getInstance().appMenu == null)
                    UserPreferences.getInstance().saveMenuApp(menuResponse)
            }, {

            })?.addTo(compositeDisposableHandler.compositeDisposable)
    }

    override fun procedeUserInformation() {
        mainBottomNavigationView
            .onUserInformationsResponse(userInformationResponse)
    }

    override fun onResume() {
        compositeDisposableHandler.start()
        mfaRepository.onStart()
    }

    override fun onDestroy() {
        compositeDisposableHandler.destroy()
        mfaRepository.onDispose()
    }

    override fun onPause() {
        mfaRepository.onDispose()
    }

    override fun showCancelOnboard() {
        if (FeatureTogglePreference.instance
                .getFeatureTogle(FeatureTogglePreference.EFETIVAR_CANCELAMENTO)
        ) {
            if (!UserPreferences.getInstance().isCancelStatus &&
                !isCancelOnboardingCalled
            ) {
                isCancelOnboardingCalled = true
                this.mainBottomNavigationView.bannerDebitoContaEligible()
                UserPreferences.getInstance().saveCancelStatus(true)
            }
        }
    }

    override fun checkLgpd() {
        this.lgpdRepository.getEligibility()
            .configureIoAndMainThread()
            .subscribe({
                when (it.eligible) {
                    true -> this.mainBottomNavigationView.showLGPD(it)
                    else -> procedureAfterLgpd()
                }
            }, {
                procedureAfterLgpd()
            })
            .addTo(this.compositeDisposableHandler.compositeDisposable)
    }

    override fun procedureAfterLgpd() {
        updateHeader()
        checkIfMfaTokenIsActivated()
    }

    private fun updateHeader(isAfterModal: Boolean = true) {
        val isNewlyAccreditedToggle = FeatureTogglePreference
            .instance
            .getFeatureTogle(FeatureTogglePreference.TRATAMENTO_RECEM_CREDENCIADO)
        val isNewlyAccredited = UserPreferences.getInstance().newlyAccredited

        if (userInformationResponse.activeMerchant.migrated?.not() == true
            && isNewlyAccreditedToggle
            && isNewlyAccredited.not()
        ) {
            if (isAfterModal)
                mainBottomNavigationView.callOnboardFirstAccess()
        } else
            procedeUserInformation()
    }

    private fun checkIfMfaTokenIsActivated() {
        val isMultipleFactorAuth = featureTogglePreference.isActivate(
            FeatureTogglePreference.MULTIPLE_FACTOR_AUTHENTICATION,
            FeatureTogglePreference.MULTIPLE_FACTOR_AUTHENTICATION_ONBORDING
        )

        if (isMultipleFactorAuth)
            mfaRepository.checkEligibility(object :
                APICallbackDefault<MfaEligibilityResponse, String> {
                override fun onError(error: ErrorMessage) {
                    if (error.httpStatus == HTTP_ENHANCE && error.errorCode == NEW_DEVICE_DETECTED)
                        mainBottomNavigationView.showMfaOnboarding()
                }

                override fun onSuccess(response: MfaEligibilityResponse) {
                    processMFAStatus(response)
                }
            })
    }

    private fun processMFAStatus(response: MfaEligibilityResponse) {
        val type = EnrollmentType.fromString(response.type ?: EMPTY)
        val status = EnrollmentStatus.fromString(response.status ?: EMPTY)

        userInformationResponse.digitalId?.let { digital ->
            if (digital.mandatory == true) {
                if (digital.p2Approved == true)
                    withP2(status)
            } else
                withoutP2(type, status)
        }
    }

    private fun withP2(status: EnrollmentStatus?) {
        val active = status == EnrollmentStatus.ACTIVE && mfaRepository.hasValidSeed().not()
        if (status == EnrollmentStatus.NOT_ACTIVE || status == EnrollmentStatus.WAITING_ACTIVATION || active)
            mainBottomNavigationView.showMfaOnboarding()
    }

    private fun withoutP2(
        type: EnrollmentType?,
        status: EnrollmentStatus?,
    ) {
        val isShowMfa = userPreferences.isToShowOnboarding(MFA)
        if (type == EnrollmentType.ENROLLMENT && status == EnrollmentStatus.NOT_ACTIVE) {
            if (isShowMfa) {
                userPreferences.setShowOnboarding(MFA, false)
                mainBottomNavigationView.showMfaOnboarding()
            }
        }
    }

    override fun sendTokenFCM(merchantId: String) {
        val fcmSent = UserPreferences.getInstance().tokenFcmSent
        val fcmToken: String? = UserPreferences.getInstance().tokenFCM

        fcmToken?.let {
            if (!fcmSent) {
                val tokenFCM = TokenFCM(merchantId, it, it, true)

                compositeDisposableHandler.compositeDisposable.add(
                    userInformationRepository
                        .sendTokenFCM(tokenFCM)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            UserPreferences.getInstance().saveTokenFcmSent(true)
                        }, { err ->
                            FirebaseCrashlytics.getInstance().recordException(err)
                        })
                )
            }
        }
    }

    /**
     * método que verifica se o usuário é elegível
     * para o balcão recebíveis
     * */
    override fun balcaoRecebiveisElegibility() {
        compositeDisposableHandler.compositeDisposable
            .add(
                merchantRepository.getMerchantPermissionsEligible()
                    .configureIoAndMainThread()
                    .subscribe({
                        if (it.eligible == true)
                            this.mainBottomNavigationView.showBannerBalcaoRecebiveis()
                        else
                            this.mainBottomNavigationView.bannerBalcaoRecebiveisNotElegivel()

                    }, {
                        val error = APIUtils.convertToErro(it)
                        this.mainBottomNavigationView.erroUrlEligible(error)
                    })
            )

    }

    override fun balcaoRecebiveisPermissionRegister() {
        compositeDisposableHandler.compositeDisposable
            .add(
                merchantRepository.balcaoRecebiveisPermissionRegister()
                    .configureIoAndMainThread()
                    .subscribe({
                        if (it.optin == true) {
                            this.mainBottomNavigationView.getAuthorizationHistory(it)
                        } else {
                            this.mainBottomNavigationView.getDataPermissionRegister(it)
                        }

                    }, {
                        val error = APIUtils.convertToErro(it)
                        this.mainBottomNavigationView.erroUrlEligible(error)
                    })
            )
    }

    /**
     * método que faz a contratação do balcao recebíveis
     * */
    override fun sendPermisionRegister() {
        var error: ErrorMessage? = null
        compositeDisposableHandler.compositeDisposable
            .add(
                merchantRepository.sendPermisionRegister()
                    .configureIoAndMainThread()
                    .subscribe({
                        error = APIUtils.convertToErro(it)
                        requestResult(error)
                    }, {
                        error = APIUtils.convertToErro(it)
                        requestResult(error)
                    })
            )
    }

    private fun requestResult(error: ErrorMessage?) {
        when (error?.httpStatus) {
            SUCCESS -> this.mainBottomNavigationView.sucessPermissionRegister()
            ERROR_BUSINESS -> this.mainBottomNavigationView.errorPermissionRegister()
            else -> this.mainBottomNavigationView.errorGeneric(error)
        }
    }

    override fun debitoEmContaElegibility() {
        compositeDisposableHandler.compositeDisposable
            .add(
                merchantRepository.getDebitoContaPermissionsEligible()
                    .configureIoAndMainThread()
                    .subscribe({
                        when (it.status) {
                            PENDING -> {
                                this.mainBottomNavigationView.showBannerDebitoEmConta(it)
                            }
                            WAITING -> {
                                this.mainBottomNavigationView.showBannerDebitoEmContaWaiting(it)
                            }
                            ACTIVE -> {
                                this.mainBottomNavigationView.resultSearchDebitoEmContaActive(it)
                            }
                            else -> {
                                this.mainBottomNavigationView.showBannerDebitoEmConta(it)
                            }
                        }
                    }, {
                        val error = APIUtils.convertToErro(it)
                        this.mainBottomNavigationView.errorGeneric(error)
                    })
            )

    }

    override fun sendDebitoContaPermission(optin: String) {
        compositeDisposableHandler.compositeDisposable
            .add(
                merchantRepository.sendDebitoContaPermission(optin)
                    .configureIoAndMainThread()
                    .subscribe({

                        when (it.code()) {
                            in 200..204 -> {
                                this.mainBottomNavigationView.showBannerDebitoEmContaActive()
                            }
                            else -> {
                                val error = APIUtils.convertToErro(it)
                                this.mainBottomNavigationView.erroUrlEligible(error)
                            }
                        }
                    }, {
                        val error = APIUtils.convertToErro(it)
                        this.mainBottomNavigationView.erroUrlEligible(error)
                    })
            )
    }

    private fun showWarningModal(ec: String?, isImpersonate: Boolean) {
        val modal = FeatureTogglePreference.instance.getFeatureToggleModal()
        val isShow = FeatureTogglePreference.instance
            .getFeatureTogle(FeatureTogglePreference.MODAL_DINAMICA)
        val warning = FeatureTogglePreference.instance.getSawWarning()

        modal?.let {
            val hasEc = PriorityWarningUtil.hasEC(warning, ec)
            val hasId = PriorityWarningUtil.hasId(warning, it.id, ec)

            if (isShow && modal.loggedModal) {
                if (modal.stickyModal)
                    showModal(modal, isImpersonate)
                else {
                    if (hasEc.not() || hasId.not())
                        showModal(modal, isImpersonate)
                    else mainBottomNavigationView.onLoadOtherInformation(isImpersonate)
                }
            } else mainBottomNavigationView.onLoadOtherInformation(isImpersonate)
        } ?: run {
            mainBottomNavigationView.onLoadOtherInformation(isImpersonate)
        }
    }

    private fun showModal(modal: FeatureToggleModal, isImpersonate: Boolean) {
        if (isImpersonate.not())
            updateHeader(false)

        mainBottomNavigationView.onShowWarningModal(modal, isImpersonate)
    }
    override fun getUserObj(): UserObj? = menuPreference.getUserObj()
}


