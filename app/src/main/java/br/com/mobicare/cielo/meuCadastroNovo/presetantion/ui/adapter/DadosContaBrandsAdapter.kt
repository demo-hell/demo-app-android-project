package br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.meuCadastroNovo.domain.Brand
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_dados_conta_brand.view.*

/**
 * Created by Enzo Teles
 */
class DadosContaABandeiradapter(var brands: List<Brand>?, var isSize: Boolean, var size:Int = 0) :
    RecyclerView.Adapter<VH>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_dados_conta_brand, parent, false)
        val vh = VH(v)
        return vh
    }

    override fun getItemCount(): Int {
        return if (isSize) {
            brands!!.size
        } else {
            if (brands!!.size > size) size else brands!!.size
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val brand = brands?.get(position)

        brand?.apply {
            Picasso.get()
                    .load(this.imgSource)
                    .into(holder.itemView.dc_iv_brand_item, object : Callback {
                        override fun onSuccess() {
                            holder.itemView.progress_dc.visibility = View.GONE
                            holder.itemView.dc_iv_brand_item.visibility = View.VISIBLE
                        }

                        override fun onError(e: Exception?) {
                            e?.printStackTrace()
                        }

                    })
        }
    }
}




