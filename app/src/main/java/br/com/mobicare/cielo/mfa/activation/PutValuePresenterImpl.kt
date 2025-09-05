package br.com.mobicare.cielo.mfa.activation

import br.com.mobicare.cielo.commons.constants.MFA_USER_BLOCKED
import br.com.mobicare.cielo.commons.constants.MFA_WRONG_VERIFICATION_CODE
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.mfa.activation.repository.PutValueInteractor
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.text.DecimalFormat

class PutValuePresenterImpl(private val view: PutValueView,
                            private val interactor: PutValueInteractor)
    : PutValuePresenter {

    private var disposable = CompositeDisposable()

    override fun onCreate() {
        view.initExplanationSpannable()
        view.initTextChange()

        if (interactor.hasActiveMfaUser()) {
            view.tokenLostWarning()
        }
    }

    override fun activationCode(value1: String, value2: String) {
        interactor.activationCode(formatActivationCode(value1, value2))
                .configureIoAndMainThread()
                .doOnSubscribe { view.showLoading() }
                .doFinally { view.hideLoading() }
                .subscribe({
                    interactor.saveMfaUserInformation(it)
                    view.onValueSuccess()
                }, {
                    val error = APIUtils.convertToErro(it)
                    when (error.httpStatus) {
                        420 -> {
                            when (error.errorCode) {
                                MFA_WRONG_VERIFICATION_CODE -> view.incorrectValues()
                                MFA_USER_BLOCKED -> view.incorrectValuesThirdAttempt()
                                else -> view.onValueError(error)
                            }
                        }
                        400 -> view.onInvalidRequestError(error)
                        404 -> view.onBusinessError(error)
                        else -> view.onValueError(error)
                    }
                }).addTo(disposable)
    }

    private fun formatActivationCode(value1: String, value2: String): String {

        val decimalFormat = DecimalFormat("#,###.00")

        val firstPart = decimalFormat.format(value1.toDouble()).removeNonNumbers()
        val secondPart = decimalFormat.format(value2.toDouble()).removeNonNumbers()

        return "${firstPart}${secondPart}"
    }


    fun fetchEnrollmentActiveBank() {
        interactor.fetchActiveBank()
            .configureIoAndMainThread()
            .subscribe({
                view.configureActiveBank(it)
            }, {
                view.hideEnrollmentActiveBank()
            }).addTo(disposable)

    }

    override fun onPause() {
        disposable.dispose()
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }
}