package br.com.mobicare.cielo.chargeback.presentation.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.chargeback.data.model.response.RefundFileInformationList
import br.com.mobicare.cielo.chargeback.presentation.details.adapter.viewholder.ChargebackDocumentSenderViewHolder
import br.com.mobicare.cielo.databinding.ItemChargebackDocumentSenderAdapterBinding

class ChargebackDocumentSenderAdapter(
    private val refundFileInformationList: List<RefundFileInformationList>
) : RecyclerView.Adapter<ChargebackDocumentSenderViewHolder>() {

    private var onTap: ((RefundFileInformationList) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChargebackDocumentSenderViewHolder {
        return ItemChargebackDocumentSenderAdapterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).let {
            ChargebackDocumentSenderViewHolder(it)
        }
    }

    override fun onBindViewHolder(holder: ChargebackDocumentSenderViewHolder, position: Int) {
        holder.bind(refundFileInformationList[position])
        holder.setOnClickListener {
            onTap?.invoke(refundFileInformationList[position])
        }
    }

    override fun getItemCount() = refundFileInformationList.size

    fun setOnTap(value: ((RefundFileInformationList) -> Unit)) {
        onTap = value
    }
}