package br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.addAccount.transferAccount

enum class AddAccountErrorType(error: String?) {
    INVALID_PREPAID_BANK("Conta Digital informada inválida."),
    INVALID_DOMICILE("Banco Domicílio inválido."),
    CLOSED_FOR_FRAUD("Estabelecimento fechado por fraude."),
    ALREADY_EXISTIS("Domicílio bancário já existe para outro CNPJ."),
    INVALID_COMPOSITION("Composição do domicílio é inválida."),
    INVALID_MERCHANT_REQUEST("Solicitação só deve ser feita para o CNPJ Matriz."),
    DOCUMENT_REQUIRED("Número do cliente ou CPF/CNPJ é obrigatório"),
    INVALID_DOCUMENT("Número do CPF ou CNPJ informado é inválido"),
    INVALID_BANK_CODE("Código do banco não informado"),
    INVALID_COMPANY_NAME("Nome da Pessoa Física ou Razão Social não foi informado"),
    INVALID_AGENCY_NUMBER("Número da agência não foi informado"),
    INVALID_ACCOUNT_NUMBER("Número da conta não foi informado"),
    INVALID_ACCOUNT_DIGIT("Dígito da conta não foi informado"),
    INVALID_ACCOUNT_TYPE("Tipo da conta não foi informado"),
    INVALID_CHANNEL("Canal solicitante não foi informado"),
    INVALID_PERSON_TYPE("Tipo de pessoa inválido."),
    UNKNOWN_VALIDATION_REASON("Motivo Validação não foi informado"),
    UNKNOWN_VALIDATION_TYPE("Tipo Validação não foi informado");

    companion object {
        fun fromString(type: String) = values().find { it.name == type }?.name
    }
}