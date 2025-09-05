package br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.addAccount.transferAccount

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.meuCadastroDomicilio.MeuCadastroDomicilioRespository
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.AccountTransferRequest
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.FlagTransferRequest
import br.com.mobicare.cielo.pix.constants.EMPTY
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Response

class AddAccountPresenter(
    private val mView: AddAccountContract.View,
    private val mRepository: MeuCadastroDomicilioRespository,
    private val userPreferences: UserPreferences,
) {

    var compositeDisposable = CompositeDisposable()

    companion object {
        private const val INVALID_OTP_CODE = "INVALID_OTP_CODE"
        private const val OTP_TEMPORARILY_BLOCKED = "OTP_TEMPORARILY_BLOCKED"
        private const val UNAUTHORIZED_CHANNEL = "UNAUTHORIZED_CHANNEL"
    }

    fun onCleared() {
        compositeDisposable.clear()
    }

    fun getUserName(): String = userPreferences.userName

    fun transferOfBrands(flagBrands: FlagTransferRequest, otpCode: String) {
            mRepository.transferOfBrands(
                flagBrands,
                userPreferences.token,
                otpCode,
                object : APICallbackDefault<Response<Void>, String> {
                    override fun onStart() {
                        mView.showLoading()
                    }

                    override fun onSuccess(response: Response<Void>) {
                        mView.transferSuccessWithToken()
                    }

                    override fun onError(error: ErrorMessage) {
                        when (error.httpStatus) {
                            420 -> mView.transferInProcess()
                            else -> mView.showError(error)
                        }
                    }
                })
    }

    fun transferAccount(
        addFlag: AccountTransferRequest, otpGenerated: String? = null,
        isUserOnMfaWhitelist: Boolean
    ) {
        val token: String = UserPreferences.getInstance().token ?: EMPTY

        mRepository.transferAccount(
            addFlag,
            token,
            otpGenerated,
            object : APICallbackDefault<Response<Void>, String> {

                override fun onStart() {
                    if (isUserOnMfaWhitelist.not())
                        mView.showLoading()
                }

                override fun onError(error: ErrorMessage) {
                    mView.hideLoading()
                    val errorType = AddAccountErrorType.fromString(error.errorCode) ?: EMPTY
                    when (error.errorCode) {
                        INVALID_OTP_CODE -> mView.errorOnOtpGeneration(error)
                        OTP_TEMPORARILY_BLOCKED -> mView.errorOnOtpTemporaryBlocked(error)
                        UNAUTHORIZED_CHANNEL -> mView.genericBlockError(error)
                        errorType -> {
                            mView.showAddAccountErrorType(error)
                        }
                        else -> {
                            if (isUserOnMfaWhitelist)
                                mView.genericErrorOnOtpGeneration(error)
                            else
                                mView.showError(error)
                        }
                    }
                }

                override fun onSuccess(response: Response<Void>) {
                    mView.hideLoading()
                    if (isUserOnMfaWhitelist.not())
                        mView.transferSuccess()
                    else
                        mView.transferSuccessWithToken()
                }
            })
    }

    fun onStart() {
        if (compositeDisposable.isDisposed) compositeDisposable = CompositeDisposable()
    }
}






















