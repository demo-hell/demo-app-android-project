package br.com.mobicare.cielo.accessManager.foreignUsers

import br.com.mobicare.cielo.accessManager.model.ForeignUsersItem
import br.com.mobicare.cielo.commons.presentation.BaseView

class AccessManagerForeignListContract {
    interface View : BaseView {
        fun onForeignUserClicked(user: ForeignUsersItem)
    }

    interface Presenter {
        fun onPause()
        fun onResume()
    }
}