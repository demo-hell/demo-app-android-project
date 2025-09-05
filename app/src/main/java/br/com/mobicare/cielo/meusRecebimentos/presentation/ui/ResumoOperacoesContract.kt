package br.com.mobicare.cielo.meusRecebimentos.presentation.ui

import android.view.Menu
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.GroupedPostings

/**
 * Created by silvia.miranda on 03/07/2017.
 */
interface ResumoOperacoesContract {


        interface View: IAttached {
            fun showProgress()
            fun hideProgress()

            fun loadResumo(postings: ArrayList<GroupedPostings>, grouped: Boolean, helpText: String)
            fun appendResumo(postings: ArrayList<GroupedPostings>, grouped: Boolean, helpText: String)
            fun logout(error: ErrorMessage)
            fun error(error: ErrorMessage)
            fun onCreateOptionsMenu(menu: Menu): Boolean
            fun addScrollEvent()
            fun removeScrollEvent()
        }

        interface Presenter{
            fun callAPI(id: String?, resumoQuantity: String?, resumoCieloDate: String?, finalDate: String?)
            fun onStart()
            fun onFinish()
            fun onError(error: ErrorMessage)
        }
}