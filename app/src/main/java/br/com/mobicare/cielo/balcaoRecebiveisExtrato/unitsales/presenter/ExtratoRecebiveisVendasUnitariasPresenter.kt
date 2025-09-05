package br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.presenter

import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Item
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter

interface ExtratoRecebiveisVendasUnitariasPresenter {

    fun onCreate(negotiation: Item,
                 quickFilter: QuickFilter? = null)

    fun getUnitReceivable(
        page: Int = 1,
        isLoadingMore: Boolean = false)
    fun onResume(negotiationType: Int?)
    fun onPause()
    fun trackException(negotiationType: Int?, error: ErrorMessage)
}