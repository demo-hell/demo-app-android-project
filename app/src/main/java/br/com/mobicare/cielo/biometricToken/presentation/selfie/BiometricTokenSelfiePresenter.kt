package br.com.mobicare.cielo.biometricToken.presentation.selfie

import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricRegisterDeviceRequest
import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricSelfieRequest
import br.com.mobicare.cielo.biometricToken.domain.BiometricTokenRepository
import br.com.mobicare.cielo.commons.constants.Intent.FILE_TYPE_JPG
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.pix.constants.EMPTY
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class BiometricTokenSelfiePresenter(
    private val view: BiometricTokenSelfieContract.View,
    private val repository: BiometricTokenRepository,
    private val userPreferences: UserPreferences,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : BiometricTokenSelfieContract.Presenter {

    private var disposable = CompositeDisposable()
    private var token: String? = null

    override fun sendBiometricSelfie(base64: String?, encrypted: String?, username: String?) {
        view.onShowSelfieLoading()

        val request = BiometricSelfieRequest(
            imageFileType = FILE_TYPE_JPG.uppercase(),
            photoFileContentBase64 = base64,
            jwtFileContent = encrypted)

        val postBiometricSelfie =  if (username.isNullOrEmpty())
            repository.postBiometricSelfie(request)
        else
            repository.postBiometricSelfie(username, request)

        disposable.add(
            postBiometricSelfie
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({ response ->
                    token = response.token
                    view.onSuccessSelfie()
                }, {
                    view.onSelfieError()
                })
        )
    }

    override fun sendBiometricDevice(fingerprint: String) {
        disposable.add(
            repository.postBiometricRegisterDevice(
                token ?: EMPTY,
                BiometricRegisterDeviceRequest(fingerprint)
            )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    view.onSuccessRegister()
                }, { error ->
                    view.showError(APIUtils.convertToErro(error))
                })
        )
    }

    override fun getStoneAgeToken() {
        disposable.add(
            repository.getStoneAgeToken()
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({ response ->
                    response.token?.let {
                        view.successStoneAgeToken(it)
                    }
                },{
                    view.errorStoneAgeToken()
                })
        )
    }

    fun isForeign(): Boolean {
        return userPreferences.userInformation?.identity?.foreigner ?: false
    }

    fun getToken(): String? {
        return token
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}