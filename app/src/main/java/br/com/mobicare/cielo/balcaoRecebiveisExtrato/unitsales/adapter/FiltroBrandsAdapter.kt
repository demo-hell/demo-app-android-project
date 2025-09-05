package br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.CardBrands
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter.VH
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_filter_detail_operation_brand.view.*


class FiltroBrandsAdapter(
    var brands: List<CardBrands>,
    var listSelected: MutableList<CardBrands> = mutableListOf()
): RecyclerView.Adapter<VH>() {

    var onClickItem:() -> Unit = {}
    var listItemSelect:MutableList<CardBrands> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_filter_detail_operation_brand, parent, false)
        val vh = VH(v)
        this.listItemSelect = listSelected
        return vh
    }

    override fun getItemCount(): Int {
        return brands.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val brand = brands[position]
        brand.value?.let {
            Picasso.get()
                .load(BrandCardHelper.getUrlBrandImageByCode(it))
                .into(holder.itemView.dc_iv_brand_item, object : Callback {
                    override fun onSuccess() {
                        holder.itemView.progress_dc.visibility = View.GONE
                        holder.itemView.dc_iv_brand_item.visibility = View.VISIBLE
                        if(listSelected.isEmpty().not() && listSelected.contains(brand)){
                            holder.itemView.contentBrands.setBackgroundResource(R.drawable.brands_blue_filter_arround)
                        }
                    }

                    override fun onError(e: Exception?) {
                        e?.printStackTrace()
                    }

                })

            holder.itemView.setOnClickListener {
                addItemSelect(holder, brand)
                onClickItem()
            }
        }
    }

    fun addItemSelect(holder: VH, brand:CardBrands){
        if(listItemSelect.contains(brand)){
            listItemSelect.remove(brand)
            holder.itemView.contentBrands.setBackgroundResource(R.drawable.brands_white_filter_arround)
        }else{
            listItemSelect.add(brand)
            holder.itemView.contentBrands.setBackgroundResource(R.drawable.brands_blue_filter_arround)
        }
    }
}




