package br.com.mobicare.cielo.commons.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.extensions.gone
import kotlinx.android.synthetic.main.layout_item_select_bottom_sheet.view.*

open class DefaultViewListAdapter<T>(
    protected var list: List<T>,
    @LayoutRes protected val layoutResId: Int =
        R.layout.item_list_text_line
) :
    RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    private var bindViewHolderCallback: OnBindViewHolder<T>? = null
    private var bindHolderPosition: OnBindViewHolderPositon<T>? = null
    private var bindItemViewTypeCallBack: OnBindItemViewType<T>? = null

    var onItemClickListener: OnItemClickListener<T>? = null

    interface OnItemClickListener<T> {
        fun onItemClick(item: T)
    }

    fun setBindViewHolderCallback(callback: OnBindViewHolder<T>) {
        this.bindViewHolderCallback = callback
    }

    fun setBindViewHolderCallback(callback: OnBindViewHolderPositon<T>) {
        this.bindHolderPosition = callback
    }

    fun setBindItemViewType(callback: OnBindItemViewType<T>) {
        this.bindItemViewTypeCallBack = callback
    }

    fun setNewDataList(list: List<T>) {
        val currentSize: Int = this.list.size
        this.list = list

        notifyItemRangeRemoved(ZERO, currentSize)
        notifyItemRangeInserted(ZERO, list.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<T>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun removeItem(index: Int?) {
        index?.let {
            this.list = list.toMutableList().apply {
                removeAt(it)
            }
            this.notifyItemRemoved(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return DefaultViewHolderKotlin(view)
    }

    override fun getItemCount(): Int {
        return if (this.list.isEmpty()) ZERO else this.list.size
    }

    private fun getLastPosition(): Int {
        return if (this.list.isEmpty()) ZERO else this.list.size - 1
    }

    override fun getItemViewType(position: Int): Int {
        this.bindItemViewTypeCallBack?.let {
            return it.onBind(position, this.list[position])
        } ?: return this.layoutResId
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val item: T = this.list[position]
        this.bindViewHolderCallback?.onBind(item, holder)
        this.onItemClickListener?.let { itListener ->
            holder.mView.setOnClickListener {
                itListener.onItemClick(item)
            }
        }

        if (item == this.list.last()) holder.mView.dividerView.gone()

        this.bindHolderPosition?.onBind(item, holder, position, getLastPosition())
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnBindViewHolder<T> {
        fun onBind(item: T, holder: DefaultViewHolderKotlin)
    }

    interface OnBindViewHolderPositon<T> {
        fun onBind(item: T, holder: DefaultViewHolderKotlin, position: Int, lastPositon: Int)
    }

    interface OnBindItemViewType<T> {
        fun onBind(position: Int, item: T): Int
    }

    open fun setNewDataSet(list: List<T>) {}
}

