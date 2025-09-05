package br.com.mobicare.cielo.pedidos.tracking.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.isoDateToBrHourAndMinute
import br.com.mobicare.cielo.commons.utils.isoDateWithMiliSecToBr
import br.com.mobicare.cielo.extensions.gone
import kotlinx.android.synthetic.main.item_tracking_order.view.*

class TrakingOrderAdapter(val context: Context, private val tracking: Tracking?) : RecyclerView.Adapter<TrakingOrderAdapter.TrakingOrderViewHolder>() {

    companion object {
        private const val FAILED = "FAILED"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrakingOrderViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_tracking_order, parent, false)
        return TrakingOrderViewHolder(view)
    }

    override fun getItemCount() = tracking?.steps?.size ?: 0

    override fun onBindViewHolder(holder: TrakingOrderViewHolder, position: Int) {
        val step = tracking?.steps?.get(position)
        val isFirst = position == 0
        val isLast = position == itemCount - 1

        holder.itemView.textViewStatus.text = step?.description
        holder.itemView.textViewStatusTime.text = "${step?.lastUpdated
                ?.isoDateWithMiliSecToBr()}\n${step?.lastUpdated
                ?.isoDateToBrHourAndMinute()}"

        if (isFirst) holder.itemView.viewLineStart.gone()
        else if (isLast) {
            holder.itemView.viewLineEnd.gone()
            holder.itemView.imageViewStatus.setImageResource(R.drawable.ic_tracking_order_current)
        }
        if (tracking?.status.equals(FAILED) && isLast) {
            holder.itemView.textViewStatus.setTextColor(ContextCompat
                    .getColor(context, R.color.red_DC392A))
            holder.itemView.imageViewStatus.setImageResource(R.drawable.ic_tracking_error)
        }
    }

    class TrakingOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}