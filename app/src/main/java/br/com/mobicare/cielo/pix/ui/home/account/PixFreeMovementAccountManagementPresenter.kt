package br.com.mobicare.cielo.pix.ui.home.account

import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.pix.api.account.PixAccountRepositoryContract
import br.com.mobicare.cielo.pix.domain.PixProfileRequest
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import java.net.HttpURLConnection
import java.net.HttpURLConnection.HTTP_OK
import java.net.HttpURLConnection.HTTP_PARTIAL

class PixFreeMovementAccountManagementPresenter(
    private val view: PixFreeMovementAccountManagementContract.View,
    private val userPreferences: UserPreferences,
    private val pixAccountRepository: PixAccountRepositoryContract,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : PixFreeMovementAccountManagementContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun getUsername(): String = userPreferences.userName

    override fun getMerchant() {
        disposable.add(
            pixAccountRepository.getMerchant()
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .doOnSubscribe {
                    view.onShowMerchantLoading()
                }
                .doFinally {
                    view.onHideMerchantLoading()
                }
                .subscribe({ response ->
                    view.onSuccessMerchant(response)
                }, { error ->
                    view.onErrorMerchant(APIUtils.convertToErro(error))
                })
        )
    }

    override fun changePixAccount(otp: String) {
        disposable.add(
            pixAccountRepository.updateProfile(
                otp, PixProfileRequest(settlementActive = true)
            )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    if (it.code() in (HTTP_OK..HTTP_PARTIAL).toList()) {
                        view.onSuccessChangePixAccount()
                    } else {
                        view.onErrorChangePixAccount { view.showError() }
                    }
                }, {
                    val error = APIUtils.convertToErro(it)
                    view.onErrorChangePixAccount {
                        if (error.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                                OTP
                            )
                        )
                            view.showError(error)
                    }
                }
                ))
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}