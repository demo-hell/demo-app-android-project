package br.com.mobicare.cielo.interactbannersoffers.router

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.presentation.ArvNavigationFlowActivity
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasEngineActivity.Companion.NOT_CAME_FROM_HELP_CENTER
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_MDR_OFFER
import br.com.mobicare.cielo.commons.constants.ARG_PARAM_SUBCATEGORY_NAME
import br.com.mobicare.cielo.dirf.DirfActivity
import br.com.mobicare.cielo.eventTracking.presentation.EventTrackingNavigationFlowActivity
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.idOnboarding.router.IDOnboardingRouter
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import br.com.mobicare.cielo.interactbannersoffers.repository.InteractBannerMapper
import br.com.mobicare.cielo.lighthouse.ui.activities.LightHouseActivity
import br.com.mobicare.cielo.mdr.ui.MdrOfferActivity
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.MeuCadastroFragmentAtualNovo
import br.com.mobicare.cielo.mfa.FluxoNavegacaoMfaActivity
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.MigrationD2toD1NavigationFlowActivity
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.MigrationD2toD1NavigationFlowActivity.NavArgs.MIGRATION_OFFER_ARGS
import br.com.mobicare.cielo.newRecebaRapido.migration.d2tod1.ui.viewModel.RaD1MigrationEffectiveTimeViewModel
import br.com.mobicare.cielo.newRecebaRapido.presentation.ReceiveAutomaticActivity
import br.com.mobicare.cielo.p2m.presentation.P2MFlowActivity
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.FluxoNavegacaoSuperlinkActivity
import br.com.mobicare.cielo.posVirtual.presentation.PosVirtualNavigationFlowActivity
import br.com.mobicare.cielo.pixMVVM.presentation.router.PixRouterNavigationFlowActivity
import br.com.mobicare.cielo.recebaMais.presentation.ui.fragment.UserLoanFragment
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import br.com.mobicare.cielo.taxaPlanos.FeeAndPlansMainFragment
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.presentation.PredictiveBatteryNavigationFlowActivity
import br.com.mobicare.cielo.technicalSupport.features.predictiveBattery.utils.PredictiveBatteryConstants.PREDICTIVE_BATTERY_SERIAL_NUMBER_ARGS

class InteractBannerRouter {
    companion object {
        fun goTo(
            activity: FragmentActivity?,
            context: Context,
            offer: HiringOffers,
        ) {
            val offerType = offer.name.orEmpty()
            val urlTarget = offer.hiringUrl.orEmpty()

            if (offerType == InteractBannerMapper.BANNER_IDENTIDADE_DIGITAL_APP) {
                IDOnboardingRouter(
                    activity = activity,
                    showLoadingCallback = {},
                    hideLoadingCallback = {},
                ).showOnboarding()
            } else {
                val actions: Map<String, RouterAction> =
                    hashMapOf(
                        InteractBannerMapper.INTERACT_BANNER_OFFER_RECEBA_RAPIDO
                            to InteractBannerActivityRouterAction(ReceiveAutomaticActivity::class.java),
                        InteractBannerMapper.INTERACT_BANNER_OFFER_SUPER_LINK to
                            InteractBannerActivityRouterAction(
                                FluxoNavegacaoSuperlinkActivity::class.java,
                            ),
                        InteractBannerMapper.INTERACT_BANNER_OFFER_TOKEN_CIELO
                            to InteractBannerActivityRouterAction(FluxoNavegacaoMfaActivity::class.java),
                        InteractBannerMapper.INTERACT_BANNER_OFFER_TAXAS_E_PLANOS to
                            InteractActivityFragmentRouterAction(
                                FeeAndPlansMainFragment::class.java.canonicalName,
                                context.getString(R.string.menu_taxa_plano),
                            ),
                        InteractBannerMapper.INTERACT_BANNER_RECEBA_RAPIDO_MASSIVA_CARENCIA
                            to InteractBannerActivityRouterAction(ReceiveAutomaticActivity::class.java),
                        InteractBannerMapper.INTERACT_BANNER_OFFER_BCREDI
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.INTERACT_BANNER_OFFER_FLIP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.PORTAL_DE_NEGOCIACAO_COBRANCA_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.PRODUTO_RECARGA_CELULAR_SITE
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.BANNER_AUMENTO_VOLUME_BALCAO_PARLA_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.PORTAL_DE_NEGOCIACAO_COBRANCA_APP_EC_INATIVO
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.BANNER_AUMENTO_VOLUME_BALCAO_CALL_LINK_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.INTERACT_BANNER_OFFER_RECEBA_MAIS
                            to
                            InteractActivityFragmentRouterAction(
                                UserLoanFragment::class.java.canonicalName,
                                context.getString(R.string.text_receba_mais_title),
                            ),
                        InteractBannerMapper.INTERACT_BANNER_OFFER_CIELO_PROMO to
                            InteractBannerActivityRouterAction(
                                CentralAjudaSubCategoriasEngineActivity::class.java,
                                Bundle().apply {
                                    putString(
                                        ConfigurationDef.TAG_KEY_HELP_CENTER,
                                        ConfigurationDef.TAG_HELP_CENTER_PROMO,
                                    )
                                    putString(
                                        ARG_PARAM_SUBCATEGORY_NAME,
                                        context.getString(R.string.text_cielo_promo),
                                    )
                                    putBoolean(NOT_CAME_FROM_HELP_CENTER, true)
                                },
                            ),
                        InteractBannerMapper.INTERACT_BANNER_OFFER_FAROL
                            to InteractBannerActivityRouterAction(LightHouseActivity::class.java),
                        InteractBannerMapper.BANNER_FAROL_APP
                            to InteractBannerActivityRouterAction(LightHouseActivity::class.java),
                        InteractBannerMapper.BANNER_FAROL_CONTRATADO_APP
                            to InteractBannerActivityRouterAction(LightHouseActivity::class.java),
                        InteractBannerMapper.INTERACT_BANNER_OFFER_PIX
                            to InteractBannerActivityRouterAction(PixRouterNavigationFlowActivity::class.java),
                        InteractBannerMapper.INTERACT_BANNER_OFFER_PIX_PARTNER
                            to InteractBannerActivityRouterAction(PixRouterNavigationFlowActivity::class.java),
                        InteractBannerMapper.BANNER_PIX_APP
                            to InteractBannerActivityRouterAction(PixRouterNavigationFlowActivity::class.java),
                        InteractBannerMapper.BANNER_ATUALIZACAO_CADASTRAL_APP
                            to
                            InteractActivityFragmentRouterAction(
                                MeuCadastroFragmentAtualNovo::class.java.canonicalName,
                                context.getString(R.string.menu_meu_cadastro),
                            ),
                        InteractBannerMapper.INTERACT_BANNER_OFFER_CAIXA
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.INTERACT_BANNER_OFFER_DEBIT
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.CIELO_ACAO_SELIC_RR
                            to
                            InteractActivityFragmentRouterAction(
                                FeeAndPlansMainFragment::class.java.canonicalName,
                                context.getString(R.string.menu_taxa_plano),
                            ),
                        InteractBannerMapper.CIELO_ACAO_RR_SELIC_E_ITC_MASTER
                            to
                            InteractActivityFragmentRouterAction(
                                FeeAndPlansMainFragment::class.java.canonicalName,
                                context.getString(R.string.menu_taxa_plano),
                            ),
                        InteractBannerMapper.CAPITAL_DE_GIRO_BB_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.ATENDIMENTO_WHATSAPP_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.OFERTA_BLACK_FRIDAY_RR_APP
                            to InteractBannerActivityRouterAction(ReceiveAutomaticActivity::class.java),
                        InteractBannerMapper.OFERTA_BLACK_FRIDAY_ARV_APP
                            to
                            InteractBannerActivityRouterAction(
                                ArvNavigationFlowActivity::class.java,
                            ),
                        InteractBannerMapper.BANNER_DIRF_APP
                            to InteractBannerActivityRouterAction(DirfActivity::class.java),
                        InteractBannerMapper.BANNER_VENDA_VIA_WPP_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.OFERTA_PARA_LP_P2M_APP
                            to
                            InteractBannerActivityRouterAction(
                                P2MFlowActivity::class.java,
                            ),
                        InteractBannerMapper.BANNER_MFA_ARV_P1_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.BANNER_MFA_ARV_P2_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.RR_SEMANA_CONSUMIDOR_APP
                            to InteractBannerActivityRouterAction(ReceiveAutomaticActivity::class.java),
                        InteractBannerMapper.BANNER_ARV_APP
                            to
                            InteractBannerActivityRouterAction(
                                ArvNavigationFlowActivity::class.java,
                            ),
                        InteractBannerMapper.BANNER_SUPERLINK_APP
                            to
                            InteractBannerActivityRouterAction(
                                FluxoNavegacaoSuperlinkActivity::class.java,
                            ),
                        InteractBannerMapper.PARCELADO_CLIENTE_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.CONVERSOR_DE_MOEDAS_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.PORTAL_DE_NEGOCIACAO_COBRANCA_DESCONTO_INATIVO_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.PORTAL_DE_NEGOCIACAO_COBRANCA_DESCONTO_ATIVO_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.PORTAL_DE_NEGOCIACAO_PASCOA_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.BANNER_SUPERLINK_PASCOA_APP
                            to
                            InteractBannerActivityRouterAction(
                                FluxoNavegacaoSuperlinkActivity::class.java,
                            ),
                        InteractBannerMapper.PRODUTO_RR_APP
                            to
                            InteractBannerActivityRouterAction(
                                ReceiveAutomaticActivity::class.java,
                            ),
                        InteractBannerMapper.BANNER_INCENTIVO_PIX_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.BANNER_SUPERLINK_DIA_DAS_MAES_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.OFERTA_PARA_LP_P2M_APP
                            to
                            InteractBannerActivityRouterAction(
                                P2MFlowActivity::class.java,
                            ),
                        InteractBannerMapper.BANNER_DIA_DOS_PAIS_ARV_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.BANNER_DIA_DOS_PAIS_RR_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.BANNER_DIA_DOS_PAIS_SUPERLINK_APP
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.BANNER_RR_SEGMENTADO_BAU_APP
                            to InteractBannerActivityRouterAction(ReceiveAutomaticActivity::class.java),
                        InteractBannerMapper.BANNER_PREDICTIVE_BATTERY
                            to
                            InteractBannerActivityRouterAction(
                                PredictiveBatteryNavigationFlowActivity::class.java,
                                Bundle().apply {
                                    putString(
                                        PREDICTIVE_BATTERY_SERIAL_NUMBER_ARGS,
                                        offer.serialNumber.orEmpty(),
                                    )
                                },
                            ),
                        InteractBannerMapper.BANNER_ASSET_RECOVERY
                            to InteractBannerActivityRouterURLAction(urlTarget),
                        InteractBannerMapper.BANNER_MDR_OFFER_REACTIVATION to createMdrBannerActivityRouterAction(offer),
                        InteractBannerMapper.BANNER_MDR_OFFER_RR_REACTIVATION to createMdrBannerActivityRouterAction(offer),
                        InteractBannerMapper.BANNER_MDR_OFFER_RETENTION to createMdrBannerActivityRouterAction(offer),
                        InteractBannerMapper.BANNER_MDR_OFFER_RR_RETENTION to createMdrBannerActivityRouterAction(offer),
                        InteractBannerMapper.BANNER_MDR_OFFER_POSTPONED to createMdrBannerActivityRouterAction(offer),
                        InteractBannerMapper.BANNER_MDR_OFFER_RR_POSTPONED to createMdrBannerActivityRouterAction(offer),
                        InteractBannerMapper.BANNER_MDR_OFFER_WITHOUT_EQUIPMENT to createMdrBannerActivityRouterAction(offer),
                        InteractBannerMapper.BANNER_MDR_OFFER_RR_WITHOUT_EQUIPMENT to createMdrBannerActivityRouterAction(offer),
                        InteractBannerMapper.BANNER_PIX_POSTECIPADO_NA_APP to
                            InteractActivityFragmentRouterAction(
                                FeeAndPlansMainFragment::class.java.canonicalName,
                                context.getString(R.string.menu_taxa_plano),
                            ),
                        InteractBannerMapper.BANNER_CIELOTAP_FOMENTO_NA_APP to
                            InteractBannerActivityRouterAction(
                                PosVirtualNavigationFlowActivity::class.java,
                            ),
                        InteractBannerMapper.BANNER_PIX_TAXAZERO_NA_APP to
                            InteractBannerActivityRouterAction(
                                PixRouterNavigationFlowActivity::class.java,
                            ),
                        InteractBannerMapper.BANNER_PIX_DIA_DAS_MAES_NA_APP to
                            InteractBannerActivityRouterAction(
                                PixRouterNavigationFlowActivity::class.java,
                            ),
                        InteractBannerMapper.BANNER_MDR_OFFER_RR_WITHOUT_EQUIPMENT to
                            createMdrBannerActivityRouterAction(
                                offer,
                            ),
                        InteractBannerMapper.BANNER_ARV_OFERTA_CRISE_NA_APP
                            to
                            InteractBannerActivityRouterAction(
                                ArvNavigationFlowActivity::class.java,
                            ),
                        InteractBannerMapper.BANNER_PAYMENT_LINK_RELEASED_IN_APP to
                            InteractBannerActivityRouterAction(
                                FluxoNavegacaoSuperlinkActivity::class.java,
                            ),
                        InteractBannerMapper.BANNER_TAP_MENSALIDADE_ZERO_NA_APP
                            to InteractBannerActivityRouterAction(PosVirtualNavigationFlowActivity::class.java),
                        InteractBannerMapper.BANNER_ACOMPANHAMENTO_PEDIDOMAQUINA_NA_APP to
                            InteractBannerActivityRouterAction(
                                EventTrackingNavigationFlowActivity::class.java,
                            ),
                        InteractBannerMapper.BANNER_PAYMENT_LINK_RELEASED_IN_APP to
                            InteractBannerActivityRouterAction(
                                FluxoNavegacaoSuperlinkActivity::class.java,
                            ),
                        InteractBannerMapper.RA_D1_BASE_COM_PRODUTO to
                                createRaD1MigrationRouterAction(offer),
                        InteractBannerMapper.BANNER_ACOMPANHAMENTO_PEDIDOMAQUINA_NA_APP to InteractBannerActivityRouterAction(EventTrackingNavigationFlowActivity::class.java)
                    )

                actions[offerType]?.execute(context)
            }
        }

        private fun createRaD1MigrationRouterAction(offer: HiringOffers) =
            if (FeatureTogglePreference.instance.getFeatureTogle(
                    RaD1MigrationEffectiveTimeViewModel.RA_D1_MIGRATION_FT_KEY)) {
                InteractBannerActivityRouterAction(
                    MigrationD2toD1NavigationFlowActivity::class.java,
                    Bundle().apply {
                        putParcelable(MIGRATION_OFFER_ARGS, offer)
                    })
            } else {
                object : RouterAction {
                    override fun execute(context: Context) {
                        //do nothing
                    }
                }
            }

        private fun createMdrBannerActivityRouterAction(offer: HiringOffers): RouterAction {
            return InteractBannerActivityRouterAction(
                MdrOfferActivity::class.java,
                Bundle().apply {
                    putParcelable(ARG_PARAM_MDR_OFFER, offer)
                },
            )
        }
    }
}

interface RouterAction {
    fun execute(context: Context)
}
