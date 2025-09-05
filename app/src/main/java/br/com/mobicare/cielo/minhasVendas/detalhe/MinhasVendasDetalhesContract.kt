package br.com.mobicare.cielo.minhasVendas.detalhe

import androidx.annotation.ColorRes
import br.com.mobicare.cielo.commons.domains.entities.ItemDetalhesVendas
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.recebaMais.domain.UserOwnerResponse

interface MinhasVendasDetalhesContract {
    interface View {
        fun populateDetail(items: ArrayList<ItemDetalhesVendas>)
        fun loadStatus(status: String?, @ColorRes color: Int)
        fun populateNote(sale: Sale)
        fun merchantResponse(response: UserOwnerResponse)
        fun onError()
        fun setupCancelSale(isShow: Boolean, isEnabled: Boolean)
    }

    interface Presenter {
        fun load(sale: Sale, fingerprint: String)
    }
}