package br.com.mobicare.cielo.commons.ui.widget

import android.os.Handler
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.mfa.FluxoNavegacaoMfaActivity
import br.com.mobicare.cielo.mfa.router.MfaRouterContract
import br.com.mobicare.cielo.mfa.router.MfaRouterPresenter
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf


open class MfaBaseLoggedActivity : BaseLoggedActivity(), MfaRouterContract.View {

    protected val presenterMfa: MfaRouterPresenter by inject {
        parametersOf(this)
    }

    override fun showMerchantOnboard(status: String?) {
        startActivity<FluxoNavegacaoMfaActivity>()
        delayHideLoading()
    }

    override fun showOnboarding() {
        startActivity<FluxoNavegacaoMfaActivity>()
        delayHideLoading()
    }

    override fun callPutValuesValidate() {
        startActivity<FluxoNavegacaoMfaActivity>()
        delayHideLoading()
    }

    override fun callBlockedForAttempt() {
        startActivity<FluxoNavegacaoMfaActivity>()
        delayHideLoading()
    }

    override fun callTokenReconfiguration() {
        startActivity<FluxoNavegacaoMfaActivity>()
        delayHideLoading()
    }

    fun delayHideLoading() {
        Handler().postDelayed({
            this.showLoading(false)
        }, 1000)
    }
}