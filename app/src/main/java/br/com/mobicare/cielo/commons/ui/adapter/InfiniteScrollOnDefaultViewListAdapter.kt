package br.com.mobicare.cielo.commons.ui.adapter

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper

class InfiniteScrollOnDefaultViewListAdapter<T>(
    list: ArrayList<T>,
    @LayoutRes layoutResId: Int = R.layout.item_list_text_line
) : DefaultViewListAdapter<T>(list, layoutResId) {

    private var isLoading: Boolean = false
    private var endOfTheList: Boolean = false
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var onLoadNextPageListener: OnLoadNextPageListener? = null
    private var onScrollListener: OnScrollListener? = null
    private var distanteToHide: Int? = null
    private var scrollDist: Int = 0
    private var isVisible: Boolean = true


    val viewBinderHelper: ViewBinderHelper = ViewBinderHelper()

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        super.onBindViewHolder(holder, position)

        val swipeRevealLayout = holder.mView
            .findViewById<SwipeRevealLayout>(R.id.swipeRevealUserSells)

        swipeRevealLayout?.run {
            if (list.isNotEmpty() && list.size > position)
                viewBinderHelper.bind(this, list[position].toString())
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                try {
                    super.onScrolled(recyclerView, dx, dy)
                    checkVisibilityOutsideView(dx, dy)
                    recyclerView.layoutManager?.let { itLayoutManager ->
                        val visibleItemCount = itLayoutManager.childCount
                        val totalItemCount = itLayoutManager.itemCount
                        val firstVisibleItemPosition =
                            (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                        val isRefreshing =
                            swipeRefreshLayout != null && swipeRefreshLayout?.isRefreshing == true

                        if (!isLoading && !isRefreshing && !endOfTheList) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition > 0) {
                                isLoading = true
                                onLoadNextPageListener?.onLoadNextPage()
                            }
                        }
                    }
                } catch (error: Throwable) {
                }
            }

            private fun checkVisibilityOutsideView(dx: Int, dy: Int) {
                this@InfiniteScrollOnDefaultViewListAdapter.distanteToHide?.let {
                    if (isVisible && scrollDist > it) {
                        this@InfiniteScrollOnDefaultViewListAdapter.onScrollListener?.onHide(it)
                        scrollDist = 0
                        isVisible = false
                    } else if (!isVisible && scrollDist < -it) {
                        this@InfiniteScrollOnDefaultViewListAdapter.onScrollListener?.onShow(it)
                        scrollDist = 0
                        isVisible = true
                    }

                    if ((isVisible && dy > 0) || (!isVisible && dy < 0)) {
                        scrollDist += dy
                    }
                }
            }
        })
    }

    fun attachSwipeLayout(swipeRefreshLayout: SwipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout
    }

    fun setEndOfTheList(isEnd: Boolean) {
        this.endOfTheList = isEnd
    }

    fun setOnLoadNextPageListener(listener: OnLoadNextPageListener) {
        this.onLoadNextPageListener = listener
    }

    fun setOnScrollListener(listener: OnScrollListener) {
        this.onScrollListener = listener
    }

    fun setDistanceToHide(distance: Int) {
        this.distanteToHide = distance
    }

    fun addMoreInList(list: List<T>) {
        val currentSize = this.list.size
        this.list = this.list.plus(list)
        this.isLoading = false
        notifyItemRangeInserted(currentSize, list.size)
    }

    override fun setNewDataSet(list: List<T>) {
        this.isLoading = false
        this.endOfTheList = false
        scrollDist = 0
        this.setNewDataList(list)
    }

    interface OnLoadNextPageListener {
        fun onLoadNextPage()
    }

    interface OnScrollListener {
        fun onShow(size: Int)
        fun onHide(size: Int)
    }

}