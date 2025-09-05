package br.com.mobicare.cielo.meusrecebimentosnew.fragments

import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

interface FilterVisaoDetalhadaContact {
    fun onCleanFilter(quickFilter: QuickFilter)
    fun onFilterSelected(quickFilter: QuickFilter)
}