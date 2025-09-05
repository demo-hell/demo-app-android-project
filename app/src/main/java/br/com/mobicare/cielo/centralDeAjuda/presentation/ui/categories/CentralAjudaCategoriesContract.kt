package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.categories

import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.HelpCategory
import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached

interface CentralAjudaCategoriesContract {

    interface View : BaseView, IAttached {
        fun showFaqCategories(faqList: List<HelpCategory>)
    }

    interface Presenter : BasePresenter<View> {
        fun onCleared()
        fun loadFaqCategories(imageType: String, accessToken: String)
    }
}