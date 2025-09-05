package br.com.mobicare.cielo.pix.ui.transfer.agency.selectBank.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.ItemSelectBankPixBinding
import br.com.mobicare.cielo.pix.model.PixBank
import br.com.mobicare.cielo.pix.ui.transfer.agency.selectBank.PixSelectBankContract

class SelectBankPixGroupHolder(
        private val itemBinding: ItemSelectBankPixBinding,
        private val listener: PixSelectBankContract.View?
) :
        RecyclerView.ViewHolder(itemBinding.root) {

    fun bind(context: Context, bank: PixBank) {
        itemBinding.apply {
            tvBankName.text = context.getString(R.string.bank_list_mask, bank.code.toString(), bank.name)
            itemContainer.setOnClickListener {
                listener?.onSelectedBank(bank)
            }
        }
    }
}