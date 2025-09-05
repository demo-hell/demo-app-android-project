package br.com.mobicare.cielo.recebaRapido.cancellation.comparationscreen

import br.com.mobicare.cielo.taxaPlanos.mapper.ComparationViewModelRR

interface CancelattionRecebaRapidoComparationView {

    fun onTaxAndBrandSuccess(comparationModel: ComparationViewModelRR)
    fun onTaxAndBrandError()
    fun showLoading()
}