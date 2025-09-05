package br.com.mobicare.cielo.commons.router

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.AccessManagerNavigationActivity
import br.com.mobicare.cielo.arv.presentation.ArvNavigationFlowActivity
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.machine.RequestMachineFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.validationServiceSupply.ValidationServiceSupplyFragment
import br.com.mobicare.cielo.balcaoRecebiveis.AuthorizationActivity
import br.com.mobicare.cielo.cancelSale.presentation.CancelSaleFlowActivity
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments.CentralAjudaLogadoFragment
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments.NewTechnicalSupportFragment
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments.NewTechnicalSupportFragment.Companion.TECHNICAL_SUPPORT_SERVICE
import br.com.mobicare.cielo.chargeback.presentation.ChargebackNavigationFlowActivity
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.debitoEmConta.DebitoEmContaAuthorizationActivity
import br.com.mobicare.cielo.dirf.DirfActivity
import br.com.mobicare.cielo.elopat.EloPatFragment
import br.com.mobicare.cielo.eventTracking.presentation.EventTrackingNavigationFlowActivity
import br.com.mobicare.cielo.home.presentation.minhasVendas.ui.activity.MinhasVendasCanceladasActivity
import br.com.mobicare.cielo.lighthouse.ui.activities.LightHouseActivity
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.MeuCadastroFragmentAtualNovo
import br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment.MyCreditCardsFragment
import br.com.mobicare.cielo.mfa.FluxoNavegacaoMfaActivity
import br.com.mobicare.cielo.newRecebaRapido.presentation.ReceiveAutomaticActivity
import br.com.mobicare.cielo.openFinance.presentation.manager.OpenFinanceFlowActivity
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.FluxoNavegacaoSuperlinkActivity
import br.com.mobicare.cielo.pix.ui.menu.SubMenuAuthorizationFragment
import br.com.mobicare.cielo.pixMVVM.presentation.router.PixRouterNavigationFlowActivity
import br.com.mobicare.cielo.posVirtual.presentation.PosVirtualNavigationFlowActivity
import br.com.mobicare.cielo.recebaMais.presentation.ui.fragment.UserLoanFragment
import br.com.mobicare.cielo.security.presentation.ui.activity.SecurityActivity
import br.com.mobicare.cielo.simulator.SimulatorNavigationFlowActivity
import br.com.mobicare.cielo.sobreApp.presentation.ui.fragment.SobreAppFragment
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import br.com.mobicare.cielo.suporteTecnico.presentation.TechnicalSupportFlowActivity
import br.com.mobicare.cielo.tapOnPhone.presentation.TapOnPhoneNavigationFlowActivity
import br.com.mobicare.cielo.taxaPlanos.FeeAndPlansMainFragment
import br.com.mobicare.cielo.webView.presentation.WebViewContainerActivity
import br.com.mobicare.cielo.webView.utils.FLOW_NAME_PARAM
import br.com.mobicare.cielo.webView.utils.URL_PARAM
import com.google.firebase.crashlytics.FirebaseCrashlytics

const val APP_ANDROID_MENU = "APP_ANDROID_MENU"

class Router {

    companion object {
        const val APP_ANDROID_PAYMENT_LINK = "APP_ANDROID_PAYMENT_LINK"
        const val APP_ANDROID_RATES = "APP_ANDROID_RATES"
        const val APP_ANDROID_ACCESS_MANAGEMENT = "APP_ANDROID_ACCESS_MANAGER"
        const val APP_ANDROID_ACCOUNT = "APP_ANDROID_ACCOUNT"
        const val APP_ANDROID_HELP_CENTER = "APP_ANDROID_HELP_CENTER"
        const val APP_ANDROID_MY_ACCOUNT = "APP_ANDROID_MY_ACCOUNT"
        const val APP_ANDROID_SELL = "APP_ANDROID_SELL"
        const val APP_ANDROID_ABOUT = "APP_ANDROID_ABOUT"
        const val APP_ANDROID_RESEARCH = "APP_ANDROID_RESEARCH"
        const val APP_ANDROID_CIELO_MOBILE = "APP_ANDROID_CIELO_MOBILE"
        const val APP_ANDROID_EXIT = "APP_ANDROID_EXIT"
        const val APP_ANDROID_ARV = "APP_ANDROID_ARV"
        const val APP_ANDROID_SUPPLIES = "APP_ANDROID_SUPPLIES"
        const val APP_ANDROID_HELP_DESK = "APP_ANDROID_HELP_DESK"
        const val APP_ANDROID_RECEBA_RAPIDO = "APP_ANDROID_RECEBA_RAPIDO"
        const val APP_ANDROID_NEW_TERMINAL = "APP_ANDROID_NEW_TERMINAL"
        const val APP_ANDROID_FAROL = "APP_ANDROID_FAROL"
        const val APP_ANDROID_RECEBA_MAIS = "APP_ANDROID_RECEBA_MAIS"
        const val APP_ANDROID_REFUNDS = "APP_ANDROID_REFUNDS"
        const val APP_ANDROID_MFA = "APP_ANDROID_MFA"
        const val APP_ANDROID_MACHINE_TRACKING = "APP_ANDROID_MACHINE_TRACKING"
        const val APP_ANDROID_AUTHORIZATION = "APP_ANDROID_AUTHORIZATION"
        const val APP_ANDROID_RECEIVABLES = "APP_ANDROID_RECEIVABLES"
        const val APP_ANDROID_ACCOUNT_DEBIT = "APP_ANDROID_ACCOUNT_DEBIT"
        const val APP_ANDROID_PIX = "APP_ANDROID_PIX"
        const val APP_ANDROID_DIRF = "APP_ANDROID_DIRF"
        const val APP_ANDROID_SECURITY = "APP_ANDROID_SECURITY"
        const val APP_ANDROID_SALES_SIMULATOR = "APP_ANDROID_SALES_SIMULATOR"
        const val APP_ANDROID_CHARGEBACK = "APP_ANDROID_CHARGEBACK"
        const val APP_ANDROID_TAP_PHONE = "APP_ANDROID_TAP_PHONE"
        const val APP_ANDROID_RECEIVE_AUTOMATIC = "APP_ANDROID_RECEIVE_AUTOMATIC"
        const val APP_ANDROID_POS_VIRTUAL = "APP_ANDROID_POS_VIRTUAL"
        const val APP_ANDROID_OPENFINANCE = "APP_ANDROID_OPENFINANCE"
        const val APP_ANDROID_PAT = "APP_ANDROID_PAT"
        const val APP_ANDROID_WEBAPP = "WEBAPP"
        const val APP_CANCEL_SALE = "APP_CANCEL_SALE"

        val actions: Map<String, RouterAction> = hashMapOf(
            APP_ANDROID_PAYMENT_LINK to ActivityRouterAction(FluxoNavegacaoSuperlinkActivity::class.java),
            APP_ANDROID_ACCESS_MANAGEMENT to ActivityRouterAction(AccessManagerNavigationActivity::class.java),
            APP_ANDROID_RATES to ActivityFragmentRouterAction(FeeAndPlansMainFragment::class.java.canonicalName),
            APP_ANDROID_HELP_CENTER to ActivityFragmentRouterAction(CentralAjudaLogadoFragment::class.java.canonicalName),
            APP_ANDROID_ACCOUNT to ActivityFragmentRouterAction(MyCreditCardsFragment::class.java.canonicalName),
            APP_ANDROID_MY_ACCOUNT to ActivityFragmentRouterAction(
                MeuCadastroFragmentAtualNovo::class.java.canonicalName,
                null,
                true
            ),
            APP_ANDROID_SELL to ActivityFragmentRouterAction(RouterActionsFragment::class.java.canonicalName),
            APP_ANDROID_ABOUT to ActivityFragmentRouterAction(SobreAppFragment::class.java.canonicalName),
            APP_ANDROID_RESEARCH to EmailRouterAction(),
            APP_ANDROID_CIELO_MOBILE to GooglePlayRouterAction(),
            APP_ANDROID_EXIT to ExitAppRouterAction(),
            APP_ANDROID_ARV to ActivityRouterAction(ArvNavigationFlowActivity::class.java),
            APP_ANDROID_SUPPLIES to ActivityFragmentRouterAction(ValidationServiceSupplyFragment::class.java.canonicalName),
            APP_ANDROID_HELP_DESK to ActivityRouterAction(TechnicalSupportFlowActivity::class.java),
            APP_ANDROID_NEW_TERMINAL to ActivityFragmentRouterAction(RequestMachineFragment::class.java.canonicalName),
            APP_ANDROID_FAROL to ActivityRouterAction(LightHouseActivity::class.java),
            APP_ANDROID_RECEBA_MAIS to ActivityFragmentRouterAction(UserLoanFragment::class.java.canonicalName),
            APP_ANDROID_REFUNDS to ActivityRouterAction(MinhasVendasCanceladasActivity::class.java),
            APP_ANDROID_MFA to ActivityRouterAction(FluxoNavegacaoMfaActivity::class.java),
            APP_ANDROID_MACHINE_TRACKING to ActivityRouterAction(EventTrackingNavigationFlowActivity::class.java),
            APP_ANDROID_AUTHORIZATION to ActivityFragmentRouterAction(SubMenuAuthorizationFragment::class.java.canonicalName),
            APP_ANDROID_RECEIVABLES to ActivityRouterAction(AuthorizationActivity::class.java),
            APP_ANDROID_PIX to ActivityRouterAction(PixRouterNavigationFlowActivity::class.java),
            APP_ANDROID_DIRF to ActivityRouterAction(DirfActivity::class.java),
            APP_ANDROID_ACCOUNT_DEBIT to ActivityRouterAction(DebitoEmContaAuthorizationActivity::class.java),
            APP_ANDROID_PAT to
                ActivityFragmentRouterAction(
                    EloPatFragment::class.java.canonicalName,
                ),
            APP_ANDROID_SECURITY to ActivityRouterAction(SecurityActivity::class.java),
            APP_ANDROID_SALES_SIMULATOR to ActivityRouterAction(SimulatorNavigationFlowActivity::class.java),
            APP_ANDROID_CHARGEBACK to ActivityRouterAction(ChargebackNavigationFlowActivity::class.java),
            APP_ANDROID_TAP_PHONE to ActivityRouterAction(TapOnPhoneNavigationFlowActivity::class.java),
            APP_ANDROID_RECEIVE_AUTOMATIC to ActivityRouterAction(ReceiveAutomaticActivity::class.java),
            APP_ANDROID_POS_VIRTUAL to ActivityRouterAction(PosVirtualNavigationFlowActivity::class.java),
            APP_ANDROID_OPENFINANCE to ActivityRouterAction(OpenFinanceFlowActivity::class.java),
            APP_ANDROID_WEBAPP to ActivityRouterAction(WebViewContainerActivity::class.java),
            APP_CANCEL_SALE to ActivityRouterAction(CancelSaleFlowActivity::class.java)
        )

        fun navigateTo(
            context: Context,
            route: Menu,
            actionListener: OnRouterActionListener? = null,
            params: Bundle? = Bundle()
        ) {
            val target = route.menuTarget

            if (target.type == APP_ANDROID_WEBAPP) {
                route.code = APP_ANDROID_WEBAPP

                params?.apply {
                    putString(FLOW_NAME_PARAM, route.name)
                    putString(URL_PARAM, target.url)
                }
            }

            val routerAction = actions[route.code]

            if (routerAction is ActivityFragmentRouterAction && params != null) {
                (routerAction as ActivityFragmentRouterAction?)?.bundle = params
            }

            routerAction?.execute(context, route, params) ?: actionListener?.actionNotFound(route)
        }
    }

    interface OnRouterActionListener {
        fun actionNotFound(action: Menu)
    }
}

interface RouterAction {
    fun execute(context: Context, route: Menu, bundle: Bundle? = null)
}

class ExitAppRouterAction : RouterAction {
    override fun execute(context: Context, route: Menu, bundle: Bundle?) {
        LocalBroadcastManager
            .getInstance(context)
            .sendBroadcast(Intent(MainBottomNavigationActivity.HOME_LOGOUT_ACTION))
    }
}

class GooglePlayRouterAction : RouterAction {
    override fun execute(context: Context, route: Menu, bundle: Bundle?) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(route.menuTarget.url)))
        } catch (ex: ActivityNotFoundException) {
            ex.message?.let { FirebaseCrashlytics.getInstance().log(it) }
        }
    }
}

class EmailRouterAction : RouterAction {
    @SuppressLint("WrongConstant")
    override fun execute(context: Context, route: Menu, bundle: Bundle?) {
        if (route.menuTarget.external) {
            if (route.menuTarget.type == "MAIL") {
                val config = ConfigurationPreference.instance
                val email = config
                    .getConfigurationValue(
                        ConfigurationDef.EMAIL_DE_SUA_OPINIAO,
                        "aplicativo@cielo.com.br"
                    )
                val isAuthenticated = !UserPreferences.getInstance().token.isNullOrEmpty()

                Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.fromParts(MAIL_TO, email, null)
                    if (isAuthenticated) {
                        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.mail_titulo_autenticado, UserPreferences.getInstance().numeroEC))
                    } else {
                        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.mail_titulo))
                    }
                    try {
                        context.startActivity(this)
                    } catch (e: Exception) {
                        Toast.makeText(context, context.getString(R.string.toast_email_app_required), Toast.LENGTH_SHORT).show()
                        FirebaseCrashlytics.getInstance().recordException(e)
                    }
                }
            }
        }
    }

    companion object {
        const val MAIL_TO = "mailto"
    }
}

class ActivityRouterAction(private val clazz: Class<*>) : RouterAction {
    override fun execute(context: Context, route: Menu, bundle: Bundle?) {
        Intent(context, clazz).let { itIntent ->
            var params = bundle
            if (params == null) {
                params = Bundle().apply {
                    putParcelable(APP_ANDROID_MENU, route)
                }
            }
            params.let {
                itIntent.putExtras(it)
            }
            context.startActivity(itIntent)
        }
    }
}

class ActivityFragmentRouterAction(
    private var clazz: String?, var bundle: Bundle? = null,
    private val enableFlagSecure: Boolean = false
) : RouterAction {

    override fun execute(context: Context, route: Menu, bundle: Bundle?) {
        this.clazz?.let {
            Intent(context, RouterFragmentInActivity::class.java).let {
                it.putExtra(TITLE_ROUTER_FRAGMENT, route.name)
                it.putExtra(MENU_ROUTER_FRAGMENT, route)
                it.putExtra(FRAGMENT_TO_ROUTER, clazz)
                it.putExtra(BUNDLE_TO_ROUTER, bundle)
                it.putExtra(ENABLE_FLAG_SECURE, enableFlagSecure)
            }.let {
                context.startActivity(it)
            }
        }
    }
}