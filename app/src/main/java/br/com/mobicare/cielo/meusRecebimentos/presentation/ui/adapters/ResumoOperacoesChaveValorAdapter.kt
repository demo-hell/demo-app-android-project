package br.com.mobicare.cielo.meusRecebimentos.presentation.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.SystemMessage
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import kotlinx.android.synthetic.main.item_lista_card_resumo_operacoes.view.*

/**
 * Created by silvia.miranda on 10/07/2017.
 */
class ResumoOperacoesChaveValorAdapter(var systemMessageDataList: ArrayList<SystemMessage>, var activity: Activity) : androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(LayoutInflater.from(parent.context).inflate(R.layout.item_lista_card_resumo_operacoes, parent, false))
    }

    override fun getItemCount(): Int {
        return systemMessageDataList.size
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val view: View = holder.mView
        val item: SystemMessage = getItem(position)

        view.meus_recebimentos_detalhe_chave.text = item.key
        view.meus_recebimentos_detalhe_valor.text = item.value
    }

    fun getItem(position: Int): SystemMessage {
        return systemMessageDataList.get(position)
    }
}
