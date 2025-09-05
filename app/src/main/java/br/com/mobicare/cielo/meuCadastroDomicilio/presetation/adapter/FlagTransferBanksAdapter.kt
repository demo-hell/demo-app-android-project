package br.com.knowledge.capitulo7_mvp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.meuCadastroNovo.domain.Bank
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.ft_fragment_01_item.view.*

/**
 * create by Enzo Teles
 * */
class FlagTransferBanksAdapter(
        var listBanks: ArrayList<Bank>, var callback: (Bank)->Unit) :
        RecyclerView.Adapter<VH>() {

    lateinit var onItemLisnter: OnItemListener

    var mItemSel = -1

    interface OnItemListener {
        fun onItemSelected(adapter: FlagTransferBanksAdapter)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.ft_fragment_01_item, parent, false)
        val vh = VH(v).apply {
            onItemLisnter = this
        }

        return vh

    }

    override fun getItemCount() = listBanks?.size

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val bank = listBanks[position]
        holder.itemView.tv_name_bank.text = if(bank.name.equals("VISANET")) "Conta digital" else "Ag: ${bank.agency} \nCc: ${bank.accountNumber}-${bank.accountDigit}"
        Picasso.get().load(bank.imgSource).into(holder.itemView.iv_icon_bank)
        holder.onItemSelected(this)
        if (mItemSel == position) {
            callback(bank)
            holder.itemView.setBackgroundResource(R.drawable.ft_frag_02_item_selector_check)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.ft_frag_02_item_selector_uncheck)
        }
    }
}

class VH(itemView: View) : RecyclerView.ViewHolder(itemView), FlagTransferBanksAdapter.OnItemListener {
    override fun onItemSelected(adapter: FlagTransferBanksAdapter) {
        var listener: View.OnClickListener = View.OnClickListener {
            adapter.mItemSel = adapterPosition
            adapter!!.notifyDataSetChanged()
            itemView.setBackgroundResource(R.drawable.ft_frag_02_item_selector_check)

        }
        itemView.setOnClickListener(listener)
    }

}

