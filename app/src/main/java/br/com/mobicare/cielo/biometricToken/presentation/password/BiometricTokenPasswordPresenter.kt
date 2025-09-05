package br.com.mobicare.cielo.biometricToken.presentation.password

import br.com.mobicare.cielo.biometricToken.data.model.request.BiometricResetPasswordRequest
import br.com.mobicare.cielo.biometricToken.domain.BiometricTokenRepository
import br.com.mobicare.cielo.commons.constants.HTTP_UNKNOWN
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.forgotMyPassword.data.utils.GENERIC_ERROR_MESSAGE
import br.com.mobicare.cielo.forgotMyPassword.data.utils.GENERIC_ERROR_TITLE
import br.com.mobicare.cielo.forgotMyPassword.data.utils.INVALID_PASSWORD_TITLE
import br.com.mobicare.cielo.forgotMyPassword.data.utils.PIECE_OF_CPF
import br.com.mobicare.cielo.forgotMyPassword.data.utils.PIECE_OF_CPF_MESSAGE
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class BiometricTokenPasswordPresenter(
    private val view: BiometricTokenPasswordContract.View,
    private val repository: BiometricTokenRepository,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : BiometricTokenPasswordContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun resetPassword(userName: String, faceIdToken: String, password: String) {
        disposable.add(
            repository.putResetPassword(
                userName,
                faceIdToken,
                BiometricResetPasswordRequest(password, password)
            )
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    if (it.isSuccessful) {
                        view.changePasswordSuccess()
                    } else {
                        val error = APIUtils.convertToErro(it)
                        view.changePasswordError(handleErrorMessage(error))
                    }
                }, {
                    val error = APIUtils.convertToErro(it)
                    view.changePasswordError(handleErrorMessage(error))
                })
        )
    }

    private fun handleErrorMessage(errorMessage: ErrorMessage): ErrorMessage {
        when (errorMessage.httpStatus) {
            HTTP_UNKNOWN -> {
                val errorDetails = errorMessage.listErrorServer.first()

                errorMessage.title = INVALID_PASSWORD_TITLE
                if (errorDetails.errorCode == PIECE_OF_CPF) errorMessage.message = PIECE_OF_CPF_MESSAGE
            }
            else -> {
                errorMessage.title = GENERIC_ERROR_TITLE
                errorMessage.message = GENERIC_ERROR_MESSAGE
            }
        }

        return errorMessage
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }

}