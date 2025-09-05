package br.com.mobicare.cielo.extrato.presentation.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.extrato.domains.entities.extratoListaAcumulada.ExtratoListaTransicaoObj
import kotlinx.android.synthetic.main.item_extrato_list.view.*

/**
 * Created by benhur.souza on 06/06/2017.
 */
class ExtratoListaAdapter(var context: Context, var list: ArrayList<ExtratoListaTransicaoObj>?, var listener: OnClickExtratoItemListener ): androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>(){

    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0

    var mIsLoading: Boolean = false

    override fun getItemViewType(position: Int): Int {
        return if ((position+1) > list?.size!!) VIEW_PROG else VIEW_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        val vh: androidx.recyclerview.widget.RecyclerView.ViewHolder
        if (viewType == VIEW_ITEM) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_extrato_list, parent, false)
            vh = DefaultViewHolderKotlin(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_progress, parent, false)
            vh = DefaultViewHolderKotlin(view)
        }
        return vh
    }

//    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DefaultViewHolderKotlin {
//        val view = LayoutInflater.from(context).inflate(R.layout.item_extrato_list, parent, false)
//        return DefaultViewHolderKotlin(view)
//    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        if(list == null || itemCount == 0){
            return
        }

        val item = getItem(position)
        if (item != null) {
            holder.mView.layout_item_extrato_list?.setOnClickListener {
                if (item.hasSales) {
                    listener.onClickItem(item)
                }
            }

            holder.mView.textview_item_extrato_list_date?.text = item.date
            holder.mView.textview_item_extrato_list_quantity?.text = context.resources.getQuantityString(R.plurals.extrato_quantity, item.quantity, String.format("%02d", item.quantity))
            holder.mView.textview_item_extrato_list_value?.text = item.amount
            if (item.hasSales) {
                holder.mView.imageview_item_list?.visibility = View.VISIBLE
            } else {
                holder.mView.imageview_item_list?.visibility = View.INVISIBLE
            }
        }

    }

    fun showLoading() {
        mIsLoading = true
        notifyDataSetChanged()
    }

    fun hideLoading() {
        mIsLoading = false
        notifyDataSetChanged()
    }

    fun appendList(newList: ArrayList<ExtratoListaTransicaoObj>) {
        this.list!!.addAll(newList)
    }

    fun getItem(position: Int): ExtratoListaTransicaoObj? {
        return if ((position+1) > list!!.size ) null else list!!.get(position)
    }

    override fun getItemCount(): Int {
        return if (mIsLoading)  list!!.size + 1 else list!!.size
    }

    interface OnClickExtratoItemListener{
        fun onClickItem(item: ExtratoListaTransicaoObj)
    }
}
