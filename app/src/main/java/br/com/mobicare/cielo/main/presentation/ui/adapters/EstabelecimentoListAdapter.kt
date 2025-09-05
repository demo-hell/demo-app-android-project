package br.com.mobicare.cielo.main.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
import br.com.mobicare.cielo.main.presentation.ui.OnClickEstableshmentListener
import kotlinx.android.synthetic.main.item_menu_estabelecimento.view.*
import java.util.*

/**
 * Created by benhur.souza on 23/05/2017.
 */

class EstabelecimentoListAdapter(var estabelecimentoObjList: ArrayList<EstabelecimentoObj>, val listener: OnClickEstableshmentListener) : androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
//        val binding = DataBindingUtil.inflate<ItemMenuEstabelecimentoBinding>(LayoutInflater.from(viewGroup.context), R.layout.item_menu_estabelecimento, viewGroup, false)
//        binding.layoutItemMenu.setOnClickListener { listenerCadastroScreen.onClickEC(binding.establishment) }
//
//        return DefaultViewHolder(binding)
        return DefaultViewHolderKotlin(LayoutInflater.from(parent?.context).inflate(R.layout.item_menu_estabelecimento, parent, false))
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, i: Int) {
        val item = getItem(i)
        if (item != null) {
            val view : View = holder.mView
            view.layout_item_menu.setOnClickListener { listener.onClickEC(item) }
            view.textview_item_menu_name.text = item.tradeName
            view.textview_item_menu_ec.text = item.ecFormatado
        }
    }

    fun getItem(position: Int): EstabelecimentoObj? {
        return estabelecimentoObjList[position]
    }

    override fun getItemCount(): Int {
        return estabelecimentoObjList.size
    }

}

