package br.com.mobicare.cielo.pagamentoLink.presentation.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLink
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.listAtivosResumo.PaymentLinkListAtivosResumoFragment
import kotlinx.android.synthetic.main.pagamento_link_list_item_ativos.view.constraint_view
import kotlinx.android.synthetic.main.pagamento_link_list_item_ativos.view.text_fantasy_name
import kotlinx.android.synthetic.main.pagamento_link_list_item_ativos.view.text_value
import kotlinx.android.synthetic.main.pagamento_link_list_item_ativos_new.view.*

class PagamentoLinkListItemAtivosAdapter(val onClick: (PaymentLink) -> Unit):
        RecyclerView.Adapter<DefaultViewHolderKotlin>(){

    private var list: MutableList<PaymentLink> = mutableListOf()
    var context: Context?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pagamento_link_list_item_ativos_new, parent, false)
        context = parent.context
        return DefaultViewHolderKotlin(view)
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val item = getItem(position)
        holder.mView.text_fantasy_name.text = item.name
        holder.mView.text_value.text = item.price?.toPtBrRealString()
        var txtType = holder.mView.txt_type
        var imageType = holder.mView.im_type
        var txtTypeDelivere = holder.mView.text_type_delivere

        holder.mView.constraint_view.setOnClickListener {
            onClick(item)
        }

        val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(32, 30, 32, 0)
        holder.mView.constraint_view.setLayoutParams(params)


        when {
            item.shipping?.type.equals(PaymentLinkListAtivosResumoFragment.CORREIOS) -> {
                PaymentLinkListAtivosResumoFragment.visibleImageTypeCorreio(txtType, imageType, txtTypeDelivere, this.context!!)
            }
            item.shipping?.type.equals(PaymentLinkListAtivosResumoFragment.LOGGI) -> {
                PaymentLinkListAtivosResumoFragment.visibleImageTypeLoggi(txtType, imageType, txtTypeDelivere,this.context!!)
            }
            item.shipping?.type.equals(PaymentLinkListAtivosResumoFragment.FIXED_AMOUNT) -> {
                PaymentLinkListAtivosResumoFragment.visibleImageTypeLogista(txtType, imageType, txtTypeDelivere, this.context!!)
            }
            else -> {
                PaymentLinkListAtivosResumoFragment.visibleImageTypeDigital(txtType, imageType, txtTypeDelivere, this.context!!)
            }
        }

    }

    fun getItem(position: Int): PaymentLink {
        return list.get(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    internal fun initialData(items: List<PaymentLink>) {
        val allItems = list.plus(items)
        list.clear()
        list.addAll(allItems)
        notifyDataSetChanged()
    }

    fun appendData(items: List<PaymentLink>) {
        list.addAll(items)
        notifyDataSetChanged()
    }

    fun clearItems() {
        list.clear()
        notifyDataSetChanged()
    }

}
