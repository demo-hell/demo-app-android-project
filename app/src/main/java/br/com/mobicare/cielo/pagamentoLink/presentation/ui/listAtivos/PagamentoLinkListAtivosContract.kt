package br.com.mobicare.cielo.pagamentoLink.presentation.ui.listAtivos

import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.BasePresenter
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLink

interface PagamentoLinkListAtivosContract {

    interface Presenter : BasePresenter<View> {
        fun loadListAtivos(isCheckNextPage: Boolean = true)
        fun loadListAtivosResumo(isShowLoading: Boolean = true)
        fun onCleared()
        fun resubmit()
        fun onSwipeRefresh()
        fun loadMore()
    }

    interface View : BaseView, IAttached {
        fun showListAtivos(itens: List<PaymentLink>)
        fun showLastLinks(items: List<PaymentLink>)
        fun showEmptyLinks()
        fun showSubmit(error: ErrorMessage)
        fun setupSwiperToRefresh(){}
        fun hideLoadingSwipeToRefresh(){}
    }

}