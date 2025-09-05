package br.com.mobicare.cielo.arv.presentation.historic.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.arv.presentation.historic.details.model.ArvHistoricDetailsCardModel
import br.com.mobicare.cielo.arv.presentation.historic.details.viewHolder.ArvHistoricDetailsCardViewHolder
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.utils.AccessibilityUtils
import br.com.mobicare.cielo.databinding.LayoutCardGeneralInformationBinding

class ArvHistoricDetailsCardAdapter : RecyclerView.Adapter<ArvHistoricDetailsCardViewHolder>() {

    private var items: ArrayList<ArvHistoricDetailsCardModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArvHistoricDetailsCardViewHolder {
        val item = LayoutCardGeneralInformationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return ArvHistoricDetailsCardViewHolder(item)
    }

    override fun onBindViewHolder(holder: ArvHistoricDetailsCardViewHolder, position: Int) {
        val item = items[position]
        val descriptionItemsChild = ArrayList<String>()

        if (item.labelOne.isNullOrEmpty().not() && item.valueOne.isNullOrEmpty().not())
            descriptionItemsChild.add(item.labelOne + Text.COLON_WITH_SPACE + item.valueOne)
        if (item.labelTwo.isNullOrEmpty().not() && item.valueTwo.isNullOrEmpty().not())
            descriptionItemsChild.add(item.labelTwo + Text.COLON_WITH_SPACE + item.valueTwo)

        val description = AccessibilityUtils.descriptionForSimpleListHorizontal(
                holder.itemView.context,
                itemsChild = descriptionItemsChild,
                position = position,
                length = itemCount
        )

        holder.bind(item)
        holder.setContentDescriptionCard(description)
    }

    override fun getItemCount() = items.count()

    fun setItems(list: List<ArvHistoricDetailsCardModel>){
        items.clear()
        items.addAll(list)
    }

}