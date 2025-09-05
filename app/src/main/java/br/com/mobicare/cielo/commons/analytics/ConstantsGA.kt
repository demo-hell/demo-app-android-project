package br.com.mobicare.cielo.commons.analytics

const val TAG_GA_GLOBAL = "TAG_GA ==>>>"
const val GLOBAL = "global"
const val ANALYTICS_TAG = "analytics"
const val GA4_TAG = "analytics4"

const val SUCESSO = "sucesso"
const val ERRO = "erro"

const val SIM = "sim"
const val NAO = "nao"

const val PF = "PF"
const val PJ = "PJ"

const val USER_INTERNAL = "interno"
const val USER_COMMON = "comum"

const val USER_ID = "user_id"
const val USER_PROPERTIES = "user_properties"
const val ESTABLISHMENT = "estabelecimento"

const val CONFIRMACAO = "confirmacao"
const val RETRY = "retry"

inline fun <reified T : Enum<T>> printValueOf(item: String): String {
    return enumValueOf<T>(item).toString()
}

/***************************************************************************************************
 *                              LOGIN
 * ************************************************************************************************/

const val LOGIN_LABEL_EMAIL = "cpf ou email"
const val LOGIN_LABEL_SENHA = "senha"
const val LOGIN_INTERACAO_1 = "login/passo 1"
const val LOGIN_INTERACAO_2 = "login/passo 2"
const val LOGIN = "login"
const val LOGIN_PASSO_1 = "/login/passo 1"
const val LOGIN_PASSO_2 = "/login/passo 2"
const val LOGIN_COMO_ACESSAR = "login/como acessar"
const val LOGIN_SPLASH = "/login/splash"
const val LOGIN_COMO_ACESSAR_PASSO_1 = "login/como acessar/passo 1"
const val LOGIN_COMO_ACESSAR_PASSO_2 = "login/como acessar/passo 2"
const val LOGIN_COMO_ACESSAR_PASSO_3 = "login/como acessar/passo 3"
const val LOGIN_USAR_OUTRA_CONTA = "login/usar outra conta"

/***************************************************************************************************
 *                              ESQUECI MINHA SENHA
 * ************************************************************************************************/

const val ESQUECI_SENHA_LABEL = "login/esqueceu sua senha"
const val ES_BANCARIA = "bancaria"
const val ES_DIGITAL_PRE_PAGO = "digital pre-pago"

const val ES_CAMPO_NRO_ESTABELECIMENTO = "nro do estabelecimento"
const val ES_CAMPO_BANCO = "banco"

const val ES_ACESSO_NR_ESTABELECIMENTO = "nro estabelecimento"
const val ES_ACESSO_EMAIL = "email"
const val ES_ACESSO_CPF = "cpf"
const val ES_ACESSO_AGENCIA = "agencia"
const val ES_ACESSO_NRO_CONTA = "nro conta"
const val ES_ACESSO_DIGITO = "digito"
const val ES_ACESSO_BANCO = "banco"
const val ES_ACESSO_NRO_SERIE = "nro de serie"
const val ES_ACESSO_TIPO_CONTA = "tipo de conta"
const val ES_ACESSO_PERFIL_CONTA = "perfil de conta"

const val NRO_ESTABELECIMENTO = "nro-estabelecimento"
const val SCREEN_NAME_ESQUECI_USUARIO = "nao-sei-nro-estabelecimento/EsqueciOUsuario"
const val SCREEN_NAME_ESTABLISHMENT_PJ = "nao-sei-nro-estabelecimento/EsqueciONEstabelecimentoPessoaJuridica"
const val SCREEN_NAME_ESTABLISHMENT_PF = "nao-sei-nro-estabelecimento/EsqueciONEstabelecimentoPessoaFisica"
const val AUTO_CADASTRO_INICIO = "autocadastro/inicio"

/***************************************************************************************************
 *                              AUTOCADASTRO
 * ************************************************************************************************/
const val LOGIN_ESQUECI_A_SENHA = "login/esqueci a senha/[[acesso]]:formulario"
const val AUTOCADASTRO = "autocadastro"
const val AUTOCADASTRO_CLIENTE = "autocadastro/cliente"
const val AUTOCADASTRO_NAO_CLIENTE = "autocadastro/nao cliente"

/***************************************************************************************************
 *                              HOME LOGADA
 * ************************************************************************************************/
const val HOME_LOGADA = "home logada"
const val HOME_LOGADA_SCREEN_VIEW = "/home logada"
const val HOME_FAZER_UMA_VENDA_VIEW = "/fazer uma venda"
const val HOME_ANTECIPE_SUAS_VENDAS_SCREEN_NAME = "/antecipe suas vendas"
const val HOME_LOGADA_ESTABELECIMENTOS = "meus estabelecimentos"
const val MEUS_ESTABELECIMENTOS_ADICINAR = "meus estabelecimentos/adicionar estabelecimento"

/***************************************************************************************************
 *                              MEU CADASTRO
 * ************************************************************************************************/

const val MEUS_CADASTRO = "meu cadastro"
const val MEUS_CADASTRO_ADICIONAR = "contas/adicionar conta"
const val ESTABELECIMENTO_NOME = "estabelecimento | editar nome fantasia"
const val ESTABELECIMENTO_ENDERECO = "estabelecimento | editar enderecos"
const val ESTABELECIMENTO_OWNER = "estabelecimento | editar dados do proprietario"
const val ESTABELECIMENTO_CONTATO = "estabelecimento | editar dados de contato"
const val MEUS_CADASTRO_USUARIO = "usuario"
const val MEUS_CADASTRO_USUARIO_ALTERAR_SENHA = "usuario | alterar senha"
const val MEUS_CADASTRO_CONTAS_ADICIONAR = "contas | adicionar conta"
const val MEUS_CADASTRO_CONTAS_TRANSFERIR_DESTINO = "contas | transferir bandeiras | conta de destino"
const val MEUS_CADASTRO_CONTAS_TRANSFERIR_BANDEIRA = "transferir bandeira"
const val MEUS_CADASTRO_NOME_FANTASIA = "estabelecimento | editar nome fantasia | clique"

/***************************************************************************************************
 *                              MEUS CARTÕES
 * ************************************************************************************************/

const val MEUS_CARTOES = "meus cartoes"
const val MEUS_CARTOES_MODAL = "modal | desbloqueio do cartao | dados do cartao"
const val MEUS_CARTOES_TED = "formulario | transferencia bancaria ted"
const val MEUS_CARTOES_PG_CONTA = "formulario | pagamento de contas"
const val MEUS_CARTOES_CONFIMACAO_PG_CONTA = "confirmacao | pagamento de contas"
const val MEUS_CARTOES_CONFIMACAO_PG_TRANSFERENCIA = "confirmacao | transferencia bancaria ted"

/***************************************************************************************************
 *                              ARV
 * ************************************************************************************************/
const val ANTECIPE_SUAS_VENDAS = "antecipe suas vendas"
const val FLUXO_CONTRATACAO = "fluxo contratacao"
const val CONFIRMAR_ANTECIPACAO = "confirmar antecipacao"
const val AVULSO = "avulso"
const val PROGRAMADO = "programado"
const val PROGRAMADO_DIARIO = "programado"
const val EXIBICAO = "exibicao"
const val ANTECIPACAO = "antecipacao"
const val ANTECIPACAO_AVULSA = "antecipacao avulsa"
const val ANTECIPACAO_PROGRAMADA = "antecipacao programada"
const val ANTECIPE_SUAS_VENDAS_SIM = "/antecipe suas vendas/sim"
const val ANTECIPE_SUAS_VENDAS_NAO = "/antecipe suas vendas/nao"

/***************************************************************************************************
 *                              SUPPLEIS
 * ************************************************************************************************/
const val SOLICITAR_MATERIAIS = "solicitar materiais"
const val BOBINA = "bobina"

/***************************************************************************************************
 *                              MAQUININHAS
 * ************************************************************************************************/

const val SOLICITAR_MAQUININHA = "solicitar maquininha"
const val PASSO_FORMAT = "passo %d"

/***************************************************************************************************
 *                              SUPORTE TÉCNICO
 * ************************************************************************************************/
const val SUPORTE_TECNICO = "suporte tecnico"

/***************************************************************************************************
 *                              MFA TOKEN
 * ************************************************************************************************/

const val MFA_NOVO_TOKEN = "token"
const val MFA_NOVO_TOKEN_TROCA = "token | troca de device"
const val MFA_NOVO_TOKEN_LOGIN = "login"
const val MFA_VISUALIZAR_TOKEN = "visualizar token"
const val MFA_VISUALIZAR_ONBOARDING = "visualizar onboarding"
const val MFA_ATIVO = "ativo"
const val MFA_PASSIVO = "passivo"
const val MFA_DOMICILIO = "domicilio"
const val MFA_VALIDAR = "validar"
const val MFA_ENVIAR = "enviar"
const val MFA_STATUS = "status"

const val MFA_FAQ = "FAQ token"
const val MFA_CONFIGURACAO = "configurar"
const val MFA_CONFIGURACAO_TOKEN = "configuracao token"
const val MFA_VALIDACAO_TOKEN = "validacao token"
const val MFA_ERROR = "indisponivel temporariamente"

const val MFA_TELA_CONFIGURACAO = "configuracao do token"
const val MFA_TELA_EXIBICAO = "exibicao do token"
const val MFA_TELA_VALIDACAO = "validacao do token"
const val MFA_CONFIGURE_O_TOKEN = "configure_o_token_de_autenticacao"
const val MFA_SCREEN_VIEW = "/configuracao_do_token"

/***************************************************************************************************
 *                              MFA TOKEN
 * ************************************************************************************************/

const val SCREENVIEW_RECEBA_RAPIDO = "/receba rapido/"
const val SCREENVIEW_RECEBA_RAPIDO_TAXAS = "/receba rapido/taxas/"

/***************************************************************************************************
 *                              CONTA DIGITAL
 * ************************************************************************************************/

const val CONTA_DIGITAL = "conta digital"
const val CONTA_DIGITAL_ATIVAR_CADASTRO = "ativar cadastro"
const val CONTA_DIGITAL_ATIVAR_CADASTRO_ATIVACAO = "ativar cadastro | ativacao"
const val CONTA_DIGITAL_DESBLOQUEIO_CARTAO = "desbloqueio do cartao"
const val CONTA_DIGITAL_DESBLOQUEIO_CARTAO_CONFIRMACAO = "desbloqueio do cartao | confirmacao"
const val CONTA_DIGITAL_TRANSFERENCIA_TED = "transferencia bancaria ted"
const val CONTA_DIGITAL_TRANSFERENCIA_TED_CONFIRMACAO = "transferencia bancaria ted | confirmacao"
const val CONTA_DIGITAL_PAGAMENTO_CONTAS = "pagamento de contas"
const val CONTA_DIGITAL_PAGAMENTO_CONTAS_COMFIRMACAO = "pagamento de contas | confirmacao"

object Category {
    const val APP_CIELO = "app cielo"
    const val GLOBAL = "global"
    const val MENSAGEM_SUCESSO = "mensagem sucesso"
    const val MINHAS_VENDAS = "minhas vendas"
    const val SOLICITAR_MATERIAIS = "solicitar materiais"
    const val CAMPANHAS_INTERACT = "campanhas interact"

    const val TAP_LABEL = "TapLabel"
    const val SWIPE_SCREEN = "SwipeScreen"
    const val TAPTEXTFIELD = "TapTextField"
    const val TAPICON = "TapIcon"
    const val TAPBUTTON = "TapButton"
    const val TAPCARD = "TapCard"
    const val TAPRADIO = "TapRadio"
    const val CANCELAR_RR = "cancelamento rr"
    const val CIELO_UNIFICA = "cielo unifica"
    const val HOME = "home"
    const val MEU_CADASTRO = "meu cadastro"
    const val ID_ONBOARDING = "identidade digital"
    const val TOKEN = "token"
    const val CIELO_TAP = "cielo tap"
    const val CIELO_SERVICES = "/servicos"
    const val HOME_NEW_ARV = "antecipacao de recebiveis novo"
    const val HOME_ARV = "antecipacao de recebiveis"
}

object Action {
    const val FORMULARIO = "formulario"
    const val ICONES_TOPO = "icones topo"
    const val ICONE = "icone"
    const val MENU = "menu"
    const val GERAL = "geral"
    const val MODAL = "modal"
    const val CLIQUE = "clique"
    const val EXIBICAO = "exibicao"
    const val INTERACAO = "interacao"
    const val VALIDACAO = "validacao"
    const val MAIS_FILTROS = "mais filtros"
    const val HEADER = "header"
    const val ATUALIZAR_ACESSO = "atualizar acesso"
    const val ONBOARDING = "onboarding"
    const val BOTAO = "botao"
    const val CLICK = "click"
    const val EDIT = "editar"
    const val DELETE = "excluir"
    const val ABA = "aba"
    const val CARTOES = "cartoes"
    const val LINK = "link"
    const val FILTRO = "filtro"
    const val EXCLUIR = "excluir"
    const val ELEGIBILIDADE = "elegibilidade"
    const val CALLBACK = "callback"
    const val SELECAO = "selecao"
    const val CAMPO = "campo"
    const val CANCELAR = "cancelar"
    const val AVANCAR = "avancar"
    const val CONCLUIR = "concluir"

    const val SOLICITAR_MATERIAIS = "material"
    const val SOLICITAR_MAQUININHA = "maquininha"
    const val BANNER = "banner"
    const val POPUP = "popup"
    const val SALVAR = "salvar"
    const val ALTERAR_SENHA = "alterar senha"
    const val CONFIRMAR = "confirmar"

    const val HOME = "home"
    const val HOME_INICIO = "Inicio"

    const val HOME_MINHAS_VENDAS = "MinhasVendas"

    const val EXTRATO_DETALHES_VENDA = "DetalhesDaVenda"
    const val LOGIN_CIELO_EC = "LoginCieloEc"
    const val CENTRAL_DE_AJUDA = "CentralDeAjuda"
    const val HOME_MINHAS_VENDAS_PERIODO = "MinhasVendasPeriodo"

    const val HOME_PATH = "Inicio/"
    const val MY_CREDIT_CARDS_PATH = "MeusCartoes"

    const val UNLOCK_CARD_SCREEN = "desbloquearCartao"
    const val UNLOCCK_CARD_ACTIVATION_SCREEN = "ativacao"

    const val CARD_FWD_SITE = "abrirSite"
    const val ENTRE_EM_CONTATO = "entre em contato"
    const val NOVO_PAGAMENTO = "novo pagamento"
    const val LINKS_ATIVOS = "links ativos"
    const val TIPO_DE_LINK = "tipo de link"
    const val DELIVERY_TYPE = "tipo de entrega"
    const val PRODUCT_DETAIL = "detalhe do produto"
    const val CARD = "card"
    const val VENDA = "venda"

    const val BUSCAR = "buscar"
    const val LIMPAR = "limpar"
    const val DETALHE = "detalhe"

    const val COMPARTILHAR = "compartilhar"
    const val CONTINUAR = "continuar"
    const val COPIAR = "copiar"
    const val VOLTAR = "voltar"
    const val FECHAR = "fechar"
    const val TOGGLE = "toggle"
    const val SHOW = "visualizar"
    const val REFRESH = "atualizar"
    const val DUVIDA = "duvida"
    const val HELP = "ajuda"

    const val UNDERSTOOD = "entendi"
    const val NEXT = "proximo"
}

object Label {
    const val CAMPO = "campo"
    const val CALLBACK = "callback"
    const val FILTRO = "filtro"
    const val SWIPE = "swipe"
    const val TOOLTIP = "tooltip"
    const val ICONE = "icone"
    const val LINK = "link"
    const val TABBAR = "tab bar"
    const val SERVICOS = "servicos"

    const val BOTAO = "botao"
    const val CARD = "card"
    const val HIPERLINK = "hiperlink"
    const val OUTROS = "outros"
    const val OPCOES = "opcoes"
    const val MOTIVO_DO_CANCELAMENTO = "motivo do cancelamento"

    const val TELEFONES = "telefones"
    const val ITEM = "item"
    const val CLIQUE = "clique"
    const val MENSAGEM = "mensagem"
    const val INTERACAO = "interacao"
    const val CHECK_BOX = "checkbox"
    const val ENVIAR = "enviar"
    const val LEADERBOARD = "leaderboard"
    const val RECTANGLE = "rectangle"
    const val HOME = "home"
    const val RECEBIVEIS = "recebiveis"
    const val TAXAS_E_PLANOS = "taxas e panos"

    const val ACESSAR_APP = "acessar o app"

    const val SWIPE_PAGE_X = "swipe pagina %s"
    const val HOME_ARV = "antecipe suas vendas | antecipe agora | %s"
    const val HOME_MEUS_RECEBIMENTOS = "meus recebimentos | %s"

    const val HOME_MEUS_RECEBIMENTOS_PROX_DIA = "dia seguinte"

    const val HOME_MEUS_RECEBIMENTOS_DIA_ANTERIOR = "dia anterior"

    const val BT_MINHAS_VENDAS_INFO = "%s duvida"

    const val VOLTAR_PARA = "voltar para %s"

    const val UNLOCK_CARD_LABEL = "desbloquear cartao"
    const val CRIAR_NOVO_LINK = "criar novo link"

    const val CLOSE_ESTABLISHIMENT_NUMBER_DIALOG = "fechar %s"

    const val ERRO = "erro"
    const val SUCESSO = "sucesso"
    const val INFO = "info"
    const val SUCCESS = "sucesso"

    const val WHATSAPP = "whatsapp"
    const val VIRTUAL_MANAGER = "gerente virtual"
    const val ELIGIBLE = "elegivel"
    const val NOT_ELIGIBLE = "nao elegivel"

    const val SEND = "enviar"
}

object Custom {
    const val DIMENSION_9 = "dimension9"
    const val DIMENSION_5 = "dimension5"
}

object Property {
    const val COMPANY_CODE = "cod_empresa"
    const val USER_CPF = "cod_pessoa"
    const val EC = "estabelecimento"
    const val USER_TYPE = "tipo_usuario"
    const val VIEW_TYPE = "tipo_visualizacao"
}

const val EVENT = "event"
const val EVENT_CATEGORY = "eventCategory"
const val EVENT_ACTION = "eventAction"
const val EVENT_LABEL = "eventLabel"

const val ERROR_LOWERCASE = "error"
const val ERROR_LOCATION = "errorLocation"
const val ERROR_DATA = "Error (code | statusText | service | message)"

const val MAX_LENGTH = 100

object GoogleAnalytics4Events {
    object ScreenView {
        const val SCREEN_VIEW_EVENT = "screen_view"
        const val SCREEN_NAME = "screen_name"
    }

    object PageView {
        const val PAGE_VIEW_EVENT = "page_view"
        const val PAGE_NAME = "page_name"
    }

    object Navigation {
        const val SELECT_CONTENT_EVENT = "select_content"
        const val DISPLAY_CONTENT_EVENT = "display_content"
        const val SELECT_ITEM_EVENT = "display_content"
        const val VIEW_ITEM_EVENT = "view_item"

        const val CONTENT_TYPE = "content_type"
        const val CONTENT_COMPONENT = "content_component"
        const val CONTENT_NAME = "content_name"
        const val FIREBASE_SCREEN = "firebase_screen"
    }

    object Elegible {
        const val IS_ELEGIBLE = "elegivel"
    }

    object Click {
        const val CLICK_EVENT = "click"
        const val CONTENT_VALUE = "content_value"
    }

    object Exception {
        const val EXCEPTION_EVENT = "exception"
        const val DESCRIPTION = "description"
        const val STATUS_CODE = "status_code"
    }

    object Cancel {
        const val ADD_CANCEL_INFO_EVENT = "add_cancel_info"
        const val BEGIN_CANCEL_EVENT = "begin_cancel"
        const val CANCEL_EVENT = "cancel"

        const val CANCELLATION_REASON = "cancellation_reason"
        const val CANCELLATION_REASON_DETAILS = "cancellation_reason_details"
        const val TRANSACTION_TYPE = "transaction_type"

        const val CANCEL_SALES = "cancelar_vendas"
        const val CANCEL_SALE = "cancelar_venda"
        const val CANCELLATIONS = "cancelamentos"
        const val CANCELLATION = "cancelamento"
        const val MY_CANCELLATIONS = "meus_cancelamentos"
    }

    object Share {
        const val SHARE_EVENT = "share"
        const val METHOD = "method"
    }

    object Search {
        const val SEARCH_EVENT = "search"

        const val SEARCH_TERM = "search_term"
    }

    object PaymentAndPurchase {
        const val PURCHASE_EVENT = "purchase"
        const val ADD_PAYMENT_INFO_EVENT = "add_payment_info"
        const val BEGIN_CHECKOUT_EVENT = "begin_checkout"

        const val CURRENCY = "currency"
        const val VALUE = "value"
        const val PRICE = "price"
        const val INSTALLMENT = "installment"
        const val INSTALLMENT_VALUE = "installment_value"
        const val PAYMENT_TYPE = "payment_type"
        const val TAX = "tax"
        const val BANK = "bank"
        const val CARD_NAME = "card_name"
        const val PERIOD_START = "period_start"
        const val PERIOD_END = "period_end"
        const val TRANSACTION_ID = "transaction_id"
        const val TRANSACTION_TYPE = "transaction_type"
        const val ITEM_PROMOTION = "item_promotion"
    }

    object ViewPromotion {
        const val VIEW_PROMOTION_EVENT = "view_promotion"
    }

    object Simulation {
        const val SIMULATE_EVENT = "simulate"
    }

    object Notification {
        const val TITLE = "title"
        const val BELL_ALERT_SHOWN_EVENT = "bell_alert_shown"
        const val BELL_ALERT_SHOWN_CLICK = "bell_alert_click"
        const val QUANTITY = "quantity"
    }

    object UserAndImpersonate {
        const val LOGIN_EVENT = "login"
        const val SIGNUP_EVENT = "sign_up"

        const val STEP = "step"
        const val USER_TYPE = "user_type"
        const val USER_PROFILE = "user_profile"
        const val COMPANY_ID = "company_id"
        const val ESTABLISHMENT_ID = "establishment_id"
        const val COMPANY_VIEW_TYPE = "company_view_type"
    }

    object Other {
        const val ITEMS = "items"

        const val PRODUCT_NAME = "product_name"
        const val PRODUCT_TYPE = "product_type"

        const val FILE_EXTENSION = "file_extension"
        const val FILE_NAME = "file_name"
        const val YEAR = "year"
        const val LINK_TEXT = "link_text"
        const val DIRF_DOWNLOAD = "dirf_download"

        const val OTHERS = "outros"
        const val AUTHORIZATIONS = "autorizacoes"
        const val DEBIT_ACCOUNT = "debito_em_conta"
    }
}

object GoogleAnalytics4Values {
    const val BUTTON = "button"
    const val LINK = "link"
    const val WARNING = "warning"
    const val MESSAGE = "message"
    const val PHONE_NUMBER = "phone_number"
    const val ICON = "icon"
    const val MODAL = "modal"
    const val BRL = "BRL"
    const val SCREEN_VIEW_PREFIX = "/app"
    const val GENERIC_ERROR = "erro_generico"
    const val STEP = "step"

    /*** Contains all the strings that have to be in uppercase during tagging. If not declared here, your string will be lowercase.**/
    val exceptionsUpperCase =
        listOf(
            BRL,
        )
}
