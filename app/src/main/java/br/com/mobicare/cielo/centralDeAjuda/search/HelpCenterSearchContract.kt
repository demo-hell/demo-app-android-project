package br.com.mobicare.cielo.centralDeAjuda.search

import br.com.mobicare.cielo.centralDeAjuda.presentation.domain.FrequentQuestionsModelView
import br.com.mobicare.cielo.commons.presentation.BaseView

interface HelpCenterSearchContract {

    interface View : BaseView {
        fun onSearchResult(list: List<FrequentQuestionsModelView>)
    }

    interface Presenter {
        fun search(term: String = "")
        fun onPause()
        fun onResume()
    }
}