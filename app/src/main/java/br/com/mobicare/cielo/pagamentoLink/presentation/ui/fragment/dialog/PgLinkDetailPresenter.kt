package br.com.mobicare.cielo.pagamentoLink.presentation.ui.fragment.dialog

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pagamentoLink.domains.DeleteLink


class PgLinkDetailPresenter(val reporitory: PgLinkDetailReporitory) : PgLinkDetailBottomSheet.OnPgLinckListener {

    override fun deleteLink(deleteLink: DeleteLink, callback: (Int)->Unit) {
        //callback(500)
        val token: String? = UserPreferences.getInstance().token

        token?.let {
            reporitory.deleteLink(it, deleteLink, object : APICallbackDefault<Int, String> {

                override fun onError(error: ErrorMessage) {
                        callback(error.httpStatus)
                }

                override fun onSuccess(response: Int) {
                    callback(response)
                }
            })

        }

    }
}