package br.com.mobicare.cielo.meusrecebimentosnew.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.databinding.LayoutSummaryViewItemBinding
import br.com.mobicare.cielo.meusrecebimentosnew.adapter.viewholder.DetailViewOfMyReceiptsViewHolder
import br.com.mobicare.cielo.meusrecebimentosnew.models.detailsummaryview.Receivable

class DetailViewOfMyReceiptsAdapter(
    private var items: ArrayList<Receivable>
) : RecyclerView.Adapter<DetailViewOfMyReceiptsViewHolder>() {

    private var isLoading: Boolean = false
    private var endOfTheList: Boolean = false
    private var bindViewHolderCallback: OnBindViewHolder<Receivable>? = null
    var onItemClickListener: DefaultViewListAdapter.OnItemClickListener<Receivable>? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailViewOfMyReceiptsViewHolder {
        val binding =
            LayoutSummaryViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewOfMyReceiptsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewOfMyReceiptsViewHolder, position: Int) {
        val item = items[position]
        this.bindViewHolderCallback?.bind(item, holder)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateDataSet(newItems: List<Receivable>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    interface OnBindViewHolder<T> {
        fun bind(item: T, holder: DetailViewOfMyReceiptsViewHolder)
    }

    fun setBindViewHolderCallback(callback: OnBindViewHolder<Receivable>) {
        this.bindViewHolderCallback = callback
    }

    fun setEndOfTheList(isEnd: Boolean) {
        this.endOfTheList = isEnd
    }

    fun addMoreInList(list: List<Receivable>) {
        val currentSize = this.items.size
        this.items.addAll(list)
        this.isLoading = false
        notifyItemRangeInserted(currentSize, list.size)
    }
}