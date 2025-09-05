package br.com.mobicare.cielo.mfa.router.userWithP2

import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.constants.MFA_INVALID_MERCHANT
import br.com.mobicare.cielo.commons.constants.MFA_INVALID_STATUS
import br.com.mobicare.cielo.commons.constants.NEW_DEVICE_DETECTED
import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.merchant.data.entity.MerchantChallengerActivateRequest
import br.com.mobicare.cielo.mfa.*
import br.com.mobicare.cielo.mfa.activation.repository.PutValueInteractor
import br.com.mobicare.cielo.mfa.activation.repository.PutValueResponse
import br.com.mobicare.cielo.newLogin.domain.LoginResponse
import retrofit2.Response

class MfaTokenConfigurationPresenter(
    private val view: MfaTokenConfigurationContract.View,
    private val repository: MfaRepository,
    private val interactor: PutValueInteractor,
    private val userPreferences: UserPreferences,
    private val mfaUserInformation: MfaUserInformation
) : MfaTokenConfigurationContract.Presenter {

    private var retryCallback: (() -> Unit)? = null

    private fun processErrorCreatingTheNewSeed(
        error: ErrorMessage, onErrorAction: () -> Unit, onWaitingActivation: () -> Unit
    ) {
        if (error.httpStatus == HTTP_ENHANCE) when (error.errorCode) {
            NEW_DEVICE_DETECTED -> view.showDifferentDevice()
            MFA_INVALID_STATUS, MFA_INVALID_MERCHANT -> onWaitingActivation.invoke()
            else -> onErrorAction.invoke()
        }
        else onErrorAction.invoke()
    }

    override fun enrollment(fingerprint: String?) {
        retryCallback = { enrollment(fingerprint) }

        repository.postBankEnrollment(MfaAccount(fingerprint = fingerprint),
            object : APICallbackDefault<BankEnrollmentResponse, String> {
                override fun onStart() {
                    view.onConfiguringMfaLoading()
                }

                override fun onError(error: ErrorMessage) {
                    if (error.errorCode != MFA_INVALID_STATUS)
                        view.onConfiguringMfaLoading(isLoading = false)

                    processErrorCreatingTheNewSeed(error = error, onErrorAction = {
                        view.onErrorConfiguringMfa(error)
                    }, onWaitingActivation = {
                        seedEnrollment(fingerprint = fingerprint, isShowLoading = false)
                    })
                }

                override fun onSuccess(response: BankEnrollmentResponse) {
                    seedEnrollment(fingerprint, false)
                }

            })
    }

    override fun seedEnrollment(fingerprint: String?, isShowLoading: Boolean) {
        retryCallback = { seedEnrollment(fingerprint, true) }

        repository.seedEnrollment(object : APICallbackDefault<PutValueResponse, String> {
            override fun onStart() {
                if (isShowLoading) view.onConfiguringMfaLoading()
            }

            override fun onError(error: ErrorMessage) {
                processErrorCreatingTheNewSeed(
                    error = error,
                    onErrorAction = {
                        view.onErrorConfiguringMfa(error)
                    },
                    onWaitingActivation = {
                        view.showUserNeedToFinishP2(error)
                    }
                )
            }

            override fun onSuccess(response: PutValueResponse) {
                interactor.saveMfaUserInformation(response)
                view.onShowSuccessConfiguringMfa()
            }

            override fun onFinish() {
                view.onConfiguringMfaLoading(isLoading = false)
            }
        }, fingerprint)
    }

    override fun challenge(fingerprint: String?) {
        retryCallback = { challenge(fingerprint) }
        if (repository.hasValidSeed()) repository.sendMFABankChallenge(MfaAccount(fingerprint = fingerprint),
            object : APICallbackDefault<EnrollmentResponse, String> {
                override fun onStart() {
                    view.onConfiguringMfaLoading()
                }

                override fun onError(error: ErrorMessage) {
                    if (error.errorCode != MFA_INVALID_STATUS)
                        view.onConfiguringMfaLoading(isLoading = false)
                    processErrorCreatingTheNewSeed(error = error,
                        onErrorAction = {
                            view.onErrorConfiguringMfa(error)
                        },
                        onWaitingActivation = {
                            seedChallenge(fingerprint = fingerprint, isShowLoading = false)
                        }
                    )
                }

                override fun onSuccess(response: EnrollmentResponse) {
                    seedChallenge(fingerprint, false)
                }
            })
        else resendMfa(fingerprint)
    }

    override fun seedChallenge(fingerprint: String?, isShowLoading: Boolean) {
        retryCallback = { seedChallenge(fingerprint, true) }

        repository.seedChallenge(object : APICallbackDefault<Response<Void>, String> {
            override fun onStart() {
                if (isShowLoading) view.onConfiguringMfaLoading()
            }

            override fun onError(error: ErrorMessage) {
                processErrorCreatingTheNewSeed(
                    error = error,
                    onErrorAction = {
                        view.onErrorConfiguringMfa(error)
                    },
                    onWaitingActivation = {
                        view.showUserNeedToFinishP2(error)
                    }
                )
            }

            override fun onSuccess(response: Response<Void>) {
                view.onShowSuccessConfiguringMfa()
            }

            override fun onFinish() {
                view.onConfiguringMfaLoading(isLoading = false)
            }
        }, MerchantChallengerActivateRequest(fingerprint = fingerprint))
    }

    override fun resendMfa(fingerprint: String?) {
        retryCallback = { resendMfa(fingerprint) }

        repository.resendMfa(object : APICallbackDefault<Response<Void>, String> {
            override fun onStart() {
                view.onResendMfaLoading()
            }

            override fun onError(error: ErrorMessage) {
                view.onResendMfaLoading(isLoading = false)
                processErrorCreatingTheNewSeed(
                    error = error,
                    onErrorAction = {
                        view.onErrorResendMfa(error)
                    },
                    onWaitingActivation = {
                        view.showUserNeedToFinishP2(error)
                    }
                )
            }

            override fun onSuccess(response: Response<Void>) {
                refreshToken(isShowLoading = false)
            }

        }, MfaResendRequest(fingerprint = fingerprint))
    }

    private fun refreshToken(isShowLoading: Boolean = true) {
        retryCallback = { refreshToken() }

        repository.refreshToken(
            object : APICallbackDefault<LoginResponse, String> {
                override fun onStart() {
                    if (isShowLoading) view.onResendMfaLoading()
                }

                override fun onError(error: ErrorMessage) {
                    view.onErrorRefreshToken(error)
                }

                override fun onSuccess(response: LoginResponse) {
                    mfaUserInformation.cleanMfaRegisters()
                    userPreferences.saveToken(response.accessToken)
                    userPreferences.saveRefreshToken(response.refreshToken)
                    view.onSuccessResendMfa()
                }

                override fun onFinish() {
                    view.onResendMfaLoading(isLoading = false)
                }
            }, userPreferences.token, userPreferences.refreshToken
        )
    }

    override fun retry() {
        retryCallback?.invoke()
    }

    override fun onResume() {
        repository.onStart()
    }

    override fun onPause() {
        repository.onDispose()
    }
}