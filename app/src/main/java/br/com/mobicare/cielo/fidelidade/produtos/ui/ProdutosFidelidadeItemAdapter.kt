package br.com.mobicare.cielo.fidelidade.produtos.ui

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.fidelidade.domains.ProdutoFidelidadeObj
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_produtos_fidelidade.view.*

/**
 * Created by silvia.miranda on 22/08/2017.
 */
class ProdutosFidelidadeItemAdapter(var produtosFidelidadeList: ArrayList<ProdutoFidelidadeObj>, var activity: Activity) : androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(LayoutInflater.from(parent.context).inflate(R.layout.item_produtos_fidelidade, parent, false))
    }

    override fun getItemCount(): Int {
        return produtosFidelidadeList.size
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val view: View = holder.mView
        val item: ProdutoFidelidadeObj = getItem(position)

        view.fidelidade_legenda_produtos.text = item.title
        Picasso.get().load(item.imagem).into(view.image_item_produtos_fidelidade);
        view.fidelidade_legenda_produtos.text = item.title
        view.fidelidade_loja_produtos.text = item.loja
        view.fidelidade_pontos_necessarios.text = item.points
        view.fidelidade_loja_pontos_button_resgatar.setOnClickListener {
            onClickItem(item)
        }

        if(position == (itemCount-1)) {
            view.fidelidade_separador_item_produtos.visibility = View.GONE
        }
    }

    fun onClickItem(item: ProdutoFidelidadeObj) {
        //todo colocar ação proximo fragment
    }

    fun getItem(position: Int): ProdutoFidelidadeObj {
        return produtosFidelidadeList[position]
    }
}