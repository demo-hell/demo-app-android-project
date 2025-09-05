package br.com.mobicare.cielo.commons.secure.presentation.ui.presenter

import androidx.annotation.VisibleForTesting
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.local.MfaUserInformation
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.commons.utils.totp.TotpClock
import br.com.mobicare.cielo.commons.utils.totp.TotpCountdownTask
import br.com.mobicare.cielo.commons.utils.totp.TotpCounter
import br.com.mobicare.cielo.mfa.token.CieloMfaTokenGenerator
import java.util.concurrent.TimeUnit

class OtpPresenter(
    val cieloMfaTokenGenerator: CieloMfaTokenGenerator,
    val view: OtpContract.View,
    val totpClock: TotpClock,
    val totpCounter: TotpCounter,
    val mfaUserInformation: MfaUserInformation,
    val userPreferences: UserPreferences
) : OtpContract.Presenter {

    private val disposableHandler: CompositeDisposableHandler = CompositeDisposableHandler()

    @VisibleForTesting
    var seed = mfaUserInformation.getMfaUser(userPreferences.userName)?.mfaSeed

    private var tOtpCountdownTask: TotpCountdownTask? = null

    override fun onResume() {
        disposableHandler.start()
    }

    override fun onDestroy() {
        disposableHandler.destroy()
        stopTotpCountdownTask()
    }

    override fun showOtpCodeForFirstTime() {
        if (CieloMfaTokenGenerator.seedHasCorrectPattern(seed)) {
            view.updateCountdownAnimation(ONE_DOUBLE)
            updateCodesAndStartTotpCountdownTask()
        } else {
            view.errorOnOtpGeneration(R.string.text_error_token_miss_seed)
        }
    }

    private fun updateCodesAndStartTotpCountdownTask() {
        stopTotpCountdownTask()
        tOtpCountdownTask = TotpCountdownTask(totpCounter, totpClock, 100L)
        tOtpCountdownTask?.setListener(
            object : TotpCountdownTask.Listener {
                override fun onTotpCountdown(millisRemaining: Long) {
                    view.updateCountdownAnimation(millisRemaining.toDouble() / TimeUnit.SECONDS.toMillis(totpCounter.getTimeStep()))
                }

                override fun onTotpCounterValueChanged() {
                    view.updateCountdownAnimation(ONE_DOUBLE)
                }

                override fun onGenerateNewOtpCode(shouldManipulateTime: Boolean) {
                    generateCieloMfaToken(shouldManipulateTime)
                }
            })

        tOtpCountdownTask?.startAndNotifyListener()
    }

    private fun generateCieloMfaToken(shouldManipulateTime: Boolean) {
        cieloMfaTokenGenerator.getOtpCode(seed, shouldManipulateTime)?.let { itOtpCode ->
            view.showOtp(itOtpCode)
            view.updateCountdownAnimation(ONE_DOUBLE)
        } ?: run {
            view.errorOnOtpGeneration(R.string.text_error_token_miss_seed)
        }
    }

    private fun stopTotpCountdownTask() {
        tOtpCountdownTask?.stop()
        tOtpCountdownTask = null
    }
}