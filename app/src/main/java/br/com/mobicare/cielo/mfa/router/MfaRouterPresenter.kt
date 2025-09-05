package br.com.mobicare.cielo.mfa.router

import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.constants.NEW_DEVICE_DETECTED
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.enums.EnrollmentType
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.main.UserInformationRepository
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.mfa.BankEnrollmentResponse
import br.com.mobicare.cielo.mfa.EnrollmentResponse
import br.com.mobicare.cielo.mfa.MfaAccount
import br.com.mobicare.cielo.mfa.MfaEligibilityResponse
import br.com.mobicare.cielo.mfa.MfaRepository
import br.com.mobicare.cielo.mfa.MfaResendRequest
import br.com.mobicare.cielo.mfa.commons.EnrollmentStatus
import br.com.mobicare.cielo.mfa.commons.MerchantStatusMFA
import br.com.mobicare.cielo.mfa.token.mapper.CieloMfaTokenMapper.toEnrollmentResponse
import br.com.mobicare.cielo.pix.constants.EMPTY
import io.reactivex.Scheduler
import retrofit2.Response
import java.util.Locale

class MfaRouterPresenter(
    private val view: MfaRouterContract.View,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val repository: MfaRepository,
    private val userInformationRepository: UserInformationRepository,
    private val userPreferences: UserPreferences,
    private val featureTogglePreference: FeatureTogglePreference
) : MfaRouterContract.Presenter {

    private val disposableHandler = CompositeDisposableHandler()

    override fun load(isEnrollment: Boolean) {
        view.showLoading()
        if (isEnrollment) {
            repository.checkEnrollment(object :
                APICallbackDefault<EnrollmentResponse, String> {
                override fun onError(error: ErrorMessage) {
                    processError(error)
                }

                override fun onSuccess(response: EnrollmentResponse) {
                    handleEligibilityResponse(response)
                }
            })
        } else {
            repository.checkEligibility(object :
                APICallbackDefault<MfaEligibilityResponse, String> {
                override fun onError(error: ErrorMessage) {
                    processError(error)
                }

                override fun onSuccess(response: MfaEligibilityResponse) {
                    handleEligibilityResponse(response.toEnrollmentResponse())
                }
            })
        }
    }

    private fun handleEligibilityResponse(response: EnrollmentResponse) {
        getUserInformation(
            response = EnrollmentResponse(
                status = response.status,
                type = response.type,
                typeCode = response.typeCode,
                statusCode = response.statusCode,
                statusTrace = response.statusTrace
            )
        )
    }

    override fun checkIsMfaEligible() {
        view.showLoading()
        repository.checkEligibility(object :
            APICallbackDefault<MfaEligibilityResponse, String> {
            override fun onError(error: ErrorMessage) {
                processError(error)
            }

            override fun onSuccess(response: MfaEligibilityResponse) {
                view.showLoading(false)
                val isEligible = response.status != MerchantStatusMFA.NOT_ELIGIBLE.name

                view.isMfaEligible(isEligible)
            }
        })
    }

    private fun processError(error: ErrorMessage = ErrorMessage()) {
        view.showLoading(isShow = false)
        if (error.httpStatus == HTTP_ENHANCE) when (error.errorCode) {
            NEW_DEVICE_DETECTED -> view.showDifferentDevice()
            else -> view.showError(error)
        }
        else view.showError(error)
    }

    private fun getUserInformation(
        response: EnrollmentResponse
    ) {
        disposableHandler
            .compositeDisposable.add(
                userInformationRepository.getUserInformation(accessToken = userPreferences.token)
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .subscribe({
                        checkUserType(response, it)
                    }, { error ->
                        processError(APIUtils.convertToErro(error))
                    })
            )
    }

    private fun checkUserType(
        response: EnrollmentResponse, meResponse: MeResponse
    ) {
        meResponse.digitalId?.let { digital ->
            if (digital.mandatory == true) {
                if (digital.p2Approved == true) {
                    userWithP2(response)
                } else {
                    if (response.status != MerchantStatusMFA.NOT_ELIGIBLE.name) {
                        finishP2()
                    } else {
                        view.showLoading(isShow = false)
                        view.showNotEligible()
                    }
                }

            } else {
                processEnrollmentResponse(response)
            }
        } ?: processError()
    }

    private fun finishP2() {
        view.showLoading(isShow = false)
        view.showUserNeedToFinishP2()
    }

    private fun userWithP2(response: EnrollmentResponse) {
        val statusToken = response.status?.uppercase(Locale.getDefault()) ?: EMPTY
        EnrollmentStatus.fromString(value = statusToken)?.let { status ->
            response.type?.let { type ->
                if (type == EnrollmentType.ENROLLMENT.name) {
                    userWithP2Enrollment(status)
                } else {
                    userWithP2Challenge(status)
                }
            } ?: run {
                checkSeed(withSeedAction = {
                    showToken()
                }, type = EnrollmentType.ENROLLMENT)
            }
        } ?: run {
            processError()
        }
    }

    private fun checkSeed(withSeedAction: () -> Unit, type: EnrollmentType) {
        if (isTokenSeedValid()) withSeedAction.invoke()
        else view.showUserWithP2(type)
    }

    private fun userWithP2Enrollment(status: EnrollmentStatus) {
        userWithP2OStatus(status = status, activeStatusAction = {
            checkSeed(withSeedAction = {
                showToken()
            }, type = EnrollmentType.ENROLLMENT)
        }, notActiveStatusAction = {
            showUserWithP2(EnrollmentType.ENROLLMENT)
        })
    }

    private fun userWithP2Challenge(status: EnrollmentStatus) {
        userWithP2OStatus(status = status, activeStatusAction = {
            checkSeed(withSeedAction = {
                showToken()
            }, type = EnrollmentType.CHALLENGE)
        }, notActiveStatusAction = {
            checkSeed(withSeedAction = {
                showUserWithP2(EnrollmentType.CHALLENGE)
            }, type = EnrollmentType.CHALLENGE)
        })
    }

    private fun userWithP2OStatus(
        status: EnrollmentStatus,
        activeStatusAction: () -> Unit,
        notActiveStatusAction: () -> Unit
    ) {
        when (status) {
            EnrollmentStatus.NOT_ACTIVE, EnrollmentStatus.WAITING_ACTIVATION -> notActiveStatusAction.invoke()
            EnrollmentStatus.ACTIVE -> activeStatusAction.invoke()
            else -> userWithP2OtherStatus(status)
        }
    }

    private fun userWithP2OtherStatus(status: EnrollmentStatus) {
        view.showLoading(isShow = false)
        if (status == EnrollmentStatus.NOT_ELIGIBLE)
            view.showNotEligible()
        else
            view.onErrorConfiguringMfa()
    }

    private fun showToken() {
        view.showLoading(isShow = false)
        view.onShowSuccessConfiguringMfa()
    }

    private fun showUserWithP2(type: EnrollmentType) {
        view.showLoading(isShow = false)
        view.showUserWithP2(type)
    }

    private fun processEnrollmentResponse(response: EnrollmentResponse) {
        val status = response.status?.uppercase(Locale.getDefault()) ?: EMPTY
        when (EnrollmentStatus.fromString(status)) {
            EnrollmentStatus.ACTIVE -> {
                view.showLoading(isShow = false)
                checkActiveUserToken(response)
            }
            EnrollmentStatus.NOT_ELIGIBLE -> {
                view.showLoading(isShow = false)
                view.showNotEligible()
            }
            EnrollmentStatus.PENDING -> {
                view.showLoading(isShow = false)
                view.showMFAStatusPending()
            }
            else -> processEnrollmentType(response)
        }
    }

    private fun isTokenSeedValid(): Boolean {
        return this.repository.hasValidSeed()
    }

    private fun checkActiveUserToken(response: EnrollmentResponse) {
        val statusTrace = EnrollmentStatus.fromString(response.statusTrace?.uppercase(Locale.getDefault()) ?: EMPTY)

        if (statusTrace == EnrollmentStatus.NOT_MIGRATED) {
            repository.postBankEnrollment(
                MfaAccount(),
                object : APICallbackDefault<BankEnrollmentResponse, String> {
                    override fun onError(error: ErrorMessage) {
                       error.message.logFirebaseCrashlytics()
                        followTheMFAFlow()
                    }

                    override fun onSuccess(response: BankEnrollmentResponse) {
                        followTheMFAFlow()
                    }
                })
        } else {
            followTheMFAFlow()
        }
    }

    private fun followTheMFAFlow() {
        when {
            isTokenSeedValid() -> view.showTokenGenerator()
            else -> view.callTokenReconfiguration()
        }
    }

    private fun processEnrollmentType(response: EnrollmentResponse) {
        when (EnrollmentType.fromString(response.type ?: EMPTY)) {
            EnrollmentType.ENROLLMENT -> processEnrollmentForUserToken(response)
            EnrollmentType.CHALLENGE -> processChallenge(response)
            else -> processError(ErrorMessage())
        }
    }

    private fun processChallenge(response: EnrollmentResponse) {
        if (isTokenSeedValid()) {
            processEnrollmentForMerchant(response)
        } else {
            resendPennyDrop()
        }
    }

    private fun processEnrollmentForUserToken(response: EnrollmentResponse) {
        view.showLoading(isShow = false)
        val status = response.status?.uppercase(Locale.getDefault()) ?: EMPTY
        when (EnrollmentStatus.fromString(status)) {
            EnrollmentStatus.NOT_ACTIVE -> view.showOnboarding()
            EnrollmentStatus.WAITING_ACTIVATION -> {
                processStatusTrace(response.statusTrace) {
                    view.callPutValuesValidate()
                }
            }
            EnrollmentStatus.BLOCKED -> view.callBlockedForAttempt()
            else -> view.showError(ErrorMessage())
        }
    }

    private fun processEnrollmentForMerchant(response: EnrollmentResponse) {
        view.showLoading(isShow = false)
        val isFeatureToggleMerchantNotActive =
            featureTogglePreference.isActivate(FeatureTogglePreference.MFA_EC_STATUS_VALIDACAO)
                .not()
        when {
            isFeatureToggleMerchantNotActive -> checkActiveUserToken(response)
            else -> {
                val status = response.status?.uppercase(Locale.getDefault()) ?: EMPTY
                when (EnrollmentStatus.fromString(status)) {
                    EnrollmentStatus.WAITING_ACTIVATION -> {
                        processStatusTrace(response.statusTrace) {
                            this.view.showMerchantOnboard(response.status)
                        }
                    }
                    else -> this.view.showMerchantOnboard(response.status)
                }
            }
        }
    }

    private fun processStatusTrace(statusTrace: String?, action: () -> Unit) {
        if (statusTrace.isNullOrEmpty().not()
            && statusTrace == EnrollmentType.ERROR_PENNY_DROP.name
        ) {
            view.showMFAStatusErrorPennyDrop()
        } else {
            action()
        }
    }

    override fun resendPennyDrop(isShowLoading: Boolean) {
        repository.resendMfa(object : APICallbackDefault<Response<Void>, String> {
            override fun onStart() {
                if (isShowLoading) view.showLoading()
            }

            override fun onError(error: ErrorMessage) {
                view.showLoading(isShow = false)
                view.onErrorResendPennyDrop(error)
            }

            override fun onSuccess(response: Response<Void>) {
                load()
            }

        }, MfaResendRequest())
    }

    override fun onResume() {
        repository.onStart()
        disposableHandler.start()
    }

    override fun onPause() {
        repository.onDispose()
        disposableHandler.destroy()
    }
}