package br.com.mobicare.cielo.commons.ui

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.mfa.FluxoNavegacaoMfaActivity
import br.com.mobicare.cielo.mfa.router.MfaRouterContract
import br.com.mobicare.cielo.mfa.router.MfaRouterPresenter
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

open class MfaBaseFragment : BaseFragment(), MfaRouterContract.View {

    protected val presenterMfa: MfaRouterPresenter by inject {
        parametersOf(this)
    }

    override fun showTokenGenerator() {
    }

    override fun showNotEligible() {
    }

    override fun showMFAStatusPending() {
    }

    override fun showMFAStatusErrorPennyDrop() {
    }

    override fun showMerchantOnboard(status: String?) {
        requireActivity().startActivity<FluxoNavegacaoMfaActivity>()
    }

    override fun showOnboarding() {
        requireActivity().startActivity<FluxoNavegacaoMfaActivity>()
    }

    override fun callPutValuesValidate() {
        requireActivity().startActivity<FluxoNavegacaoMfaActivity>()
    }

    override fun callBlockedForAttempt() {
        requireActivity().startActivity<FluxoNavegacaoMfaActivity>()
    }

    override fun callTokenReconfiguration() {
        requireActivity().startActivity<FluxoNavegacaoMfaActivity>()
    }

    override fun showLoading(isShow: Boolean) {
        //not implements
    }

    override fun showError(error: ErrorMessage) {
    }
}