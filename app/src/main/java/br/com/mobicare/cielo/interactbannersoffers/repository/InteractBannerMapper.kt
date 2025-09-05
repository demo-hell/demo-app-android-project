package br.com.mobicare.cielo.interactbannersoffers.repository

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.interactBannersOffersNew.utils.enums.InteractBannerEnum
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import br.com.mobicare.cielo.interactbannersoffers.termoAceite.model.TermoAceiteObj
import br.com.mobicare.cielo.interactbannersoffers.view.InteractBannerType
import br.com.mobicare.cielo.interactbannersoffers.view.InteractLeaderboardBannerView
import com.google.firebase.crashlytics.FirebaseCrashlytics

object InteractBannerMapper {
    /** Priorities by position */
    const val PRIORITY_BANNER_LEADERBOARD_HOME = 99
    const val PRIORITY_BANNER_RECTANGLE_HOME = 0
    const val PRIORITY_BANNER_LEADERBOARD_RECEBIVEIS = 1
    const val PRIORITY_BANNER_LEADERBOARD_OUTROS = 2
    const val PRIORITY_BANNER_LEADERBOARD_SERVICOS = 3
    const val PRIORITY_BANNER_LEADERBOARD_TAXAS_E_PLANOS = 4

    /** Banners type */
    const val INTERACT_BANNER_OFFER_ARV = "ARV"
    const val INTERACT_BANNER_OFFER_RECEBA_MAIS = "RECEBA_MAIS"
    const val INTERACT_BANNER_OFFER_RECEBA_RAPIDO = "RECEBA_RAPIDO"
    const val INTERACT_BANNER_OFFER_TOKEN_CIELO = "TOKEN_CIELO"
    const val INTERACT_BANNER_OFFER_BCREDI = "BCREDI"
    const val INTERACT_BANNER_OFFER_FLIP = "FLIP"
    const val INTERACT_BANNER_OFFER_SUPER_LINK = "SUPER_LINK"
    const val INTERACT_BANNER_OFFER_TAXAS_E_PLANOS = "RECEBA_RAPIDO_MASSIVA"
    const val INTERACT_BANNER_RECEBA_RAPIDO_MASSIVA_CARENCIA = "RECEBA_RAPIDO_MASSIVA_CARENCIA"
    const val INTERACT_BANNER_OFFER_FAROL = "FAROL"
    const val INTERACT_BANNER_OFFER_CIELO_PROMO = "CIELO_PROMO"
    const val INTERACT_BANNER_OFFER_PIX = "PIX"
    const val INTERACT_BANNER_OFFER_CAIXA = "PEAC_CAIXA"
    const val INTERACT_BANNER_OFFER_PIX_PARTNER = "PIX_PARCEIROS"
    const val INTERACT_BANNER_OFFER_DEBIT = "OFERTA_SALDO_DEVEDOR"
    const val CIELO_ACAO_SELIC_RR = "CIELO_ACAO_SELIC_RR"
    const val CIELO_ACAO_RR_SELIC_E_ITC_MASTER = "CIELO_ACAO_RR_SELIC_E_ITC_MASTER"
    const val INTERACT_BANNER_OFFER_LUCKY_PROMO = "OFERTA_PROMO_DA_SORTE"
    const val PORTAL_DE_NEGOCIACAO_COBRANCA_APP = "PORTAL_DE_NEGOCIACAO_COBRANCA_APP"
    const val PORTAL_DE_NEGOCIACAO_COBRANCA_APP_EC_INATIVO = "PORTAL_DE_NEGOCIACAO_COBRANCA_APP_EC_INATIVO"
    const val BANNER_AUMENTO_VOLUME_BALCAO_CALL_LINK_APP = "BANNER_AUMENTO_VOLUME_BALCAO_CALL_LINK_APP"
    const val BANNER_AUMENTO_VOLUME_BALCAO_PARLA_APP = "BANNER_AUMENTO_VOLUME_BALCAO_PARLA_APP"
    const val BANNER_ATUALIZACAO_CADASTRAL_APP = "BANNER_ATUALIZACAO_CADASTRAL_APP"
    const val PRODUTO_RECARGA_CELULAR_SITE = "PRODUTO_RECARGA_CELULAR_SITE"
    const val CAPITAL_DE_GIRO_BB_APP = "CAPITAL_DE_GIRO_BB_APP"
    const val ATENDIMENTO_WHATSAPP_APP = "ATENDIMENTO_WHATSAPP_APP"
    const val OFERTA_BLACK_FRIDAY_ARV_APP = "OFERTA_BLACK_FRIDAY_ARV_APP"
    const val OFERTA_BLACK_FRIDAY_RR_APP = "OFERTA_BLACK_FRIDAY_RR_APP"
    const val BANNER_DIRF_APP = "BANNER_DIRF_APP"
    const val BANNER_VENDA_VIA_WPP_APP = "BANNER_P2M_APP"
    const val BANNER_MFA_ARV_P1_APP = "BANNER_MFA_ARV_P1_APP"
    const val BANNER_MFA_ARV_P2_APP = "BANNER_MFA_ARV_P2_APP"
    const val RR_SEMANA_CONSUMIDOR_APP = "RR_SEMANA_CONSUMIDOR_APP"
    const val BANNER_ARV_APP = "BANNER_ARV_APP"
    const val BANNER_SUPERLINK_APP = "BANNER_SUPERLINK_APP"
    const val BANNER_FAROL_CONTRATADO_APP = "BANNER_FAROL_CONTRATADO_APP"
    const val BANNER_FAROL_APP = "BANNER_FAROL_APP"
    const val PARCELADO_CLIENTE_APP = "PARCELADO_CLIENTE_APP"
    const val CONVERSOR_DE_MOEDAS_APP = "CONVERSOR_DE_MOEDAS_APP"
    const val PORTAL_DE_NEGOCIACAO_COBRANCA_DESCONTO_INATIVO_APP =
            "PORTAL_DE_NEGOCIACAO_COBRANCA_DESCONTO_INATIVO_APP"
    const val PORTAL_DE_NEGOCIACAO_COBRANCA_DESCONTO_ATIVO_APP =
            "PORTAL_DE_NEGOCIACAO_COBRANCA_DESCONTO_ATIVO_APP"
    const val BANNER_IDENTIDADE_DIGITAL_APP = "BANNER_IDENTIDADE_DIGITAL_APP"
    const val BANNER_PIX_APP = "BANNER_PIX_APP"
    const val PORTAL_DE_NEGOCIACAO_PASCOA_APP = "PORTAL_DE_NEGOCIACAO_PASCOA_APP"
    const val BANNER_SUPERLINK_PASCOA_APP = "BANNER_SUPERLINK_PASCOA_APP"
    const val PRODUTO_RR_APP = "PRODUTO_RR_APP"
    const val OFERTA_PARA_LP_P2M_APP = "OFERTA_PARA_LP_P2M_APP"
    const val BANNER_INCENTIVO_PIX_APP = "BANNER_INCENTIVO_PIX_APP"
    const val BANNER_SUPERLINK_DIA_DAS_MAES_APP = "BANNER_SUPERLINK_DIA_DAS_MAES_APP"
    const val BANNER_DIA_DOS_PAIS_ARV_APP = "BANNER_DIA_DOS_PAIS_ARV_APP"
    const val BANNER_DIA_DOS_PAIS_RR_APP = "BANNER_DIA_DOS_PAIS_RR_APP"
    const val BANNER_DIA_DOS_PAIS_SUPERLINK_APP = "BANNER_DIA_DOS_PAIS_SUPERLINK_APP"
    const val BANNER_RR_SEGMENTADO_BAU_APP = "BANNER_RR-SEGMENTADO_BAU_APP"
    const val BANNER_PREDICTIVE_BATTERY = "BANNER_MANUTENCAO_PREDITIVA"
    const val BANNER_MDR_OFFER_REACTIVATION = "REATIVACAO_MDR_ALUGUEL_99"
    const val BANNER_MDR_OFFER_RR_REACTIVATION = "REATIVACAO_MDR_RR_ALUGUEL_99"
    const val BANNER_MDR_OFFER_RETENTION = "RETENCAO_MDR_ALUGUEL_129"
    const val BANNER_MDR_OFFER_RR_RETENTION = "RETENCAO_MDR_RR_ALUGUEL_129"
    const val BANNER_MDR_OFFER_POSTPONED = "RETENCAO_MDR_ALUGUEL_S_POSTECIPADO"
    const val BANNER_MDR_OFFER_RR_POSTPONED = "RETENCAO_MDR_ALUGUEL_RR_S_POSTECIPADO"
    const val BANNER_MDR_OFFER_WITHOUT_EQUIPMENT = "RETENCAO_MDR_S_MAQUINA"
    const val BANNER_MDR_OFFER_RR_WITHOUT_EQUIPMENT = "RETENCAO_MDR_RR_S_MAQUINA"
    const val BANNER_ASSET_RECOVERY = "BANNER_MVP_RECUPERACAODEATIVOS_NA_APP"
    const val BANNER_PIX_POSTECIPADO_NA_APP = "BANNER_PIX_POSTECIPADO_NA_APP"
    const val BANNER_CIELOTAP_FOMENTO_NA_APP = "BANNER_CIELOTAP_FOMENTO_NA_APP"
    const val BANNER_PIX_TAXAZERO_NA_APP = "BANNER_PIX_TAXAZERO_NA_APP"
    const val BANNER_PIX_DIA_DAS_MAES_NA_APP = "BANNER_PIX_DIA_DAS_MAES_NA_APP"
    const val BANNER_ARV_OFERTA_CRISE_NA_APP = "BANNER_ARV_OFERTA_CRISE_NA_APP"
    const val BANNER_ACOMPANHAMENTO_PEDIDOMAQUINA_NA_APP = "BANNER_ACOMPANHAMENTO_PEDIDOMAQUINA_NA_APP"
    const val BANNER_PAYMENT_LINK_RELEASED_IN_APP = "BANNER_LINK_DE_PAGAMENTO_LIBERADO_NA_APP"
    const val BANNER_TAP_MENSALIDADE_ZERO_NA_APP = "BANNER_TAP_MENSALIDADE_ZERO_NA_APP"
    const val RA_D1_BASE_COM_PRODUTO = "RA_D1_BASE_COM_PRODUTO"

    private val hashmap: HashMap<Int, HiringOffers> = HashMap()

    fun orderBannerByPriority(offers: List<HiringOffers>?, priority: Int): HiringOffers? {
        try {
            hashmap.clear()
            if (offers?.firstOrNull() != null && offers.isNotEmpty())
                offers.sortedWith(compareByDescending { it.priority })
                    .forEachIndexed { index, hiringOffer ->
                        when (index) {
                            PRIORITY_BANNER_LEADERBOARD_HOME
                            -> hashmap[index] = hiringOffer
                            PRIORITY_BANNER_RECTANGLE_HOME
                            -> hashmap[index] = hiringOffer
                            PRIORITY_BANNER_LEADERBOARD_RECEBIVEIS
                            -> hashmap[index] = hiringOffer
                            PRIORITY_BANNER_LEADERBOARD_SERVICOS
                            -> hashmap[index] = hiringOffer
                            PRIORITY_BANNER_LEADERBOARD_OUTROS
                            -> hashmap[index] = hiringOffer
                            PRIORITY_BANNER_LEADERBOARD_TAXAS_E_PLANOS
                            -> hashmap[index] = hiringOffer
                        }
                    }
            return if (hashmap.isNotEmpty()) hashmap[priority] else null
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().recordException(ex)
            return null
        } catch (cast: ClassCastException) {
            FirebaseCrashlytics.getInstance().recordException(cast)
            return null
        }
    }

    fun getBanner(
        bannerName: String?,
        bannerType: InteractBannerType,
        bannerView: InteractLeaderboardBannerView
    ) {
        bannerName?.let { name ->
            try {
                InteractBannerEnum.valueOf(name).also {
                    setBanner(
                        bannerType,
                        it.leaderboardResId,
                        it.rectangleResId,
                        bannerView
                    )
                }
            } catch (exception: Exception) {
                exception.message?.logFirebaseCrashlytics()
            }
        }
    }

    private fun setBanner(
            type: InteractBannerType, leadboardImage: Int, rectangleImage: Int,
            bannerView: InteractLeaderboardBannerView
    ) {
        bannerView.setImage(
                when (type) {
                    InteractBannerType.LEADERBOARD -> leadboardImage
                    InteractBannerType.RECTANGLE -> rectangleImage
                }, type
        )
    }

    fun getTermoAceite(context: Context, offerId: Int, offerName: String): TermoAceiteObj? {
        return when (offerName) {
            //A princípio não vincularemos nenhum termo de aceite, deixando somente a estrutura para futuras inclusões.

            /*
                INTERACT_BANNER_OFFER_RECEBA_RAPIDO -> {
                TermoAceiteObj(
                    offerId,
                    offerName,
                    R.drawable.img_receba_rapido_leaderboard_banner,
                    context.getString(R.string.receba_rapido_title),
                    context.getString(R.string.receba_rapido_subtitle),
                    getTermsOfUseUrl(offerName),
                    CustomMessageSuccess(
                        context.getString(R.string.receba_rapido_title_bs_success),
                        context.getString(R.string.receba_rapido_subtitle_bs_success)
                    )
                )
            }
            */
            else -> null
        }
    }

    private fun getTermsOfUseUrl(offerName: String): String =
            BuildConfig.BANNER_TERMS_URL.replace("{bannerName}", offerName)
}
