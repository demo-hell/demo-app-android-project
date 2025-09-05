package br.com.mobicare.cielo.newLogin

import br.com.mobicare.cielo.biometricToken.data.model.response.BiometricSuccessResponse
import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.newLogin.domain.LoginRequest
import br.com.mobicare.cielo.newLogin.domain.LoginResponse
import br.com.mobicare.cielo.newLogin.domain.PostRegisterDeviceRequest
import br.com.mobicare.cielo.newLogin.domain.PostRegisterDeviceResponse
import br.com.mobicare.cielo.newLogin.enums.SessionExpiredEnum
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LoginRepository(
    private val dataSource: LoginDataSource,
    private val mfaUserInformation: MfaUserInformation
) : DisposableDefault {

    private var composite = CompositeDisposable()

    override fun disposable() {
        composite.clear()
    }

    fun login(
        username: String, password: String,
        merchant: String? = null,
        callback: APICallbackDefault<LoginResponse, String>,
        fingerprint: String?,
        sessionExpired: SessionExpiredEnum,
        akamaiSensorData: String?
    ) {
        composite.add(dataSource.login(
            LoginRequest(username, password, merchant, fingerprint),
            sessionExpired.value,
            akamaiSensorData
        ).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { callback.onStart() }
            .subscribe({
                callback.onSuccess(it)
            }, { error ->
                callback.onError(APIUtils.convertToErro(error))
            })
        )
    }

    fun hasValidTokenSeed(userId: String?) = mfaUserInformation.getMfaUser(userId) != null

    fun hasEnabledMfa() = FeatureTogglePreference.instance
        .getFeatureTogle(FeatureTogglePreference.MULTIPLE_FACTOR_AUTHENTICATION)

    fun postRegisterDevice(faceIdToken: String, body: PostRegisterDeviceRequest): Observable<PostRegisterDeviceResponse> =
        dataSource.postRegisterDevice(faceIdToken, body)

    fun refreshToken(accessToken: String?, refreshToken: String?) = dataSource.refreshToken(accessToken, refreshToken)
}