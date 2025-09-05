package br.com.mobicare.cielo.autoAtendimento.presentation.presenter

import br.com.mobicare.cielo.autoAtendimento.domain.model.Supply
import br.com.mobicare.cielo.commons.data.DisposableDefault
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.migration.presentation.presenter.ItemBannerMigration
import br.com.mobicare.cielo.migration.presentation.presenter.ItemPraVenderMais

interface AutoAtendimentoContract{

    interface View {
        fun responseListSuplies(supplies: List<Supply>){}
        fun selectItem(items: ItemBannerMigration){}
        fun selectItemPraVenderMais(itemObj: ItemPraVenderMais){}
        fun errorResponse(hashCode: Throwable){}
    }

    interface Presenter {
        fun setView(view: View)
        fun loadSuplies()
        fun responseListSuplies(supplies: List<Supply>)
        fun errorResponse(e: Throwable)
    }
    interface Repository : DisposableDefault {
        fun loadSuplies(accessToken: String, authoziration: String)
        fun callBack(callback: AutoAtendimentoContract.Presenter)
        fun loadSupplies(accessToken: String, authoziration: String, callback: APICallbackDefault<List<Supply>, String>) {}
    }

}