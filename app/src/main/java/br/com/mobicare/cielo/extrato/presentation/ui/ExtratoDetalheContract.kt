package br.com.mobicare.cielo.extrato.presentation.ui

import androidx.annotation.ColorRes
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.SystemMessage
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.extrato.domains.entities.extratoRecibo.ExtratoReciboObj


interface ExtratoDetalheContract{
    interface View : IAttached {
        fun showProgress()
        fun hideProgress()
        fun showError(error: ErrorMessage)
        fun loadRecibo(obj: ExtratoReciboObj?)
        fun loadList(list: ArrayList<SystemMessage>?)
        fun loadStatus(status: String?, @ColorRes color: Int )
    }

    interface Presenter {
        fun checkStatus()
        fun loadRecibo(salesCode: String, salesDate: String)
    }
}
