package br.com.mobicare.cielo.pagamentoLink.presentation.ui.listAtivos

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pagamentoLink.PagamentoLinkRespository
import br.com.mobicare.cielo.pagamentoLink.domain.PaginationPaymentLink
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLinkResponse

private const val SIZE = 25

class PagamentoLinkListAtivosPresenter(
    private val respository: PagamentoLinkRespository
) : PagamentoLinkListAtivosContract.Presenter {

    private lateinit var mView: PagamentoLinkListAtivosContract.View
    private var mPagination: PaginationPaymentLink? = null
    private var mPage = 1
    private var isLoadingMore: Boolean = false

    override fun loadListAtivosResumo(isShowLoading: Boolean) {
        mPage = 1
        val token: String? = UserPreferences.getInstance().token
        token?.let {
            if (isShowLoading) {
                mView.showLoading()
            }

            callPaymentLinkActivity(it = it, page = 1) { response ->
                mView.showLastLinks(response.items)
                mView.hideLoading()
                mView.hideLoadingSwipeToRefresh()
            }
        }
    }

    private fun isNextPage(): Boolean {
        mPagination?.let {
            if (it.pageNumber < it.numPages) {
                mPage = it.pageNumber + 1
            } else {
                return false
            }
        }
        return true
    }

    override fun loadListAtivos(isCheckNextPage: Boolean) {

        if (isCheckNextPage && !isNextPage()) return

        val token: String? = UserPreferences.getInstance().token
        token?.let {
            mView.showLoading()

            callPaymentLinkActivity(it = it, page = mPage) { response ->
                mPagination = response.pagination

                if (response.items.isEmpty()) {
                    mView.showEmptyLinks()
                } else {
                    mView.showListAtivos(response.items)
                }

                mView.hideLoading()
            }
        }
    }

    private fun callPaymentLinkActivity(
        it: String, page: Int,
        callFunctions: (response: PaymentLinkResponse) -> Unit
    ) {
        respository.paymentLinkActivity(token = it, size = SIZE, page = page, callback =
        object : APICallbackDefault<PaymentLinkResponse, String> {
            override fun onError(error: ErrorMessage) {
                when {
                    error.logout -> {}
                    error.httpStatus >= 500 -> mView.showSubmit(error)
                    else -> mView.showError(error)
                }
                this@PagamentoLinkListAtivosPresenter.isLoadingMore = false
            }

            override fun onSuccess(response: PaymentLinkResponse) {
                mPagination = response.pagination
                callFunctions(response)
                this@PagamentoLinkListAtivosPresenter.isLoadingMore = false
            }

        })
    }

    override fun resubmit() {
        loadListAtivos(isCheckNextPage = false)
    }

    override fun onCleared() {
        respository.disposable()
    }


    override fun setView(view: PagamentoLinkListAtivosContract.View) {
        mView = view
    }

    override fun onSwipeRefresh() {
        mView.setupSwiperToRefresh()
    }

    override fun loadMore() {
        if (!this.isLoadingMore) {
            this.isLoadingMore = true
            this.loadListAtivos()
        }
    }

}