package br.com.mobicare.cielo.internaluser.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4
import kotlinx.android.synthetic.main.item_change_ec.view.*

class ChangeEcMerchantInternalUserAdapter(
        private val merchants: ArrayList<Merchant>,
        private val chooserMarchent: (Merchant) -> Unit,
        private val analytics: HomeGA4)
    : RecyclerView.Adapter<DefaultViewHolderKotlin>() {

    private var merchantsSearchList: ArrayList<Merchant> = merchants

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DefaultViewHolderKotlin {
        return DefaultViewHolderKotlin(LayoutInflater
                .from(parent.context).inflate(R.layout.item_change_ec, parent, false))
    }

    override fun getItemCount(): Int {
        return merchantsSearchList.size
    }


    override fun onBindViewHolder(holder: DefaultViewHolderKotlin, position: Int) {
        val view: View = holder.mView
        val merchant = item(position)

        view.text_item_title.text = merchant.tradingName
        view.text_item_ec.text = merchant.id
        view.text_item_individual.text = merchant.hierarchyLevelDescription
        view.constraint_view.setOnClickListener {
            analytics.logHomeEcSelect(merchant.tradingName)
            chooserMarchent(item(position))
        }
    }

    private fun item(position: Int): Merchant {
        return merchantsSearchList[position]
    }
}