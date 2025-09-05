package br.com.mobicare.cielo.meusRecebimentos.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.utils.Utils
import kotlinx.android.synthetic.main.item_dialog_domicilio_bancario.view.*

/**
 * Created by benhur.souza on 10/07/2017.
 */
class DialogRecebimentosAdapter(var list:  Array<Double> ): androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_domicilio_bancario, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val view : View = holder.mView
        val value = getItem(position)

        view.text_view_meus_recebimentos_dialog_item_description.text = "${(position+1)} - ${Utils.formatValue(value)}"
    }

    fun getItem(position: Int): Double {
        return list[position]
    }
}