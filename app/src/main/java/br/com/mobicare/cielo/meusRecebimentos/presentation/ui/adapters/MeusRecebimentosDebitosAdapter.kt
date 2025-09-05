package br.com.mobicare.cielo.meusRecebimentos.presentation.ui.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.domains.entities.SystemMessage
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.utils.Utils
import kotlinx.android.synthetic.main.item_card_debitos.view.*

/**
 * Created by silvia.miranda on 26/06/2017.
 */

class MeusRecebimentosDebitosAdapter(var debitDataList: ArrayList<SystemMessage>, var context: Context, var activity: Activity) : androidx.recyclerview.widget.RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(LayoutInflater.from(parent.context).inflate(R.layout.item_card_debitos, parent, false))
    }

    override fun getItemCount(): Int {
        return debitDataList.size
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val view: View = holder.mView
        val item: SystemMessage = getItem(position)

        view.meus_recebimentos_debitos_chave.text = item.key
        view.meus_recebimentos_debitos_valor.text = item.value
        view.meus_recebimentos_debitos_valor.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, Category.TAP_LABEL),
                action = listOf("CentralDeAjuda"),
                label = listOf("Regularize em ${item.key}")
            )
            Utils.validateCallPhone(activity, item.value!!, context.getString(R.string.menu_meus_recebimentos))
        }

        changeVisibility(position, view)
    }

    fun getItem(position: Int): SystemMessage {
        return debitDataList[position]
    }

    fun changeVisibility(position: Int, view: View) {
        if (position == itemCount - 1) {
            view.meus_recebimentos_debitos_item_card_divisioria.visibility = View.GONE
        } else {
            view.meus_recebimentos_debitos_item_card_divisioria.visibility = View.VISIBLE
        }
    }

}
