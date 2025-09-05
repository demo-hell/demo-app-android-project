package br.com.mobicare.cielo.chargeback.presentation.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.mobicare.cielo.chargeback.presentation.details.adapter.viewholder.ChargebackInfoContentViewHolder
import br.com.mobicare.cielo.chargeback.presentation.details.adapter.viewholder.ChargebackUniqueFieldCardContentViewHolder
import br.com.mobicare.cielo.chargeback.presentation.details.model.ChargebackInfoContent
import br.com.mobicare.cielo.chargeback.presentation.details.model.ChargebackInfoContentFieldType
import br.com.mobicare.cielo.databinding.ItemChargebackInfoBinding
import br.com.mobicare.cielo.databinding.ItemChargebackUniqueFieldInfoBinding

class ChargebackInfoContentAdapter(
    private val chargebackInfoContentList: List<ChargebackInfoContent>
) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        private const val NORMAL_CARD = 0
        private const val UNIQUE_FIELD_CARD = 1
    }

    private var onItemReasonClicked: (() -> Unit)? = null
    private var onItemMessageClicked: ((String) -> Unit)? = null

    fun setOnItemReasonClicked(callback: () -> Unit) {
        onItemReasonClicked = callback
    }

    fun setOnItemMessageClicked(callback: (String) -> Unit) {
        onItemMessageClicked = callback
    }

    override fun getItemViewType(position: Int): Int {
        return if(chargebackInfoContentList[position].firstField.type ==
            ChargebackInfoContentFieldType.UNIQUE_FIELD)
                UNIQUE_FIELD_CARD
        else
            NORMAL_CARD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == NORMAL_CARD){
            val view = ItemChargebackInfoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            ChargebackInfoContentViewHolder(view)
        }else {
            val view = ItemChargebackUniqueFieldInfoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            ChargebackUniqueFieldCardContentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(holder is ChargebackUniqueFieldCardContentViewHolder)
            holder.bind(chargebackInfoContentList[position])
        else {
            (holder as ChargebackInfoContentViewHolder).bind(chargebackInfoContentList[position])
            onItemReasonClicked?.let { holder.setOnItemReasonClicked(it) }
            onItemMessageClicked?.let { holder.setOnItemMessageClicked(it) }
        }
    }

    override fun getItemCount() = chargebackInfoContentList.size
}