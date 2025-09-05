package br.com.mobicare.cielo.suporteTecnico.ui.adapter

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.suporteTecnico.domain.entities.SupportItem


class TechnicalSupportAdapter(private val supportItems: TechnicalSupportItems,
                              private val clickListener: OnClickListener) :
        androidx.recyclerview.widget.RecyclerView.Adapter<TechnicalSupportAdapter.TechnicalSupportViewHolder>() {

    interface OnClickListener {
        fun onClick(supportItem: SupportItem)
    }

    override fun onBindViewHolder(holder: TechnicalSupportViewHolder, position: Int) =
            holder.bind(supportItems.supportItems[position], clickListener)

    override fun getItemCount() = supportItems.supportItems.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            TechnicalSupportViewHolder = TechnicalSupportViewHolder(LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_technical_support, parent, false))


    class TechnicalSupportViewHolder(val view: View) :
            androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        fun bind(supportItem: SupportItem, clickListener: OnClickListener) {

            val textItemDesc = view.findViewById<TextView>(R.id.textTechnicalSupportItemDescription)


            val relativeLayout = view.findViewById<RelativeLayout>(R.id.relativeTechnicalSupportItemContent)

            Utils.addFontMuseoSans500(view.context, textItemDesc)
            textItemDesc.text = SpannableStringBuilder.valueOf(supportItem.categoryName)

            relativeLayout.setOnClickListener {
                clickListener.onClick(supportItem)
            }
        }
    }


}