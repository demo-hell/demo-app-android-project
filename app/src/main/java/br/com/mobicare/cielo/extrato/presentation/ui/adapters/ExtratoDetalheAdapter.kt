package br.com.mobicare.cielo.extrato.presentation.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ItemDetalhesVendas
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.extrato.domains.entities.extratoListaAcumulada.ExtratoListaTransicaoObj
import kotlinx.android.synthetic.main.item_extrato_detalhe.view.*
import java.util.*

/**
 * Created by benhur.souza on 08/06/2017.
 */
class ExtratoDetalheAdapter(var context: Context, var list: ArrayList<ItemDetalhesVendas>?) : androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        val view = LayoutInflater.from(context).inflate(R.layout.item_extrato_detalhe, parent, false)
        return DefaultViewHolderKotlin(view)
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        if (list == null || itemCount == 0) {
            return
        }

        var item = getItem(position)

        holder.mView.textview_item_extrato_detalhe_key?.text = item.key
        holder.mView.textview_item_extrato_detalhe_value?.text = item.value
    }

    fun getItem(position: Int): ItemDetalhesVendas {
        return list!!.get(position)
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    interface OnClickExtratoItemListener {
        fun onClickItem(item: ExtratoListaTransicaoObj)
    }
}
