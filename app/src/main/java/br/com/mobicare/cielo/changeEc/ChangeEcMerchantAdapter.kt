package br.com.mobicare.cielo.changeEc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4
import br.com.mobicare.cielo.login.domains.entities.ActiveMerchantObj
import br.com.mobicare.cielo.login.domains.entities.EstabelecimentoObj
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import kotlinx.android.synthetic.main.item_change_ec.view.*

class ChangeEcMerchantAdapter(
        private val merchants: ArrayList<Merchant>,
        private val chooserMarchent: (Merchant) -> Unit,
        private val findMoreItens: (() -> Unit)?,
        private val isChangeEcChild: Boolean,
        private val analytics: HomeGA4
)
    : RecyclerView.Adapter<DefaultViewHolderKotlin>(), Filterable {

    private var merchantsSearchList: ArrayList<Merchant> = merchants

    private val mEstableshment = MenuPreference.instance.getEstablishment()
    private val mUser = MenuPreference.instance.getUserObj()

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
        configVisaoAtual(merchant, view)
        configArrowForNonIndividualLevel(merchant, view)

        view.constraint_view.setOnClickListener {
            analytics.logHomeEcSelect(merchant.tradingName)
            chooserMarchent(item(position))
        }
    }

    private fun configArrowForNonIndividualLevel(merchant: Merchant, view: View) {
        view.arrowRightImage?.visibility = if (merchant.hierarchyLevel == "PONTO_VENDA") View.GONE else View.VISIBLE
    }

    private fun configVisaoAtual(merchant: Merchant, view: View) {
        mEstableshment?.let { estableshmentObj ->
            // caso isconvivenciaUser = true
            mUser?.activeMerchant?.let { activeMerchantObj ->
                if (merchant.id == estableshmentObj.ec
                    && (isChangeEcChild.not() || merchant.hierarchyLevelEnglish() == estableshmentObj.hierarchyLevel)
                ) {
                    configColorActivated(view, true)
                } else {
                    configColorActivated(view, false)
                }
            } ?: run {
                // caso isconvivenciaUser = false
                mEstableshment?.let { estableshmentObj ->
                    if (merchant.id == estableshmentObj.ec) {
                        configColorActivated(view, true)
                    } else {
                        configColorActivated(view, false)
                    }
                }
            }
        } ?: run {
            configColorActivated(view, false)
        }
    }

    private fun configColorActivated(view: View, isActivated: Boolean) {
        view.text_item_title?.let { itTitle ->
            itTitle.setTextColor(ContextCompat.getColor(itTitle.context, if (isActivated) R.color.cielo_400 else R.color.nublado_400))
        }
        view.arrowRightImage?.let { itImage ->
            itImage.setColorFilter(ContextCompat.getColor(itImage.context, if (isActivated) R.color.cielo_400 else R.color.nublado_400))
        }
    }

    private fun findMerchatChoose(estableshmentObj: EstabelecimentoObj, merchant: Merchant) =
            (!estableshmentObj.hierarchyLevel.isNullOrEmpty() && estableshmentObj.hierarchyLevel == merchant.hierarchyLevel)

    private fun findMerchatFirst(estableshmentObj: EstabelecimentoObj, merchant: Merchant, activeMerchantObj: ActiveMerchantObj) =
            (estableshmentObj.hierarchyLevel.isNullOrEmpty() && merchant.hierarchyLevel == activeMerchantObj.hierarchyLevel?.let {
                hierarchyLevelConver(
                    it
                )
            })

    private fun hierarchyLevelConver(hierarchyLevel: String): String {
        return if (hierarchyLevel == "PAYMENT_GROUP") "GRUPO_PAGAMENTO" else "PONTO_VENDA"
    }

    private fun item(position: Int): Merchant {
        return merchantsSearchList[position]
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charString = constraint.toString()
                if (charString.isEmpty()) {
                    merchantsSearchList = merchants
                } else {
                    var key = charString.toLowerCasePTBR()
                    val filteredList = merchants.filter {
                        (!it.tradingName.isNullOrEmpty() && it.tradingName.toLowerCasePTBR().contains(key))
                                || !it.id.isNullOrEmpty() && it.id.contains(key)
                    }
                    merchantsSearchList = ArrayList(filteredList)
                }
                val filterResults = FilterResults()
                filterResults.values = merchantsSearchList
                return filterResults

            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                results?.values?.let {
                    merchantsSearchList = it as ArrayList<Merchant>
                    if (merchantsSearchList.isEmpty()) {
                        findMoreItens?.invoke()
                    }
                }
                notifyDataSetChanged()
            }

        }
    }


}