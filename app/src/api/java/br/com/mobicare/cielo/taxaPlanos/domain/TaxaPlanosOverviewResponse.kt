package br.com.mobicare.cielo.taxaPlanos.domain

data class TaxaPlanosOverviewResponse (
        val merchant : String,
        val value: Double,
        val minimumRevenue: Double,
        val settlementTerm: TaxaPlanosDays,
        val mdr: TaxaPlanosTaxas
)