package br.com.mobicare.cielo.mfa

import br.com.mobicare.cielo.commons.constants.MFA_DEFAULT_ERROR
import br.com.mobicare.cielo.commons.constants.MFA_DEFAULT_ERROR_MESSAGE
import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.merchant.data.entity.MerchantChallengerActivateRequest
import br.com.mobicare.cielo.mfa.activation.repository.PutValueResponse
import br.com.mobicare.cielo.newLogin.domain.LoginResponse
import io.reactivex.Observable
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_NO_CONTENT
import java.net.HttpURLConnection.HTTP_OK

class MfaRepository(
    private val dataSource: MfaApiDataSource,
    private val mfaUserInformation: MfaUserInformation
) {

    private val disposableHandler = CompositeDisposableHandler()

    fun eligibility() = dataSource.checkMfaEligibility()

    fun checkEligibility(callback: APICallbackDefault<MfaEligibilityResponse, String>) {
        handleMfa(dataSource.checkMfaEligibility(), callback)
    }

    fun checkEnrollment(callback: APICallbackDefault<EnrollmentResponse, String>) {
        handleMfa(dataSource.checkEnrollment(), callback)
    }

    fun resendMfa(
        callback: APICallbackDefault<Response<Void>, String>,
        request: MfaResendRequest?
    ) {
        handleVoid(dataSource.resendMfa(request), callback)
    }

    fun seedChallenge(
        callback: APICallbackDefault<Response<Void>, String>,
        request: MerchantChallengerActivateRequest
    ) {
        handleVoid(dataSource.seedChallenge(request), callback)
    }

    fun seedEnrollment(
        callback: APICallbackDefault<PutValueResponse, String>,
        fingerprint: String?
    ) {
        disposableHandler.compositeDisposable.add(
            dataSource.seedEnrollment(fingerprint)
                .configureIoAndMainThread()
                .doOnSubscribe {
                    callback.onStart()
                }.doFinally {
                    callback.onFinish()
                }.subscribe({
                    callback.onSuccess(it)
                }, { error ->
                    callback.onError(APIUtils.convertToErro(error))
                })
        )
    }

    fun refreshToken(
        callback: APICallbackDefault<LoginResponse, String>,
        accessToken: String?, refreshToken: String?
    ) {
        disposableHandler.compositeDisposable.add(
            dataSource.refreshTokenMfa(accessToken, refreshToken)
                .configureIoAndMainThread()
                .doOnSubscribe {
                    callback.onStart()
                }.doFinally {
                    callback.onFinish()
                }.subscribe({
                    callback.onSuccess(it)
                }, { error ->
                    callback.onError(APIUtils.convertToErro(error))
                })
        )
    }

    private fun handleVoid(
        source: Observable<Response<Void>>,
        callback: APICallbackDefault<Response<Void>, String>
    ) {
        disposableHandler.compositeDisposable.add(
            source
                .configureIoAndMainThread()
                .doOnSubscribe {
                    callback.onStart()
                }.doFinally {
                    callback.onFinish()
                }.subscribe({
                    if (it.code() in HTTP_OK..HTTP_NO_CONTENT)
                        callback.onSuccess(it)
                    else
                        callback.onError(APIUtils.convertToErro(it))
                }, { error ->
                    callback.onError(APIUtils.convertToErro(error))
                })
        )
    }

    private fun <T> handleMfa(source: Observable<T>, callback: APICallbackDefault<T, String>) {

        val enabledMfa = FeatureTogglePreference.instance
            .getFeatureTogle(FeatureTogglePreference.MULTIPLE_FACTOR_AUTHENTICATION)

        if (enabledMfa) {
            onStart()
            disposableHandler.compositeDisposable.add(
                source
                    .configureIoAndMainThread()
                    .subscribe({
                        callback.onSuccess(it)
                    }, { error ->
                        callback.onError(APIUtils.convertToErro(error))
                    })
            )
        } else {
            callback.onError(ErrorMessage().apply {
                errorCode = MFA_DEFAULT_ERROR
                errorMessage = MFA_DEFAULT_ERROR_MESSAGE
            })
        }
    }

    fun getMfaBanks(callback: APICallbackDefault<ArrayList<MfaAccount>, String>) {
        onStart()
        disposableHandler.compositeDisposable.add(
            dataSource.getMfaBanks()
                .configureIoAndMainThread()
                .doOnSubscribe {
                    callback.onStart()
                }.subscribe({
                    callback.onSuccess(it)
                }, { error ->
                    callback.onError(APIUtils.convertToErro(error))
                })
        )
    }

    fun sendMFABankChallenge(
        account: MfaAccount,
        callback: APICallbackDefault<EnrollmentResponse, String>
    ) {
        onStart()
        disposableHandler.compositeDisposable.add(
            dataSource.sendMFABankChallenge(account)
                .configureIoAndMainThread()
                .doOnSubscribe { callback.onStart() }
                .doFinally { callback.onFinish() }
                .subscribe({
                    callback.onSuccess(it)
                }, { error ->
                    callback.onError(APIUtils.convertToErro(error))
                })
        )
    }

    fun postBankEnrollment(
        account: MfaAccount,
        callback: APICallbackDefault<BankEnrollmentResponse, String>
    ) {
        onStart()
        disposableHandler.compositeDisposable.add(
            dataSource.postBankEnrollment(account)
                .configureIoAndMainThread()
                .doOnSubscribe {
                    callback.onStart()
                }
                .doFinally { callback.onFinish() }
                .subscribe({
                    callback.onSuccess(it)
                }, { error ->
                    callback.onError(APIUtils.convertToErro(error))
                })
        )
    }

    fun postEnrollmentActivate(code: String): Observable<PutValueResponse> {
        return dataSource.postEnrollmentActivate(code);
    }

    fun hasValidSeed(): Boolean {
        val key = UserPreferences.getInstance().userInformation?.identity?.cpf
            ?: UserPreferences.getInstance().userInformation?.id
            ?: UserPreferences.getInstance().userInformation?.email

        return mfaUserInformation.getMfaUser(key) != null
    }

    fun onStart() {
        disposableHandler.start()
    }

    fun onDispose() {
        disposableHandler.destroy()
    }
}