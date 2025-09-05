package br.com.mobicare.cielo.pagamentoLink.orders

enum class DeliveryType constructor(type: String) {

    CORREIOS("CORREIOS"),
    LOGGI("LOGGI"),
    FIXED_AMOUNT("FIXED_AMOUNT"),
    WITHOUT_SHIPPING("WITHOUT_SHIPPING")
}