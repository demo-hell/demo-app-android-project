package br.com.mobicare.cielo.pix.ui.keys.myKeys.register.validation

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.getKey
import br.com.mobicare.cielo.pix.api.claim.PixClaimRepositoryContract
import br.com.mobicare.cielo.pix.api.keys.PixKeysRepositoryContract
import br.com.mobicare.cielo.pix.domain.RevokeClaimsRequest
import br.com.mobicare.cielo.pix.domain.ValidateCode
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import br.com.mobicare.cielo.pix.enums.PixRevokeClaimsEnum
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixValidationCodePresenter(
    private val view: PixValidationCodeContract.View,
    private val repository: PixKeysRepositoryContract,
    private val claimRepository: PixClaimRepositoryContract,
    private val userPreferences: UserPreferences,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixValidationCodeContract.Presenter {

    private var disposable = CompositeDisposable()
    private var verificationCode: String? = null

    override fun getUsername(): String = userPreferences.userName

    override fun onSendValidationCode(key: String?, type: PixKeyTypeEnum?, isClaimFlow: Boolean) {
        key?.let { itKey ->
            val myKey = if (isClaimFlow) itKey else
                getKey(itKey, type?.name, isClaimFlow.not())
            disposable.add(
                repository.requestValidateCode(
                    ValidateCode(
                        myKey, type?.name
                    )
                )
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .doOnSubscribe {
                        view.showLoading()
                    }
                    .subscribe({
                        view.hideLoading()
                        view.onSuccessSendCode()
                    }, { error ->
                        view.hideLoading()
                        view.showError(APIUtils.convertToErro(error))
                    })
            )
        } ?: run {
            view.showError()
        }
    }

    override fun onRevokeClaim(
        otp: String,
        claimId: String?,
        code: String?
    ) {
        setVerificationCode(code)
        disposable.add(
            claimRepository.revokeClaims(
                otp,
                RevokeClaimsRequest(
                    claimId = claimId,
                    isClaimer = false,
                    reason = PixRevokeClaimsEnum.FRAUD.name,
                    verificationCode = verificationCode
                )
            )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    view.onSuccessClaim {
                        view.onSuccessRevokeClaim()
                    }
                }, { error ->
                    view.onShowErrorClaim(APIUtils.convertToErro(error))
                })
        )
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