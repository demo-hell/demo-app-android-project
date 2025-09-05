package br.com.mobicare.cielo.mfa.validationprevioustoken

import br.com.mobicare.cielo.commons.constants.MFA_INVALID_CREDENTIALS
import br.com.mobicare.cielo.commons.constants.MFA_USER_BLOCKED
import br.com.mobicare.cielo.commons.constants.MFA_WRONG_VERIFICATION_CODE
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.mfa.MfaRepository
import br.com.mobicare.cielo.mfa.activation.repository.PutValueInteractor
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class ValidationPreviousTokenPresenter(
    private val view: ValidationPreviousTokenContract.View,
    private val repository: MfaRepository,
    private val interactor: PutValueInteractor
) : ValidationPreviousTokenContract.Presenter {

    private val disposible = CompositeDisposable()

    override fun putCode(code: String) {
        this.repository.postEnrollmentActivate(code)
            .configureIoAndMainThread()
            .doOnSubscribe { view.showLoading(true) }
            .doFinally { view.showLoading(false) }
            .subscribe({
                interactor.saveMfaUserInformation(it)
                this.view.showSuccess()
            }, {
                val error = APIUtils.convertToErro(it)
                when (error.httpStatus) {
                    420 -> {
                        when (error.errorCode) {
                            MFA_INVALID_CREDENTIALS -> view.showIncorrectValues()
                            MFA_WRONG_VERIFICATION_CODE -> view.showIncorrectValues()
                            MFA_USER_BLOCKED -> view.showUserBlocked()
                        }
                    }
                    400 -> view.onInvalidRequestError(error)
                    404 -> view.onBusinessError(error)
                    else -> view.onValueError(error)
                }
            })
            .addTo(disposible)
    }

}