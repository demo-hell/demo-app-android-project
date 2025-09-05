package br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.utils.LocaleUtil
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.extensions.gone
import kotlinx.android.synthetic.main.item_view_billing_history.view.*

const val GOAL_ACHIEVED_VALUE = 100

class PostecipadoBillingHistoryAdapter(
        private val history: List<PostecipadoRentInformationResponse>,
        private val context: Context
) : RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_view_billing_history, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val view: View = holder.mView

        val historyObj = history[position]
        val percentageReached = historyObj.percentageReached

        view.textMonth?.text = LocaleUtil.getMonthLongName(historyObj.referenceMonth)
        view.valueAchieved?.text = Utils.formatValue(historyObj.billingPerformed)

        if (percentageReached != null && percentageReached.toDouble().toInt() >= GOAL_ACHIEVED_VALUE) {
            view.goalStatus.text = context.getString(R.string.goal_achieved)
            view.valueAchieved.setTextColor(ContextCompat.getColor(context, R.color.success_400))
        } else {
            view.goalStatus.text = context.getString(R.string.unreached_goal)
            view.valueAchieved.setTextColor(ContextCompat.getColor(context, R.color.alert_400))
        }

        if (historyObj == history.last()) view.divider.gone()
    }

    override fun getItemCount(): Int {
        return history.size
    }
}