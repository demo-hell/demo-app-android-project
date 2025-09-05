package br.com.mobicare.cielo.home.presentation.produtos.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.home.presentation.produtos.domain.entities.ProdutoObj
import kotlinx.android.synthetic.main.item_produtos_home.view.*


/**
 * Created by David on 04/08/17.
 */

class ProdutosHomeAdapter(var context: Context, var list: List<ProdutoObj>? ): androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        val view = LayoutInflater.from(context).inflate(R.layout.item_produtos_home, parent, false)
        return DefaultViewHolderKotlin(view)
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        if(list == null || itemCount == 0){
            return
        }

        val item = getItem(position)
        holder.mView.textview_item_produto_name?.text = item.name
        holder.mView.textview_item_produto_description?.text = item.description
        holder.mView.layout_produtos_item?.setOnClickListener {
        }

    }

    fun getItem(position: Int): ProdutoObj {
        return list!![position]
    }

    override fun getItemCount(): Int {
        return list!!.size
    }
}
