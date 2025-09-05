package br.com.mobicare.cielo.taxaPlanos.presentation.ui.taxasBandeiras

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.meuCadastro.domains.entities.Products
import kotlinx.android.synthetic.main.item_fees.view.*


/**
 * Created by gustavon on 15/01/18.
 */
class FeesAdapter(val products: List<Products>) : androidx.recyclerview.widget.RecyclerView.Adapter<FeesAdapter.ViewHolder>() {

    var listenerOnClickItemFee: OnClickItemFee? = null

    constructor(products: List<Products>, listenerItemFee: OnClickItemFee) : this(products) {
        this.listenerOnClickItemFee = listenerItemFee
    }

    interface OnClickItemFee {
        fun onClickItem()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fees, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product: Products? = getItem(position)

        holder.let {
            it.feeDescription.feeDescription?.text = product?.name
            it.feeValue.feeValue?.text = product?.fee

            val tempProduct = product as Products
            it.feeInstallments.text = tempProduct.installmentsText
            it.feeInstallments.visibility = if(tempProduct.installmentsText != "none") View.VISIBLE else View.INVISIBLE

        }

        if (products.size >= BandeirasHabilitadasFragment.CARD_PRODUCTS_THRESHOLD) {
            holder.let {
                it.feeDescription.setOnClickListener {
                    listenerOnClickItemFee?.let {
                        it.onClickItem()
                    }
                }
                it.feeValue.setOnClickListener {
                    listenerOnClickItemFee?.let {
                        it.onClickItem()
                    }
                }
                it.feeItem.setOnClickListener {
                    listenerOnClickItemFee?.let {
                        it.onClickItem()
                    }
                }
            }
        } else {
            holder.let {
                it.feeDescription.setOnClickListener(null)
                it.feeValue.setOnClickListener(null)
                it.feeItem.setOnClickListener(null)
            }
        }

    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun getItem(position: Int): Products? {
        return products[position]
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val feeDescription = itemView.feeDescription
        val feeValue = itemView.feeValue
        val feeInstallments = itemView.textInstallments
        val feeItem = itemView
    }

}