package br.com.mobicare.cielo.recebaMais.domains.entities

enum class InstallmentStatus(val code: Int, val text: String) {
    PAY_INSTALLMENT(0,"Valor pagamento maior que a parcela"),
    PAY_TICKET(1,"Pago via boleto"),
    PAY_RECEBA_MAIS(2,"Pago via receba mais"),
    OPENED(3,"Em aberto"),
    UNKNOWN(4,"Desconhecido");
}