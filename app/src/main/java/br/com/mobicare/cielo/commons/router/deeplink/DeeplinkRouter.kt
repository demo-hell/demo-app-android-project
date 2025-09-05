package br.com.mobicare.cielo.commons.router.deeplink

import android.content.Context
import android.content.Intent
import br.com.mobicare.cielo.arv.presentation.ArvNavigationFlowActivity
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfServiceSupply.SelfServiceSupplyFragment
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.router.FRAGMENT_TO_ROUTER
import br.com.mobicare.cielo.commons.router.RouterFragmentInActivity
import br.com.mobicare.cielo.commons.router.TITLE_ROUTER_FRAGMENT
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.ARV
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.CANCELAMENTO_VENDAS
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.CIELO_FAROL
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.CIELO_TAP
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.DIRF
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.MACHINE_TRACKING
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.MEU_CADASTRO
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.MINHAS_VENDAS
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.PIX
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.POS_VIRTUAL
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.PREDICTIVE_BATTERY
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.RECEBA_MAIS
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.RECEBA_RAPIDO
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.RECEBIVEIS
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.SIMULADOR_VENDAS
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.SOLICITACAO_MATERIAIS
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.SUPER_LINK
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.SUPORTE_TECNICO
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkFlowIdEnum.TAXAS_PLANOS
import br.com.mobicare.cielo.deeplink.model.DeepLinkModel
import br.com.mobicare.cielo.deeplink.utils.DeepLinkConstants
import br.com.mobicare.cielo.dirf.DirfActivity
import br.com.mobicare.cielo.eventTracking.presentation.EventTrackingNavigationFlowActivity
import br.com.mobicare.cielo.home.presentation.meusrecebimentonew.MeusRecebimentosHomeActivityNew
import br.com.mobicare.cielo.home.presentation.minhasVendas.ui.activity.MinhasVendasCanceladasActivity
import br.com.mobicare.cielo.home.presentation.minhasVendas.ui.activity.MinhasVendasHomeActivity
import br.com.mobicare.cielo.lighthouse.ui.activities.LightHouseActivity
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.MeuCadastroFragmentAtualNovo
import br.com.mobicare.cielo.newRecebaRapido.presentation.ReceiveAutomaticActivity
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.FluxoNavegacaoSuperlinkActivity
import br.com.mobicare.cielo.pixMVVM.presentation.router.PixRouterNavigationFlowActivity
import br.com.mobicare.cielo.posVirtual.presentation.PosVirtualNavigationFlowActivity
import br.com.mobicare.cielo.recebaMais.presentation.ui.fragment.UserLoanFragment
import br.com.mobicare.cielo.simulator.SimulatorNavigationFlowActivity
import br.com.mobicare.cielo.suporteTecnico.ui.activity.TechnicalSupportActivity
import br.com.mobicare.cielo.tapOnPhone.presentation.TapOnPhoneNavigationFlowActivity
import br.com.mobicare.cielo.taxaPlanos.FeeAndPlansMainFragment
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.presentation.PredictiveBatteryNavigationFlowActivity

private const val RECEBA_MAIS_TITLE = "Receba Mais"
private const val TAXAS_E_PLANOS_TITLE = "Taxas e Planos"
const val MEU_CADASTRO_TITLE = "Meu Cadastro"
private const val SOLICITACAO_MATERIAIS_TITLE = "Solicitar Materiais"

class DeeplinkRouter {

    private val flows: Map<String, DeeplinkRouterNavigationInterface> = hashMapOf(
        ARV.id to ActivityDeepLinkRouterAction(ArvNavigationFlowActivity::class.java),
        CIELO_FAROL.id to ActivityDeepLinkRouterAction(LightHouseActivity::class.java),
        RECEBA_RAPIDO.id to ActivityDeepLinkRouterAction(ReceiveAutomaticActivity::class.java),
        RECEBA_MAIS.id to FragmentDeepLinkRouterAction(
            UserLoanFragment::class.java.canonicalName,
            RECEBA_MAIS_TITLE
        ),
        PIX.id to ActivityDeepLinkRouterAction(PixRouterNavigationFlowActivity::class.java),
        SIMULADOR_VENDAS.id to ActivityDeepLinkRouterAction(SimulatorNavigationFlowActivity::class.java),
        MINHAS_VENDAS.id to ActivityDeepLinkRouterAction(MinhasVendasHomeActivity::class.java),
        RECEBIVEIS.id to ActivityDeepLinkRouterAction(MeusRecebimentosHomeActivityNew::class.java),
        CANCELAMENTO_VENDAS.id to ActivityDeepLinkRouterAction(MinhasVendasCanceladasActivity::class.java),
        SUPER_LINK.id to ActivityDeepLinkRouterAction(FluxoNavegacaoSuperlinkActivity::class.java),
        SOLICITACAO_MATERIAIS.id to FragmentDeepLinkRouterAction(
            SelfServiceSupplyFragment::class.java.canonicalName,
            SOLICITACAO_MATERIAIS_TITLE
        ),
        SUPORTE_TECNICO.id to ActivityDeepLinkRouterAction(TechnicalSupportActivity::class.java),
        TAXAS_PLANOS.id to FragmentDeepLinkRouterAction(
            FeeAndPlansMainFragment::class.java.canonicalName,
            TAXAS_E_PLANOS_TITLE
        ),
        MEU_CADASTRO.id to FragmentDeepLinkRouterAction(
            MeuCadastroFragmentAtualNovo::class.java.canonicalName,
            MEU_CADASTRO_TITLE
        ),
        DIRF.id to ActivityDeepLinkRouterAction(DirfActivity::class.java),
        CIELO_TAP.id to ActivityDeepLinkRouterAction(TapOnPhoneNavigationFlowActivity::class.java),
        PREDICTIVE_BATTERY.id to ActivityDeepLinkRouterAction(
            PredictiveBatteryNavigationFlowActivity::class.java
        ),
        MACHINE_TRACKING.id to ActivityDeepLinkRouterAction(EventTrackingNavigationFlowActivity::class.java),
        POS_VIRTUAL.id to ActivityDeepLinkRouterAction(PosVirtualNavigationFlowActivity::class.java)
    )

    fun startDeeplinkFlow(context: Context, deepLinkModel: DeepLinkModel) {
        navigateToDeeplinkDestination(context, deepLinkModel)
        removeDeepLinkModel()
    }

    private fun navigateToDeeplinkDestination(context: Context, deepLinkModel: DeepLinkModel) {
        flows[deepLinkModel.id]?.startNavigation(context, deepLinkModel)
    }

    private fun removeDeepLinkModel() {
        UserPreferences.getInstance().deleteDeepLinkModel()
    }

    private inner class FragmentDeepLinkRouterAction(
        private var clazz: String?,
        private var title: String?
    ) : DeeplinkRouterNavigationInterface {
        override fun startNavigation(context: Context, deepLinkModel: DeepLinkModel) {
            clazz?.let { itDestinationClass ->
                Intent(context, RouterFragmentInActivity::class.java).apply {
                    putExtra(FRAGMENT_TO_ROUTER, itDestinationClass)
                    putExtra(TITLE_ROUTER_FRAGMENT, title)
                    putExtra(DeepLinkConstants.DEEP_LINK_MODEL_ARGS, deepLinkModel)

                    context.startActivity(this)
                }
            }
        }
    }

    private inner class ActivityDeepLinkRouterAction(private val clazz: Class<*>) : DeeplinkRouterNavigationInterface {

        override fun startNavigation(context: Context, deepLinkModel: DeepLinkModel) {
            Intent(context, clazz).apply {
                putExtra(DeepLinkConstants.DEEP_LINK_MODEL_ARGS, deepLinkModel)
                context.startActivity(this)
            }
        }

    }

}