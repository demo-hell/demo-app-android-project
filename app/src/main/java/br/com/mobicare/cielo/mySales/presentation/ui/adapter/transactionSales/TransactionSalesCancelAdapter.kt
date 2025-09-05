package br.com.mobicare.cielo.mySales.presentation.ui.adapter.transactionSales

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.LayoutItemMinhasVendasManBinding
import br.com.mobicare.cielo.mySales.data.model.Sale
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper


@SuppressLint("NotifyDataSetChanged")
class TransactionSalesCancelAdapter(
    private val clickListener: (Sale) -> Unit,
    private val cancelClickListener: (Sale) -> Unit): RecyclerView.Adapter<TransactionsSalesCancelViewHolder>(){


    private var salesList: MutableList<Sale> = mutableListOf()
    val viewBinderHelper: ViewBinderHelper = ViewBinderHelper()

    init{
        viewBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsSalesCancelViewHolder {
       val binding = LayoutItemMinhasVendasManBinding.inflate(
           LayoutInflater.from(parent.context),
           parent,
           false
       )
        return TransactionsSalesCancelViewHolder(
            binding = binding,
            clickListener = clickListener,
            cancelClickListener = cancelClickListener
        )
    }

    override fun getItemCount(): Int = salesList.size

    override fun onBindViewHolder(holder: TransactionsSalesCancelViewHolder, position: Int) {
        val swipeRevealLayout = holder.itemView.findViewById<SwipeRevealLayout>(R.id.swipeRevealUserSells)
        swipeRevealLayout.run {
            if(salesList.isNotEmpty() && salesList.size > position){
                viewBinderHelper.bind(this,salesList[position].toString())
            }
        }
        holder.bind(salesList[position])
    }

    fun setSales(list: MutableList<Sale>){
        salesList.clear()
        salesList.addAll(list)
        notifyDataSetChanged()
    }

    fun addSales(sales: List<Sale>){
        salesList.addAll(sales)
        notifyDataSetChanged()
    }


}