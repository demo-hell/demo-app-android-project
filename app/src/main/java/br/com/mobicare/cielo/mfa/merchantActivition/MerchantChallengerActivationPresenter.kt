package br.com.mobicare.cielo.mfa.merchantActivition

import br.com.mobicare.cielo.commons.constants.MFA_USER_BLOCKED
import br.com.mobicare.cielo.commons.constants.MFA_WRONG_VERIFICATION_CODE
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.commons.utils.removeNonNumbers
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.merchant.domain.repository.MerchantRepository
import io.reactivex.rxkotlin.addTo
import java.text.DecimalFormat

class MerchantChallengerActivationPresenter(
    private val view: MerchantChallengerActivationContract.View,
    private val repository: MerchantRepository
) : MerchantChallengerActivationContract.Presenter {

    private val disposible = CompositeDisposableHandler()

    override fun activationCode(value1: String, value2: String) {
        this.repository.postMerchantChallengerActivate(
            formatActivationCode(value1, value2)
        )
            .configureIoAndMainThread()
            .doOnSubscribe { view.showLoading() }
            .doFinally { view.hideLoading() }
            .subscribe({
                val error = APIUtils.convertToErro(it)
                when (error.httpStatus) {
                    200, 204 -> view.onValueSuccess()
                    400 -> view.onInvalidRequestError(error)
                    404 -> view.onBusinessError(error)
                    420 -> {
                        when (error.errorCode) {
                            MFA_WRONG_VERIFICATION_CODE -> view.incorrectValues()
                            MFA_USER_BLOCKED -> view.incorrectValuesThirdAttempt()
                            else -> view.onValueError(error)
                        }
                    }
                    else -> view.onValueError(error)
                }

            }, {
            })
            .addTo(this.disposible.compositeDisposable)
    }

    override fun onResume() {
        disposible.start()
    }

    override fun onPause() {
        disposible.destroy()
    }

    private fun formatActivationCode(value1: String, value2: String): String {

        val decimalFormat = DecimalFormat("#,###.00")

        val firstPart = decimalFormat.format(value1.toDouble()).removeNonNumbers()
        val secondPart = decimalFormat.format(value2.toDouble()).removeNonNumbers()

        return "${firstPart}${secondPart}"
    }

    fun fetchEnrollmentActiveBank() {

        repository.fetchEnrollmentActiveBank()
                .configureIoAndMainThread()
                .subscribe({
                    view.configureActiveBank(it)
                }, {
                    view.hideEnrollmentActiveBank()
                }).addTo(disposible.compositeDisposable)

    }

}