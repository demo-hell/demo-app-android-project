package br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.BalcaoRecebiveisExtratoContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.activity.BalcaoRecebiveisExtratoActivity
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Item
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Negotiations
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.presenter.BalcaoRecebiveisExtratoPresenter
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.convertTimeStampToDate
import br.com.mobicare.cielo.extensions.visible
import kotlinx.android.synthetic.main.content_ver_mais.*
import kotlinx.android.synthetic.main.item_balcao_recebiveis_extrato.view.*
import kotlinx.android.synthetic.main.layout_card_error.*
import kotlinx.android.synthetic.main.layout_list_recebiveis_extrato.*
import kotlinx.android.synthetic.main.layout_list_recebiveis_extrato_new.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * create by Enzo Teles
 * Jan 19, 2021
 * */
class BalcaoRecebiveisExtratoFragment : BaseFragment(), BalcaoRecebiveisExtratoContract.View {

    var negotiations: Negotiations? = null
    var listNegotiations: List<Item>? = null

    private val presenter: BalcaoRecebiveisExtratoPresenter by inject {
        parametersOf(this)
    }

    private val analytics: ReceivablesAnalyticsGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_balcao_recebiveis_extrato, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.initView()
    }

    /**
     * method to init views
     * */
    override fun initView() {
        linearSeeMoreContent?.gone()
        layoutCardError?.gone()
        callAPI()

        buttonLoadAgain?.setOnClickListener {
            layoutCardError?.gone()
            callAPI()
        }

        linearSeeMoreContent?.setOnClickListener {
            arguments?.let { itBundle ->
                negotiations?.let { itNegotiations ->
                    itBundle.getString(INIT_DATE)?.let { initDate ->
                        itBundle.getString(FINAL_DATE)?.let { finalDate ->
                            BalcaoRecebiveisExtratoActivity.start(
                                requireContext(),
                                initDate,
                                finalDate,
                                itNegotiations
                            )
                        }
                    }
                }
            }
        }

    }

    /**
     * method to call api negotiations
     * */
    private fun callAPI() {
        arguments?.let {
            presenter.callApi(it.getString(INIT_DATE), it.getString(FINAL_DATE))
        }
    }

    /**
     * method to init progess
     * */
    override fun initProgress() {
        progress_extrato?.visible()
    }

    /**
     * method to finished progess
     * */
    override fun finishedProgress() {
        progress_extrato?.gone()
    }

    /**
     * method to load negotiations of the api.
     * @param negotiations
     * */
    override fun showSuccess(negotiations: Negotiations) {
        layout_recebiveis_extrato?.visible()
        layoutCardError?.gone()

        this.negotiations = negotiations
        negotiations.items?.let { it -> validationsViews(it) }
    }

    /**
     * method to do the validation about views
     * @param negotiations
     * */
    private fun validationsViews(negotiations: List<Item>) {
        val isShow = negotiations.isNotEmpty()
        rvListExtrato?.visible(isShow)
        linearSeeMoreContent?.visible(isShow)
        tvListExtratoEmpty?.visible(isShow.not())

        if (isShow)
            populateRecyclerView(negotiations)
    }

    /**
     * method to populate recycler view
     * @param negotiations
     * */
    private fun populateRecyclerView(negotiations: List<Item>) {
        listNegotiations = if (negotiations.size > THREE)
            negotiations.subList(ZERO, THREE)
        else
            negotiations

        rvListExtrato?.layoutManager = LinearLayoutManager(requireContext())
        rvListExtrato?.setHasFixedSize(true)

        listNegotiations?.let { list ->
            val adapter = DefaultViewListAdapter(list, R.layout.item_balcao_recebiveis_extrato)
            adapter.setBindViewHolderCallback(object :
                DefaultViewListAdapter.OnBindViewHolder<Item> {
                override fun onBind(item: Item, holder: DefaultViewHolderKotlin) {
                    holder.mView.tv_value_negociation.text = Utils.formatValue(item.netAmount)
                    holder.mView.tv_date_negociation.text = item.date.convertTimeStampToDate()
                    holder.mView.tv_type_negociation.text = if (item.operationSourceCode == ONE)
                        CIELO
                    else
                        MERCADO

                    if (item.operationNumber == list[list.size - ONE].operationNumber)
                        holder.mView.view2.invisible()
                }
            })
            rvListExtrato?.adapter = adapter
        }
    }

    /**
     * method to show error of the api
     * */
    override fun serverError() {
        layout_recebiveis_extrato?.gone()
        layoutCardError?.visible()
        linearSeeMoreContent?.invisible()
    }

    override fun logException(screenName: String, error: NewErrorMessage) {
        analytics.logException(screenName, error)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.mInteractor.cleanDisposable()
    }

    companion object {

        const val INIT_DATE = "INIT_DATE"
        const val FINAL_DATE = "FINAL_DATE"
        const val CIELO = "Cielo"
        const val MERCADO = "Mercado"
        const val MARKET = "Market"

        fun newInstance(initDate: String, finalDate: String) =
            BalcaoRecebiveisExtratoFragment().apply {
                arguments = Bundle().apply {
                    putString(INIT_DATE, initDate)
                    putString(FINAL_DATE, finalDate)
                }
            }
    }
}