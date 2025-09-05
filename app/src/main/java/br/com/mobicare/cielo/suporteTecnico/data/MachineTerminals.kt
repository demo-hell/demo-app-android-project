package br.com.mobicare.cielo.suporteTecnico.data

data class MachineTerminals(
    val model: String? = null,
    val logicalNumber: String? = null,
    val logicalNumberDigit: String? = null,
    val rentalAmount: Double? = null,
    val name: String? = null,
    val description: String? = null,
    val technology: String? = null,
    val replacementAllowed: Boolean? = null,
    var selectedItem: Boolean = false
)
