package br.com.mobicare.cielo.meuCadastro.presetantion.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroSolucoesContratadas
import kotlinx.android.synthetic.main.item_cadastro_solucoes_contratadas.view.*
import java.util.*

/**
 * Created by benhur.souza on 31/05/2017.
 */

class ItemSolucoesContratadasAdapter(var list:  Array<MeuCadastroSolucoesContratadas> ): androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>(){

    val solucoesContratadasList: List<MeuCadastroSolucoesContratadas>

    init{
        this.solucoesContratadasList = Arrays.asList(*list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(LayoutInflater.from(parent.context).inflate(R.layout.item_cadastro_solucoes_contratadas, parent, false))
    }

    override fun getItemCount(): Int {
        return solucoesContratadasList.size
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val view : View = holder.mView
        val item : MeuCadastroSolucoesContratadas = getItem(position)

        view.textview_item_solucao_contratada_value.text = item.price
        view.textview_item_solucao_contratada_num_item.text = item.quantidadeItem()
        view.textview_item_solucao_contratada_title.text = item.name
        view.textview_item_solucao_contratada_description.text = item.description

    }

    fun getItem(position: Int): MeuCadastroSolucoesContratadas{
        return solucoesContratadasList.get(position)
    }
}