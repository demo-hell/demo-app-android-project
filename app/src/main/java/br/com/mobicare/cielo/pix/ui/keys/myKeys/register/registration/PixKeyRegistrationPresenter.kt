package br.com.mobicare.cielo.pix.ui.keys.myKeys.register.registration

import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.getKey
import br.com.mobicare.cielo.commons.utils.getKeyType
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.pix.api.claim.PixClaimRepositoryContract
import br.com.mobicare.cielo.pix.api.keys.PixKeysRepositoryContract
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.KEY_NOT_FOUND
import br.com.mobicare.cielo.pix.domain.ClaimsRequest
import br.com.mobicare.cielo.pix.domain.ClaimsResponse
import br.com.mobicare.cielo.pix.domain.CreateKeyRequest
import br.com.mobicare.cielo.pix.domain.ValidateKeyResponse
import br.com.mobicare.cielo.pix.enums.PixClaimTypeEnum
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixKeyRegistrationPresenter(
    private val view: PixKeyRegistrationContract.View,
    private val menuPreference: MenuPreference,
    private val userPreferences: UserPreferences,
    private val repository: PixKeysRepositoryContract,
    private val claimsRepository: PixClaimRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixKeyRegistrationContract.Presenter {

    private var disposable = CompositeDisposable()
    private var verificationCode: String? = null

    override fun getUsername(): String = userPreferences.userName

    override fun getDocument(): String =
        menuPreference.getEstablishment()?.cnpj ?: EMPTY

    override fun onValidateKey(otp: String, key: String?, type: String, code: String?) {
        setVerificationCode(code)
        key?.let { itKey ->
            val keyWithCode = getKey(key, type, isCodeCountry = true)
            disposable.add(
                repository.validateKey(
                    keyWithCode,
                    getKeyType(itKey, type, true)
                )
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .doOnSubscribe {
                        view.showLoading()
                    }
                    .subscribe({
                        view.hideLoading()
                        validateStatusKey(it, otp, keyWithCode, type)
                    }, { error ->
                        view.hideLoading()
                        processError(APIUtils.convertToErro(error), otp, keyWithCode, type)
                    })
            )
        } ?: run { view.onShowErrorValidateKey() }
    }

    private fun processError(
        error: ErrorMessage,
        otp: String,
        key: String?,
        type: String
    ) {
        if (error.httpStatus == HTTP_ENHANCE)
            if (error.errorCode == KEY_NOT_FOUND)
                onRegisterKey(otp, key, type, verificationCode, isStartAnimation = true)
            else
                view.onShowErrorValidateKey(error)
        else view.onShowErrorValidateKey(error)
    }

    private fun validateStatusKey(
        response: ValidateKeyResponse,
        otp: String,
        key: String?,
        type: String
    ) {
        when (response.claimType) {
            PixClaimTypeEnum.POSSESSION_CLAIM.name ->
                view.onShowClaim(response)

            PixClaimTypeEnum.PORTABILITY.name ->
                view.onShowPortability(response)
            else ->
                onRegisterKey(otp, key, type, verificationCode, isStartAnimation = true)
        }
    }

    override fun onRegisterKey(
        otp: String,
        key: String?,
        type: String,
        code: String?,
        isStartAnimation: Boolean
    ) {
        setVerificationCode(code)
        val myKey = key?.ifEmpty { null }
        disposable.add(
            repository.createKey(otp, CreateKeyRequest(myKey, type, verificationCode))
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    view.onSuccess { view.onSuccessRegisterKey() }
                }) {
                    val error = APIUtils.convertToErro(it)
                    view.onShowError { view.onErrorRegisterKey(error) }
                }
        )
    }

    override fun onCreateClaim(
        otp: String,
        key: String?,
        type: String,
        claimType: String,
        code: String?
    ) {
        setVerificationCode(code)
        val clearKey = key?.let { getKey(it, type, isCodeCountry = true) }
        disposable.add(
            claimsRepository.createClaims(
                otp,
                ClaimsRequest(claimType, clearKey, type, verificationCode)
            )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    processSuccessClaim(claimType, it)
                }, { error ->
                    processErrorClaim(claimType, APIUtils.convertToErro(error))
                })
        )
    }

    private fun processErrorClaim(type: String, error: ErrorMessage) {
        if (type == PixClaimTypeEnum.OWNERSHIP.name)
            view.onShowError { view.onErrorCreateClaimOwnership(error) }
        else
            view.onShowError { view.onErrorCreateClaimPortability(error) }
    }

    private fun processSuccessClaim(type: String, response: ClaimsResponse) {
        if (type == PixClaimTypeEnum.OWNERSHIP.name)
            view.onSuccess { view.onSuccessCreateClaimOwnership(response) }
        else
            view.onSuccess { view.onSuccessCreateClaimPortability(response) }
    }

    private fun setVerificationCode(code: String?) {
        verificationCode = if (code?.isEmpty() == true) null
        else code
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}