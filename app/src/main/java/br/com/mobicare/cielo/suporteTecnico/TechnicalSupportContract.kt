package br.com.mobicare.cielo.suporteTecnico

import androidx.appcompat.app.AppCompatActivity
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.suporteTecnico.domain.entities.SupportItem

/**
 * Created by marcosj on 12/19/17.
 */
interface TechnicalSupportContract {

    interface View : IAttached {

        fun loadTechnicalSupportItems(support: List<SupportItem>)
        fun systemError(error: ErrorMessage)
        fun userError(error: ErrorMessage)

        fun showLoading()
        fun hideLoading()

    }

    interface Presenter {

        fun loadItems()
        fun loadView(activity: AppCompatActivity)
    }

}