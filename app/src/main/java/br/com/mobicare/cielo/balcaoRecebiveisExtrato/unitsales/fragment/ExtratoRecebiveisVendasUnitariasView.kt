package br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.fragment

import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.ExtratoRecebiveisVendasUnitariasUseCase
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

interface ExtratoRecebiveisVendasUnitariasView {

    fun onSuccess(response: ExtratoRecebiveisVendasUnitariasUseCase)
    fun onError(error: ErrorMessage)
    fun showLoading()
    fun hideLoading()
    fun showLoadingMore()
    fun hideLoadingMore()
    fun logScreenView(screen: String)
    fun logException(screenName: String, error: NewErrorMessage)
}