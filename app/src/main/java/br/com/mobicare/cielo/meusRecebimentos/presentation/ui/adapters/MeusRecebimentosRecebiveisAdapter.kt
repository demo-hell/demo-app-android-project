package br.com.mobicare.cielo.meusRecebimentos.presentation.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.extensions.formatarValores
import br.com.mobicare.cielo.meusrecebimentosnew.models.SummaryItems
import br.com.mobicare.cielo.meusrecebimentosnew.visaosumarizada.PARAM_OBJECT
import br.com.mobicare.cielo.meusrecebimentosnew.visaosumarizada.PARAM_QUICKFILTER
import br.com.mobicare.cielo.meusrecebimentosnew.visaosumarizada.VisaoSumarizadaMeusRecebimentosActivity
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import kotlinx.android.synthetic.main.item_recebiveis.view.*

class MeusRecebimentosRecebiveisAdapter(val summaryItems: List<SummaryItems>,
                                        val context: Context?,
                                        val quickFilter: QuickFilter)
    : RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recebiveis, parent, false))
    }

    override fun getItemCount() = summaryItems.size

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val view: View = holder.mView
        val item: SummaryItems = summaryItems[position]

        view.meus_recebimentos_recebiveis_name.text = item.type
        item.netAmount.let {
            view.meus_recebimentos_recebiveis_amount.formatarValores(it, true)
        }
        view.meus_recebimentos_recebiveis_linear.setOnClickListener {
            context?.startActivity(Intent(context, VisaoSumarizadaMeusRecebimentosActivity::class.java).let {
                it.putExtra(PARAM_OBJECT, item)
                it.putExtra(PARAM_QUICKFILTER, this.quickFilter)
            })
        }
    }
}
