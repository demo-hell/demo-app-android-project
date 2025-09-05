package br.com.mobicare.cielo.meusrecebimentosnew.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.databinding.LayoutSummaryViewItemBinding
import br.com.mobicare.cielo.meusrecebimentosnew.adapter.viewholder.SummaryViewMyReceiptsViewHolder
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.Item

class SummaryViewMyReceiptsAdapter(
    private var items: ArrayList<Item>,
) : RecyclerView.Adapter<SummaryViewMyReceiptsViewHolder>() {


    private var isLoading: Boolean = false
    private var endOfTheList: Boolean = false
    private var distanteToHide: Int? = null
    private var bindViewHolderCallback: OnBindViewHolder<Item>? = null
    var onItemClickListener: AdapterView.OnItemClickListener? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SummaryViewMyReceiptsViewHolder {
        val binding =
            LayoutSummaryViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SummaryViewMyReceiptsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SummaryViewMyReceiptsViewHolder, position: Int) {
        val item = items[position]
        this.bindViewHolderCallback?.bind(item, holder)
    }

    fun setDistanceToHide(distance: Int) {
        this.distanteToHide = distance
    }

    fun setNewDataList(list: List<Item>) {
        val currentSize: Int = this.items.size
        this.items = list as ArrayList<Item>

        notifyItemRangeRemoved(ZERO, currentSize)
        notifyItemRangeInserted(ZERO, list.size)
    }

    fun addMoreInList(list: List<Item>) {
        val currentSize = this.items.size
        this.items.addAll(list)
        this.isLoading = false
        notifyItemRangeInserted(currentSize, list.size)
    }

    fun setEndOfTheList(isEnd: Boolean) {
        this.endOfTheList = isEnd
    }

    interface OnBindViewHolder<T> {
        fun bind(items: T, holder: SummaryViewMyReceiptsViewHolder)
    }

    fun setBindViewHolderCallback(callback: OnBindViewHolder<Item>) {
        this.bindViewHolderCallback = callback
    }
}