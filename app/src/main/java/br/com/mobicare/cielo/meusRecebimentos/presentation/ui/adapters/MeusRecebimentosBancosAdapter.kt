package br.com.mobicare.cielo.meusRecebimentos.presentation.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.utils.OnClickItemListener
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.BankDataObj
import br.com.mobicare.cielo.meusRecebimentos.presentation.ui.viewholders.PrepaidBankViewHolder
import kotlinx.android.synthetic.main.item_card_bancos.view.*


class MeusRecebimentosBancosAdapter(
    var bankDataList: ArrayList<BankDataObj>,
    var context: Context,
    var listener: OnClickItemListener<BankDataObj>
) :
    androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>() {


    companion object {
        const val COMMON_BANK_VIEW_TYPE = 0
        const val PREPAID_VIEW_TYPE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return if (viewType == COMMON_BANK_VIEW_TYPE) {
            DefaultViewHolderKotlin(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_card_bancos, parent, false)
            )
        } else {
            PrepaidBankViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_prepaid_card_bank, parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (bankDataList[position].isPrepaid) {
            PREPAID_VIEW_TYPE
        } else {
            COMMON_BANK_VIEW_TYPE
        }
    }

    override fun getItemCount(): Int {
        return bankDataList.size
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {

        if (holder !is PrepaidBankViewHolder) {
            val view: View = holder.mView
            val item: BankDataObj = getItem(position)

            view.meus_recebimentos_nome_banco.text = item.name
            item.amount.let {
                val recebimentosDepositado: Double = item.amount?.toDouble() as Double
                view.meus_recebimentos_valor_deposito_banco.text =
                    Utils.formatValue(recebimentosDepositado)
            }
            view.meus_recebimentos_agencia.text =
                "${context.resources.getString(R.string.meus_recebimentos_agencia)} ${item.branch}"
            view.meus_recebimentos_conta.text =
                "${context.resources.getString(R.string.meus_recebimentos_conta)} ${item.account}"
            view.meus_recebimentos_lancamentos.text = item.postingsQty

            changeVisibility(position, view)
        } else {
            holder.bind(getItem(position))
        }

    }

    fun getItem(position: Int): BankDataObj {
        return bankDataList[position]
    }

    fun changeVisibility(position: Int, view: View) {
        if (position == itemCount - 1) {
            view.meus_recebimentos_item_card_divider.visibility = View.GONE
        } else {
            view.meus_recebimentos_item_card_divider.visibility = View.VISIBLE
        }
    }

}
