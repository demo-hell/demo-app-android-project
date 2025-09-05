package br.com.mobicare.cielo.extrato.presentation.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoStatusDef
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTransicaoObj
import kotlinx.android.synthetic.main.item_extrato_time_line.view.*


class TimeLineAdapter(var resourceLayourt: Int, var context: Context,
                      var list: List<ExtratoTransicaoObj>?,
                      var listener: OnClickExtratoItemListener? = null,
                      val onlyVisualization: Boolean = false) : androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0
    var mIsLoading: Boolean = false

    override fun getItemViewType(position: Int): Int {
        return if ((position + 1) > list?.size!!) VIEW_PROG else VIEW_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        val vh: androidx.recyclerview.widget.RecyclerView.ViewHolder
        if (viewType == VIEW_ITEM) {
            val view = LayoutInflater.from(context).inflate(resourceLayourt, parent, false)
            vh = DefaultViewHolderKotlin(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_progress, parent, false)
            vh = DefaultViewHolderKotlin(view)
        }
        return vh
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        if (list == null || itemCount == 0) {
            return
        }

        val item = getItem(position)
        if (item != null) {
            holder.mView.textview_item_extrato_timeline_hour?.text = item.time
            holder.mView.textview_item_extrato_timeline_value?.text = item.amount
            holder.mView.textview_item_extrato_timeline_payment_type?.text = item.description
            holder.mView.textview_item_extrato_timeline_status?.text = item.status


            if (onlyVisualization) {
                holder.mView.imageview_item_extrato.gone()
            } else {
                listener.let {
                    holder.mView.layout_item_extrato_time_line?.setOnClickListener { listener?.onClickItem(item) }
                }
            }

            managerStatus(holder, item)
            managerItem(holder, position)
        }
    }

    fun managerStatus(holder: DefaultViewHolderKotlin, item: ExtratoTransicaoObj) {
        var status:Int?= null

        when{
            (item.statusCode.equals("AP"))->{status = 1}
            (item.statusCode.equals("NE"))->{status = 2}
            (item.statusCode.equals("AT"))->{status = 3}
            (item.statusCode.equals("CA"))->{status = 4}
            (item.statusCode.equals("1"))->{status = 1}
            (item.statusCode.equals("2"))->{status = 2}
        }

        when (status) {
            ExtratoStatusDef.APROVADA -> statusAprovado(holder)
            ExtratoStatusDef.NEGADA -> statusNegado(holder)
            ExtratoStatusDef.CANCELADA -> statusCancelado(holder)
            ExtratoStatusDef.ATUALIZAR -> statusAtualizar(holder)
            else -> statusAtualizar(holder)
        }
    }

    fun statusAprovado(holder: DefaultViewHolderKotlin) {
        holder.mView.imageview_item_extrato_timeline_circle?.setBackgroundResource(R.drawable.circle_green)
        holder.mView.textview_item_extrato_timeline_status?.setTextColor(ContextCompat.getColor(context, R.color.green))
    }

    fun statusNegado(holder: DefaultViewHolderKotlin) {
        holder.mView.imageview_item_extrato_timeline_circle.setBackgroundResource(R.drawable.circle_red)
        holder.mView.textview_item_extrato_timeline_status.setTextColor(ContextCompat.getColor(context, R.color.red))
    }

    fun statusAtualizar(holder: DefaultViewHolderKotlin) {
        holder.mView.imageview_item_extrato_timeline_circle.setBackgroundResource(R.drawable.circle_purple)
        holder.mView.textview_item_extrato_timeline_status.setTextColor(context.resources.getColor(R.color.purple))
    }

    fun statusCancelado(holder: DefaultViewHolderKotlin) {
        holder.mView.imageview_item_extrato_timeline_circle.setBackgroundResource(R.drawable.circle_gray)
        holder.mView.textview_item_extrato_timeline_status.setTextColor(context.resources.getColor(R.color.gray_light))
    }

    fun getItem(position: Int): ExtratoTransicaoObj? {
        return if ((position + 1) > list!!.size) null else list!!.get(position)
    }

    fun managerItem(holder: DefaultViewHolderKotlin, position: Int) {
        if (position == (itemCount - 1)) {
            holder.mView.view_item_extrato_time_line_vertical?.visibility = View.GONE
            holder.mView.view_item_extrato_separator?.visibility = View.GONE
            holder.mView.view_item_extrato_time_line_top_vertical?.visibility = View.VISIBLE
        } else if (position == 0) {
            holder.mView.view_item_extrato_time_line_top_vertical?.visibility = View.INVISIBLE
            holder.mView.view_item_extrato_time_line_vertical?.visibility = View.VISIBLE
            holder.mView.view_item_extrato_separator?.visibility = View.VISIBLE
        } else {
            holder.mView.view_item_extrato_time_line_top_vertical?.visibility = View.VISIBLE
            holder.mView.view_item_extrato_time_line_vertical?.visibility = View.VISIBLE
            holder.mView.view_item_extrato_separator?.visibility = View.VISIBLE
        }
    }

    fun appendList(newList: ArrayList<ExtratoTransicaoObj>) {
        (this.list!! as ArrayList).addAll(newList)
    }

    fun showLoading() {
        mIsLoading = true
        notifyDataSetChanged()
    }

    fun hideLoading() {
        mIsLoading = false
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return if (mIsLoading) list!!.size + 1 else list!!.size
    }

    interface OnClickExtratoItemListener {
        fun onClickItem(item: ExtratoTransicaoObj)
    }
}
