package br.com.mobicare.cielo.mfa.router

import androidx.fragment.app.FragmentActivity
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.enums.EnrollmentType
import br.com.mobicare.cielo.extensions.*
import br.com.mobicare.cielo.idOnboarding.router.IDOnboardingRouter
import br.com.mobicare.cielo.mfa.FluxoNavegacaoMfaActivity
import br.com.mobicare.cielo.mfa.router.userWithP2.MfaTokenConfigurationBottomSheet
import org.jetbrains.anko.startActivityForResult
import org.koin.core.parameter.parametersOf
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MfaRouteHandler(val activity: FragmentActivity) :
    MfaRouterContract.View,
    KoinComponent {

    private val mfaPresenter: MfaRouterPresenter by inject {
        parametersOf(this)
    }

    /** Callback with the eligibility for the **current user** */
    private var isMfaEligibleCallback: ((Boolean) -> Unit)? = null

    /** Callback that says the Token is ready to be used */
    private var tokenAvailableCallback: (() -> Unit)? = null

    /** Callback that tells whether a **loading** is needed or not */
    var showLoadingCallback: ((Boolean) -> Unit)? = null

    /** Callback called when it's needed to open MfaActivity */
    var showMfaActivityCallback: (() -> Unit)? = null

    init {
        canMfaProceedForAction = false
        mfaPresenter.onResume()
    }

    /**
     * Runs the [tokenAvailableCallback] when MFA Token is ready to be used
     */
    fun runWithMfaToken(whenTokenAvailableCallback: (() -> Unit)) {
        tokenAvailableCallback = whenTokenAvailableCallback
        loadMfaRoute()
    }

    /**
     * Tells if the user can use the MFA Token.
     * @param loading optionally tells if the [showLoadingCallback] should be called.
     * @param isMfaEligibleCallback response callback with [isEligible] Boolean
     */
    fun checkIsMfaEligible(
        loading: Boolean = true, isMfaEligibleCallback: ((isEligible: Boolean) -> Unit)
    ) {
        this.isMfaEligibleCallback = isMfaEligibleCallback
        callShowLoadingCallback(loading)
        mfaPresenter.checkIsMfaEligible()
    }

    private fun loadMfaRoute() {
        callShowLoadingCallback()
        mfaPresenter.load(isEnrollment = false)
    }

    private fun genericErrorMfa(error: ErrorMessage?) {
        activity.genericError(
            error = error,
            onFirstAction = {
                loadMfaRoute()
            },
            onSecondAction = {
                activity.moveToHome()
            },
            onSwipeAction = {
                activity.moveToHome()
            },
            isErrorMFA = true
        )
    }

    override fun showTokenGenerator() {
        callShowLoadingCallback(show = false)
        tokenAvailableCallback?.invoke()
    }

    override fun isMfaEligible(isEligible: Boolean) {
        isMfaEligibleCallback?.let {
            isMfaEligibleCallback?.invoke(isEligible)
            isMfaEligibleCallback = null
        } ?: mfaActivityNeeded()
    }

    override fun callTokenReconfiguration() {
        mfaActivityNeeded()
    }

    override fun showOnboarding() {
        mfaActivityNeeded()
    }

    override fun callPutValuesValidate() {
        mfaActivityNeeded()
    }

    override fun callBlockedForAttempt() {
        mfaActivityNeeded()
    }

    override fun showMerchantOnboard(status: String?) {
        mfaActivityNeeded()
    }

    override fun showMFAStatusPending() {
        mfaActivityNeeded()
    }

    override fun showMFAStatusErrorPennyDrop() {
        mfaActivityNeeded()
    }

    override fun showNotEligible() {
        isMfaEligible(isEligible = false)
    }

    override fun showError(error: ErrorMessage) {
        if (isMfaEligibleCallback == null)
            mfaActivityNeeded()
        else isMfaEligibleCallback = null
    }

    private fun mfaActivityNeeded() {
        callShowLoadingCallback(show = false)
        showMfaActivityCallback?.invoke()
        activity.startActivityForResult<FluxoNavegacaoMfaActivity>(
            MFA_ACTIVITY_REQUEST_CODE, Pair(MFA_FROM_ROUTE_HANDLER, true)
        )
    }

    private fun callShowLoadingCallback(show: Boolean = true) {
        showLoadingCallback?.invoke(show)
    }

    override fun bottomSheetConfiguringMfaDismiss() {
        activity.moveToHome()
    }

    override fun showDifferentDevice() {
        MfaTokenConfigurationBottomSheet.onCreate(
            listener = this, isResend = true
        ).show(activity.supportFragmentManager, MFA_FROM_ROUTE_HANDLER)
    }

    override fun showUserWithP2(type: EnrollmentType) {
        MfaTokenConfigurationBottomSheet.onCreate(
            listener = this, type = type.name, isResend = false
        ).show(activity.supportFragmentManager, MFA_FROM_ROUTE_HANDLER)
    }

    override fun onErrorResendPennyDrop(error: ErrorMessage?) {
        activity.genericError(
            error = error,
            onFirstAction = {
                mfaPresenter.resendPennyDrop()
            },
            onSecondAction = {
                activity.moveToHome()
            },
            onSwipeAction = {
                activity.moveToHome()
            },
            isErrorMFA = true
        )
    }

    override fun onShowSuccessConfiguringMfa(isShowMessage: Boolean) {
        callShowLoadingCallback(show = false)
        if (isShowMessage)
            activity.successConfiguringMfa {
                tokenAvailableCallback?.invoke()
            }
        else
            tokenAvailableCallback?.invoke()
    }

    override fun onErrorConfiguringMfa(error: ErrorMessage?) {
        genericErrorMfa(error)
    }

    override fun showUserNeedToFinishP2(error: ErrorMessage?) {
        activity.finishP2(
            onFirstAction = {
                activity.moveToHome()
            },
            onSecondAction = {
                showOnboardingID()
            },
            onSwipeAction = {
                activity.moveToHome()
            },
            error
        )
    }

    private fun showOnboardingID() {
        IDOnboardingRouter(
            activity = activity,
            showLoadingCallback = {},
            hideLoadingCallback = {}
        ).showOnboarding()
    }

    fun onResume() {
        mfaPresenter.onResume()

        if (canMfaProceedForAction) {
            this.tokenAvailableCallback?.let {
                it.invoke()
                canMfaProceedForAction = false
            }
        }
    }

    fun onPause() {
        mfaPresenter.onPause()
    }

    companion object {
        var canMfaProceedForAction = false
        const val MFA_FROM_ROUTE_HANDLER = "mfa_from_route_handler"
        const val MFA_ACTIVITY_REQUEST_CODE = 22222
    }
}