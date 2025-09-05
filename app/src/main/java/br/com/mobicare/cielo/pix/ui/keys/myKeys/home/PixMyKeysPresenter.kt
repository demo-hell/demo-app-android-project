package br.com.mobicare.cielo.pix.ui.keys.myKeys.home

import androidx.annotation.VisibleForTesting
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pix.api.claim.PixClaimRepositoryContract
import br.com.mobicare.cielo.pix.api.keys.PixKeysRepositoryContract
import br.com.mobicare.cielo.pix.domain.*
import br.com.mobicare.cielo.pix.enums.PixClaimTypeEnum
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.net.HttpURLConnection

class PixMyKeysPresenter(
    private val view: PixMyKeysContract.View,
    private val repository: PixKeysRepositoryContract,
    private val claimRepository: PixClaimRepositoryContract,
    private val userPreferences: UserPreferences,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixMyKeysContract.Presenter {

    @VisibleForTesting
    var tryAgainTimes = 0

    private var disposable = CompositeDisposable()

    override fun getUsername(): String = userPreferences.userName

    override fun getMyKeys() {
        disposable.add(
            repository.getKeys()
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.showLoading()
                }
                .subscribe({ pixMyKeyResponse ->
                    view.hideLoading()
                    validateKeys(pixMyKeyResponse)
                }, { error ->
                    view.hideLoading()
                    view.showError(APIUtils.convertToErro(error))
                })
        )
    }

    private fun validateKeys(response: PixKeysResponse) {
        val isShowCNPJ = isShowCNPJ(response)
        val keys = response.key?.keys
        val claim = response.claims?.keys

        if (keys.isNullOrEmpty() && claim.isNullOrEmpty())
            view.onNoKeyRegistered()
        else {
            if (keys?.isNotEmpty() == true)
                view.onShowMyKeys(response.key, isShowCNPJ)
            else
                view.onHideMyKeys()

            if (claim?.isNotEmpty() == true)
                view.onShowVerificationKeys(claim)
            else
                view.onHideVerificationKeys()
        }
    }

    private fun isShowCNPJ(response: PixKeysResponse): Boolean {
        response.key?.keys?.forEach {
            if (it.keyType == PixKeyTypeEnum.CNPJ.name || it.keyType == PixKeyTypeEnum.CPF.name)
                return false
        }
        return true
    }

    override fun deleteKey(otp: String, key: String, isStartAnimation: Boolean) {
        disposable.add(
            repository.deleteKey(otp, PixKeyDeleteRequest(key))
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    view.onSuccess {
                        view.onGetMyKeys()
                    }
                }, {
                    val error = APIUtils.convertToErro(it)
                    view.onShowAllErrors(
                        onFirstAction = { view.onErrorDelete(error) }
                    )
                })
        )
    }

    override fun cancelClaim(
        otp: String,
        key: MyKey?,
        isPortabilityOrClaimKey: Boolean,
        isClaimer: Boolean
    ) {
        key?.claimDetail?.claimId?.let {
            disposable.add(
                claimRepository.revokeClaims(
                    otp,
                    RevokeClaimsRequest(claimId = it, isClaimer = isClaimer)
                )
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .subscribe({
                        view.onSuccess {
                            if (isPortabilityOrClaimKey) view.onShowSuccessToKeepKey()
                            else view.onGetMyKeys()
                        }
                    }, { error ->
                        errorCancelClaim(
                            APIUtils.convertToErro(error),
                            isPortabilityOrClaimKey,
                            isClaimer
                        )
                    })
            )
        } ?: run {
            errorCancelClaim(null, isPortabilityOrClaimKey, isClaimer)
        }
    }

    override fun confirmClaim(otp: String, key: MyKey?) {
        key?.claimDetail?.let { itClaimDetail ->
            itClaimDetail.claimId?.let { itClaimId ->
                disposable.add(
                    claimRepository.confirmClaims(otp, ConfirmClaimsRequest(claimId = itClaimId))
                        .observeOn(uiScheduler)
                        .subscribeOn(ioScheduler)
                        .subscribe({
                            view.onSuccess {
                                view.onShowSuccessToReleaseKey()
                            }
                        }, { error ->
                            setupConfirmClaimError(
                                itClaimDetail.claimType,
                                APIUtils.convertToErro(error)
                            )
                        })
                )
            }
        } ?: run {
            errorConfirmClaim()
        }
    }

    @VisibleForTesting
    fun setupConfirmClaimError(type: String?, error: ErrorMessage) {
        if (type == PixClaimTypeEnum.OWNERSHIP.name)
            view.onShowAllErrors {
                view.onErrorCreateClaimOwnership(error)
            }
        else
            view.onShowAllErrors {
                view.onErrorCreateClaimPortability(error)
            }
    }

    private fun errorCancelClaim(
        error: ErrorMessage? = null,
        isPortabilityOrClaimKey: Boolean = false,
        isClaimer: Boolean
    ) {
        view.onShowAllErrors(
            onFirstAction = { view.onErrorDefault(error) }
        )
    }

    private fun errorConfirmClaim(error: ErrorMessage? = null) {
        view.onShowAllErrors(
            onFirstAction = { view.onErrorDefault(error) }
        )
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}