package br.com.mobicare.cielo.mySales.presentation.ui.adapter

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListener(
    private val linearLayoutManager: LinearLayoutManager): RecyclerView.OnScrollListener() {

    abstract fun isLastPage(): Boolean
    abstract fun isLoading(): Boolean
    abstract fun loadMoreItems()

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = linearLayoutManager.childCount
        val totalItemCount = linearLayoutManager.itemCount
        val firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()

        if (!isLoading() && !isLastPage()) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                loadMoreItems()
            }
        }
    }
}

fun recyclerViewOnScrollListener(
    recyclerView: RecyclerView,
    linearLayoutManager: LinearLayoutManager?,
    isLastPageListener: () -> Boolean,
    isLoadingListener: () -> Boolean,
    loadMoreItems: () -> Unit
    ) {

    if (linearLayoutManager != null) {
        recyclerView.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager){
            override fun isLastPage(): Boolean {
                return isLastPageListener.invoke()
            }

            override fun isLoading(): Boolean {
                return isLoadingListener.invoke()
            }

            override fun loadMoreItems() {
                return loadMoreItems.invoke()
            }

        })
    }

}
