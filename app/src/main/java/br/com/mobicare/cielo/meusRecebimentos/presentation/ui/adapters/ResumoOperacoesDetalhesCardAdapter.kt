package br.com.mobicare.cielo.meusRecebimentos.presentation.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.PostingOfDetailDetailObject
import kotlinx.android.synthetic.main.item_card_resumo_operacoes_detalhe.view.*

/**
 * Created by silvia.miranda on 10/07/2017.
 */

class ResumoOperacoesDetalhesCardAdapter(var detailDatailResumo: ArrayList<PostingOfDetailDetailObject>, var activity: Activity) : androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    private val VIEW_ITEM = 1
    private val VIEW_PROG = 0

    var mIsLoading:Boolean = false

    override fun getItemViewType(position: Int): Int {
        return if ((position+1) > detailDatailResumo?.size!!) VIEW_PROG else VIEW_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        val vh: androidx.recyclerview.widget.RecyclerView.ViewHolder
        if (viewType == VIEW_ITEM) {
            val view = LayoutInflater.from(activity).inflate(R.layout.item_card_resumo_operacoes_detalhe, parent, false)
            vh = DefaultViewHolderKotlin(view)
        } else {
            val view = LayoutInflater.from(activity).inflate(R.layout.item_progress, parent, false)
            vh = DefaultViewHolderKotlin(view)
        }
        return vh
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        var view: View = holder!!.mView
        var item: PostingOfDetailDetailObject? = getItem(position)

        if (item != null) {
            view.item_resumo_operacoes_detalhe_da_detalhe_title.text = item.title
            view.item_resumo_operacoes_detalhe.setLayoutManager(androidx.recyclerview.widget.LinearLayoutManager(activity));
            view.item_resumo_operacoes_detalhe.adapter = item.details?.let { ResumoOperacoesChaveValorAdapter(it, activity) }
        }
    }

    fun getItem(position: Int): PostingOfDetailDetailObject? {
        return if ((position+1) > detailDatailResumo.size ) null else detailDatailResumo.get(position)
    }


    fun appendList(newList: ArrayList<PostingOfDetailDetailObject>) {
        (detailDatailResumo as ArrayList).addAll(newList)
    }

    fun showLoading() {
        mIsLoading = true
        notifyDataSetChanged()
    }

    fun hideLoading() {
        mIsLoading = false
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (mIsLoading)  detailDatailResumo.size + 1 else detailDatailResumo.size
    }

}

