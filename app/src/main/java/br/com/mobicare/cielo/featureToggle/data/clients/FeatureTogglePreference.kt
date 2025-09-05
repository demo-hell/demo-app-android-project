package br.com.mobicare.cielo.featureToggle.data.clients

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.PriorityWarningVisualization
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.featureToggle.domain.Feature
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggle
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleModal
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.orhanobut.hawk.Hawk

class FeatureTogglePreference {
    private object Holder {
        val INSTANCE = FeatureTogglePreference()
    }

    fun saveFeatureTogle(
        feature: String?,
        featureToggle: Feature?,
    ) {
        Hawk.put(feature, featureToggle)
    }

    fun saveAllFeatureToggles(featureTogglesJson: String) {
        Hawk.put(FEATURE_TOGGLE_LIST, featureTogglesJson)
    }

    fun getAllFeatureToggles(): List<FeatureToggle> {
        return try {
            val featureToggles = Hawk.get(FEATURE_TOGGLE_LIST, EMPTY)
            val type = object : TypeToken<List<FeatureToggle>>() {}.type
            Gson().fromJson(featureToggles, type)
        } catch (exception: Exception) {
            exception.message.logFirebaseCrashlytics()
            emptyList()
        }
    }

    fun getFeatureToggleObject(feature: String): Feature? {
        return try {
            Hawk.get(feature, null)
        } catch (runTime: RuntimeException) {
            Hawk.delete(feature)
            null
        } catch (ex: Exception) {
            ex.message?.let { error ->
                FirebaseCrashlytics.getInstance().log(error)
            }
            null
        }
    }

    fun getFeatureTogle(feature: String): Boolean {
        val featureToggle = getFeatureToggleObject(feature)
        return featureToggle?.show ?: false
    }

    fun getFeatureToggle(feature: String): Boolean? {
        val featureToggle = getFeatureToggleObject(feature)
        return featureToggle?.show
    }

    fun isActivate(vararg strings: String): Boolean {
        strings.forEach {
            if (!getFeatureTogle(it)) {
                return false
            }
        }
        return true
    }

    fun saveFeatureToggleModal(statusMessage: FeatureToggleModal?) {
        val statusMessageJson = Gson().toJson(statusMessage)

        UserPreferences.getInstance()
            .put(key = MODAL_DINAMICA_OBJ, value = statusMessageJson, isProtected = true)
    }

    fun getFeatureToggleModal(): FeatureToggleModal? {
        return try {
            val featureToggleModal =
                UserPreferences.getInstance()
                    .get(key = MODAL_DINAMICA_OBJ, defaultValue = EMPTY, isProtected = true)

            return Gson().fromJson(featureToggleModal, FeatureToggleModal::class.java)
        } catch (exception: Exception) {
            exception.message.logFirebaseCrashlytics()
            null
        }
    }

    fun getFeatureToggleString(feature: String): String? {
        val featureToggle = getFeatureToggleObject(feature)
        return featureToggle?.statusMessage
    }

    fun saveSawWarning(warningVisualization: List<PriorityWarningVisualization?>?) {
        val warningVisualizationJson = Gson().toJson(warningVisualization)

        UserPreferences.getInstance().put(
            key = SAW_WARNING_FEATURE_TOGGLE,
            value = warningVisualizationJson,
            isProtected = true,
        )
    }

    fun getSawWarning(): ArrayList<PriorityWarningVisualization?>? {
        return try {
            val priorityWarningVisualization =
                UserPreferences.getInstance()
                    .get(key = SAW_WARNING_FEATURE_TOGGLE, defaultValue = EMPTY, isProtected = true)

            val type = object : TypeToken<ArrayList<PriorityWarningVisualization?>>() {}.type
            return Gson().fromJson<ArrayList<PriorityWarningVisualization?>>(
                priorityWarningVisualization,
                type,
            )
        } catch (exception: Exception) {
            exception.message.logFirebaseCrashlytics()
            null
        }
    }

    companion object {
        private const val FEATURE_TOGGLE = "feature_toggle"
        const val MINHAS_SOLICITACOES = "minhas_solicitacoes"
        const val ASSISTENTE_CIELO = "assistente_cielo"
        const val MINHAS_MAQUINAS = "minhas_maquinas"
        const val ASSISTENTE_CIELO_OPEN = "assistente_cielo_open"
        const val PRODUTOS_SERVICOS = "produtos_servicos"
        const val CIELO_FIDELIDADE = "cielo_fidelidade"
        const val MINHAS_VENDAS = "minhas_vendas"
        const val MEUS_RECEBIMENTOS = "meus_recebimentos"
        const val ANTECIPE_VENDAS = "antecipe_vendas"
        const val ANTECIPE_VENDAS_CARD_AVULSA = "antecipe_vendas_card_avulsa"
        const val ANTECIPE_VENDAS_CARD_PROGRAMADA = "antecipe_vendas_card_programada"
        const val ANTECIPE_VENDAS_TAB_AVULSA = "antecipe_vendas_tab_avulsa"
        const val ANTECIPE_VENDAS_TAB_PROGRAMADA = "antecipe_vendas_tab_programada"
        const val ANTECIPE_VENDAS_MERCADO_PROGRAMADA =
            "antecipe_vendas_recebiveis_mercado_programada"
        const val ANTECIPE_VENDAS_MERCADO_AVULSA = "antecipe_vendas_recebiveis_mercado_avulsa"
        const val MEU_CADASTRO = "meu_cadastro"
        const val CENTRAL_AJUDA = "central_ajuda"
        const val SUPORTE_TECNICO = "suporte_tecnico"
        const val TAXAS_BANDEIRAS = "bandeiras_taxas"
        const val RECEBA_MAIS = "receba_mais"
        const val CIELO_FAROL = "farol"
        const val MY_CARDS: String = "meus_cartoes"
        const val MY_CARDS_TRANSFER: String = "meus_cartoes_transferencia"
        const val MY_CARDS_PAYMENT: String = "meus_cartoes_pagamento"
        const val MY_CARDS_BANNER: String = "meus_cartoes_banner"
        const val NOTIFICATION_BOX: String = "caixa_notificacoes"
        const val RESEARCHES_SATISFACTION = "pesquisa_satisfacao"
        const val PAGAMENTO_POR_LINK = "pagamento_por_link"
        const val CHAT = "chat"
        const val CENTRAL_AJUDA_WHATSAPP = "central_ajuda_whatsapp"
        const val FEATURE_SERVICE = "feature_service"
        const val TAXA_PLANOS = "taxas_planos"
        const val INCLUIR_DOMICILIO_BANCARIO = "incluir_domicilio_bancario"
        const val EFETIVAR_CANCELAMENTO = "efetivar_cancelamento"
        const val MEUS_CANCELAMENTOS = "consultar_cancelamento"
        const val TRATAMENTO_FULL_SEC = "tratamento_full_sec"
        const val TRATAMENTO_RECEM_CREDENCIADO = "tratamento_recem_credenciado"
        const val ACOMPANHA_TROCA_DOMICILIO = "acompanha_troca_domicilio"

        const val SUPERLINK_ENTREGA = "super_link_entrega"
        const val SUPERLINK_ENTREGA_LOGGI = "super_link_entrega_loggi"
        const val SUPERLINK_ENTREGA_CORREIOS = "super_link_entrega_correios"
        const val SUPERLINK_ENTREGA_FRETE_FIXO = "super_link_entrega_frete_fixo"
        const val SUPERLINK_ENTREGA_FRETE_GRATIS = "super_link_entrega_frete_gratis"
        const val LINK_PAGAMENTO_CORREIOS = "link-pagamento-correios"

        const val MULTIPLE_FACTOR_AUTHENTICATION = "segundo_fator_autenticacao"
        const val MULTIPLE_FACTOR_AUTHENTICATION_ONBORDING = "segundo_fator_autenticacao_onboarding"

        const val RECEBA_RAPIDO_CONTRATACAO = "receba-rapido-contratacao"
        const val RECEBA_RAPIDO_CANCELAMENTO = "receba-rapido-cancelamento"
        const val AUTOMATIC_RECEIPT_OPTIONAL = "recebimento-automatico-opcional"

        const val LGPD = "lgpd"
        const val MFA_EC_STATUS_VALIDACAO = "segundo-fator-exibicao-de-status"
        const val INTERACT_BANNERS = "interact_banners"
        const val PIX = "pix"
        const val DIRF = "dirf"
        const val CERTIFICATE_PINNING = "certificate_pinning"
        const val PERFIL_PERSONALIZADO = "perfil_personalizado"
        const val PERFIL_TECNICO = "perfil_tecnico"

        const val BALCAO_RECEBIVEIS = "balcao-recebiveis"
        const val DEBITO_EM_CONTA = "debito-em-conta"
        const val SECURITY_HASH = "security_hash"
        const val SECURITY_HASH_LOCATION = "security_hash_location"

        const val BLOQUEIO_APP_INDISPONIVEL = "bloqueio_indisponibilidade"
        const val MODAL_DINAMICA = "alerta_prioridade"
        const val MODAL_DINAMICA_OBJ = "alerta_prioridade_obj"
        const val SALES_SIMULATOR = "simulador_vendas"
        const val PARCELADO_CLIENTE_SIMULADOR = "parcelado-cliente-simulador"

        const val SAW_WARNING_FEATURE_TOGGLE = "saw_warning_feature_toggle"

        const val POSTECIPADO = "postecipado"
        const val MENU_PIX = "pix_menu"

        const val ID_ONBOARDING = "id_onboarding"
        const val ONBOARDING_SEND_SMS = "onboarding_send_sms"
        const val ONBOARDING_SEND_WHATSAPP = "onboarding_send_whatsApp"
        const val ONBOARDING_CUSTOMER_SETTINGS = "onboarding_customer_settings"
        const val ACCESS_MANAGER = "access_manager"
        const val FEES_PER_FLAG_HOME = "taxas_por_bandeira_home"

        const val SOLESP = "solesp"
        const val SOLESP_REMOVE = "solesp-remove"
        const val CHARGEBACK_REMOVE = "chargeback-remove"
        const val P2M_WHATS_APP = "p2m_fees"

        const val USE_REASONMESSAGE_CHARGEBACK = "utiliza_reasonmessage_chargeback"
        const val SHOW_CHARGEBACK_RDR_CARD_IN_DETAILS = "show_chargeback_rdr"

        const val POS_VIRTUAL_WHITE_LIST = "pos-virtual-white-list"
        const val POS_VIRTUAL_BUTTON_HOME = "pos-virtual-btn-vender"
        const val POS_VIRTUAL_BS_CONFIRM_TERM = "pos-virtual-confirmar-prazos"
        const val POS_VIRTUAL_BS_CONFIRM_TERM_TAP = "pos-virtual-confirmar-prazos-tap"
        const val POS_VIRTUAL_BS_CONFIRM_TERM_SUPER_LINK = "pos-virtual-confirmar-prazos-super-link"
        const val POS_VIRTUAL_BS_CONFIRM_TERM_PIX = "pos-virtual-confirmar-prazos-pix"
        const val POS_VIRTUAL_404 = "POS-VIRTUAL-404"
        const val POS_VIRTUAL_REQUIRED_DATA_FIELD = "pos-virtual-required-data-field"

        const val NEW_RESEARCH_ENVIRONMENT = "new_research_environment"

        const val CONTACT_CIELO = "contact_cielo"
        const val CONTACT_CIELO_GERENTE_VIRTUAL = "contact_cielo_gerente_virtual"

        const val RA_CANCEL_WHATSAPP_ONLY = "ra_cancel_whatsapp_only"

        const val LOCAL_DATE_DIFFERENCE = "local_date_difference"

        const val OPEN_FINANCE_DEEPLINK_DETAINER = "open_finance_deeplink_detentora"
        const val OPEN_FINANCE_HOME_DATA = "open_finance_home_dados"
        const val OPEN_FINANCE_HOME_PAYMENTS = "open_finance_home_pagamentos"
        const val OPEN_FINANCE_HOME_SHARING = "open_finance_home_compartilhamento"

        const val HOME_ALERT_CARD_ARV = "arv_home_alert_card"

        const val PREDICTIVE_BATTERY = "bateria-preditiva"
        const val GENERAL_EVENT_TRACKING = "tracking_events"

        const val PIX_BUTTON_TRANSACTION_ANALYZE = "show-button-transaction-analyze-pix"
        const val PIX_BUTTON_CHANGE_TYPE_ACCOUNT_TO_SCHEDULED_TRANSFER = "scheduled-transfer-enable"
        const val PIX_SHOW_MODAL_NEW_LAYOUT_PIX_2024_1 = "show-modal-new-layout-pix-2024-1"
        const val PIX_SHOW_BUTTON_TRANSFER_SCHEDULED_BALANCE = "show_button_transfer_scheduled_balance"
        const val PIX_SHOW_BUTTON_TRANSFER_RECURRENCE = "show-button-transfer-recurrence-pix"
        const val PIX_SHOW_NEW_SCHEDULING_EXTRACT_2024_2 = "show-new-pix-scheduling-extract-2024-2"

        const val ALERT_RECEIVABLE = "alert_receivable"

        const val DATE_ALERT_RECEIVABLE = "date_alert_receivable"
        const val CREATE_ACCOUNT_LOGIN = "criar_conta_login"

        const val GOOGLE_PLAY_REVIEW = "google_play_review"
        const val GOOGLE_PLAY_REVIEW_SALES_TAP = "google_play_review_sales_tap"
        const val TRANSPARENT_LOGIN = "transparent_login"
        const val TURBO_REGISTRATION = "credenciamento_turbo_area_logada"
        const val GOOGLE_PLAY_REVIEW_DIRF = "google_play_review_dirf"
        const val FEATURE_TOGGLE_LIST = "feature_toggle_list"
        const val SHOW_RECEIVABLES_WEB = "show_receivables_web"
        const val SHOW_SALES_WEB = "show_sales_web"


        const val ARV_ENABLE_WHATSAPP_NEWS = "arv-enable-whatsapp-news"
        const val SHOW_SALE_SIMULATOR_RECEIVE_TOTAL_VALUE_FT_KEY = "showSaleSimulatorReceiveTotalValue"
        const val POS_ACCREDITATION_ENABLE = "pos_accreditation_enable"

        val instance: FeatureTogglePreference
            get() = Holder.INSTANCE
    }
}
