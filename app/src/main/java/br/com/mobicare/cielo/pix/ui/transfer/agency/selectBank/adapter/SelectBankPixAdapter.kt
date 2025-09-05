package br.com.mobicare.cielo.pix.ui.transfer.agency.selectBank.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.databinding.ItemSelectBankPixBinding
import br.com.mobicare.cielo.pix.model.PixBank
import br.com.mobicare.cielo.pix.ui.transfer.agency.selectBank.PixSelectBankContract

class SelectBankPixAdapter(private var mBankItems: List<PixBank>, private val mListener: PixSelectBankContract.View, private val mContext: Context) : RecyclerView.Adapter<SelectBankPixGroupHolder>() {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): SelectBankPixGroupHolder {
        val binding = ItemSelectBankPixBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SelectBankPixGroupHolder(binding, mListener)
    }

    fun updateList(newBankList: List<PixBank>) {
        mBankItems = newBankList
        notifyDataSetChanged()
    }

    override fun getItemCount() = mBankItems.size

    override fun onBindViewHolder(holder: SelectBankPixGroupHolder, position: Int) {
        holder.bind(mContext, mBankItems[position])
    }
}