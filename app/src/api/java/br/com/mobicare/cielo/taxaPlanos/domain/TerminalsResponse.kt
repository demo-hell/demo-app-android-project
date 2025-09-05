package br.com.mobicare.cielo.taxaPlanos.domain

data class TerminalsResponse (
        val rentalEquipments: Boolean?,
        val terminals: List<TaxaPlanosMachine>
)