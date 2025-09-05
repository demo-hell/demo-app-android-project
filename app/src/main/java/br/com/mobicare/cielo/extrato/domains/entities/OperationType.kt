package br.com.mobicare.cielo.extrato.domains.entities

enum class OperationType(val operationSymbol: String,
                         val description: String) {

    DEBIT("D", "Débito"), CREDIT("C",
            "Crédito");



    companion object {
        fun ofOperationSymbol(operationSymbol: String): OperationType? {
            return when (operationSymbol) {
                CREDIT.operationSymbol -> CREDIT
                DEBIT.operationSymbol -> DEBIT
                else -> null
            }
        }
    }

}