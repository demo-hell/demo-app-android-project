package br.com.mobicare.cielo.chargeback.presentation.details.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.chargeback.data.model.response.RefundFileInformationList
import br.com.mobicare.cielo.databinding.ItemChargebackDocumentSenderAdapterBinding

class ChargebackDocumentSenderViewHolder(
    private val binding: ItemChargebackDocumentSenderAdapterBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        refundFileInformationList: RefundFileInformationList
    ) {
        binding.tvFileName.text = refundFileInformationList.nameFile
    }

    fun setOnClickListener(onClickListener: () -> Unit) {
        binding.btnDownloadFile.setOnClickListener {
            onClickListener.invoke()
        }
    }

}