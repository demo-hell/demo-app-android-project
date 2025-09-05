package br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.BalcaoRecebiveisExtratoBanksContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.BalcaoRecebiveisExtratoContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.BankItem
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Item
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Negotiations
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.NegotiationsBanks
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.presenter.BalcaoRecebiveisExtratoBanksPresenter
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.presenter.BalcaoRecebiveisExtratoPresenter
import br.com.mobicare.cielo.commons.constants.FIVE
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.*
import kotlinx.android.synthetic.main.content_recebiveis_bancos.*
import kotlinx.android.synthetic.main.fragment_recebiveis_extrato_type.*
import kotlinx.android.synthetic.main.item_amount_recebiveis_extrato.*
import kotlinx.android.synthetic.main.item_balcao_last_negotiations.view.*
import kotlinx.android.synthetic.main.item_card_bancos_negotiations.view.*
import kotlinx.android.synthetic.main.item_prepaid_card_bank_negotiation.view.*
import kotlinx.android.synthetic.main.layout_card_error_extrato.*
import kotlinx.android.synthetic.main.layout_list_last_negotiations.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*

class RecebiveisExtratoTypeFragment : BaseFragment(), BalcaoRecebiveisExtratoContract.View,
    BalcaoRecebiveisExtratoBanksContract.View {

    private val typeTab: Int? by lazy { arguments?.getInt(CATEGORY_TYPE_ARGS) }
    private val dateInit: String? by lazy { arguments?.getString(DATEINIT_ARGS) }
    private val dateEnd: String? by lazy { arguments?.getString(DATEEND_ARGS) }
    private var callBack: (Negotiations) -> Unit = {}

    private var initDateChange: String? = null
    private var endDateChange: String? = null
    private var negotiationChange: Negotiations? = null

    private val presenterBalcao: BalcaoRecebiveisExtratoPresenter by inject {
        parametersOf(this)
    }
    private val presenterBank: BalcaoRecebiveisExtratoBanksPresenter by inject {
        parametersOf(this)
    }
    private val analytics: ReceivablesAnalyticsGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recebiveis_extrato_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenterBalcao.initView()
    }

    override fun initView() {
        val dateInit = DataCustomNew()
        dateInit.setDate(Date())

        initNegotiations()

        verMais?.setOnClickListener { _ ->
            negotiationChange?.let { callBack(it) }
        }

        buttonLoadAgain?.setOnClickListener {
            callBanks()
        }

        negotiationEmpity?.configureButtonVisible(false)
    }

    fun changeNegotiations(dateInitParams: String, dateEndParams: String) {
        this.initDateChange = dateInitParams
        this.endDateChange = dateEndParams
        parseNegotiationsForTab(dateInitParams, dateEndParams)
    }

    private fun initNegotiations() {
        parseNegotiationsForTab(initDateChange ?: dateInit, endDateChange ?: dateEnd)
    }

    override fun initProgress() {
        progressViewLastNegotiations?.visible()
        scrollContentLayout?.gone()
    }

    override fun finishedProgress() {
        progressViewLastNegotiations?.gone()
        scrollContentLayout?.visible()
    }

    override fun showSuccess(negotiations: Negotiations) {
        errorView?.gone()
        negotiationEmpity?.gone()

        this.negotiationChange = negotiations
        processSummary(negotiations)
        processItemsLastNegotiations(negotiations)

        callBanks()
    }

    private fun callBanks() {
        typeTab?.let {
            val initDate = initDateChange ?: dateInit
            val endDate = endDateChange ?: dateEnd
            val type = if (it == ZERO) TYPE_CIELO else TYPE_MARKET

            initDate?.let { start ->
                endDate?.let { end ->
                    presenterBank.callBanks(start, end, type)
                }
            }
        }
    }

    private fun showEmptyLastNegotiations() {
        tvListExtratoEmpty?.gone()
        rvLastNegotiations?.gone()
        negotiationEmpity?.visible()
    }

    private fun showLastNegotiations() {
        tvListExtratoEmpty?.gone()
        rvLastNegotiations?.visible()
        negotiationEmpity?.gone()
    }

    private fun showEmptyAmount() {
        tvAmountEmpty?.gone()
        titleAmount?.gone()
        inputAmount?.gone()
        verMais?.gone()
        negotiationEmpity?.visible()
    }

    private fun showAmountViews() {
        tvAmountEmpty?.gone()
        titleAmount?.visible()
        inputAmount?.visible()
        verMais?.visible()
        negotiationEmpity?.gone()
    }

    override fun serverError() {
        scrollContentLayout?.gone()
        errorView?.visible()
    }

    private fun processSummary(negotiations: Negotiations) {
        if (negotiations.summary?.totalAmount != null
            && negotiations.summary.totalAmount > ZERO_DOUBLE
        ) {
            negotiationEmpity?.visible()
            if (negotiations.items != null && negotiations.items.isNotEmpty()
            ) {
                typeTab?.let { type ->
                    if (type == ZERO) {
                        val listCielo = negotiations.items.filter { it.operationSourceCode == ONE }
                        if (listCielo.isNullOrEmpty().not()) {
                            showAmountViews()
                            inputAmount.text =
                                negotiations.summary.totalNetAmount.toPtBrWithNegativeRealString()
                        } else
                            showEmptyAmount()
                    } else {
                        val listMarket = negotiations.items.filter { it.operationSourceCode != ONE }
                        if (listMarket.isNullOrEmpty().not()) {
                            showAmountViews()
                            inputAmount.text =
                                negotiations.summary.totalNetAmount.toPtBrWithNegativeRealString()
                        } else
                            showEmptyAmount()
                    }
                }
            }
        } else
            showEmptyAmount()
    }

    private fun processItemsLastNegotiations(negotiations: Negotiations) {
        if (negotiations.items != null
            && negotiations.items.isNotEmpty()
        ) {
            showLastNegotiations()
            typeTab?.let { type ->
                if (type == ZERO) {
                    val listCielo = negotiations.items.filter { it.operationSourceCode == ONE }
                    if (listCielo.isNullOrEmpty().not())
                        populateLastNegotiations(listCielo)
                    else
                        showEmptyLastNegotiations()

                } else {
                    val listMarket = negotiations.items.filter { it.operationSourceCode != ONE }
                    if (listMarket.isNullOrEmpty().not())
                        populateLastNegotiations(listMarket)
                    else
                        showEmptyLastNegotiations()
                }
            }
        } else
            showEmptyLastNegotiations()
    }

    private fun parseNegotiationsForTab(dateInit: String?, dateEnd: String?) {
        typeTab?.let {
            val type = if (it == ZERO) TYPE_CIELO else TYPE_MARKET
            presenterBalcao.callApi(dateInit, dateEnd, type)
        }
    }

    private fun populateLastNegotiations(negotiations: List<Item>) {
        val listNegotiations =
            if (negotiations.size > FIVE) negotiations.take(FIVE) else negotiations
        rvLastNegotiations?.layoutManager = LinearLayoutManager(requireContext())
        rvLastNegotiations?.setHasFixedSize(true)
        val adapter =
            DefaultViewListAdapter(listNegotiations, R.layout.item_balcao_last_negotiations)
        adapter.setBindViewHolderCallback(object : DefaultViewListAdapter.OnBindViewHolder<Item> {
            override fun onBind(item: Item, holder: DefaultViewHolderKotlin) {
                holder.mView.tv_date_negociation.text = item.date.convertTimeStampToDate()
                holder.mView.tv_value_negociation.text = Utils.formatValue(item.netAmount)

                if (holder.adapterPosition == ZERO)
                    holder.mView.view_item_extrato_time_line_top_vertical.invisible()
                else if (holder.adapterPosition == negotiations.size - ONE)
                    holder.mView.view_item_extrato_time_line_vertical.invalidate()
            }
        })
        rvLastNegotiations?.adapter = adapter
    }

    override fun initProgressBanks() {
        loadingReceivablesBankAccounts?.visible()
        layoutBankCardError?.gone()
    }

    override fun finishedProgressBanks() {
        loadingReceivablesBankAccounts?.gone()
    }

    override fun showSuccessBanks(banks: NegotiationsBanks) {
        if (banks.items != null
            && banks.items.isNotEmpty()
        ) {
            loadingReceivablesBankAccounts?.gone()
            layoutBankCardError?.gone()
            listRecebimentosBancos?.visible()
            tvAmountEmptyBank?.gone()
            populateBanksNegotiations(banks.items)
        } else {
            loadingReceivablesBankAccounts?.gone()
            tvAmountEmptyBank?.visible()
            listRecebimentosBancos?.gone()
            layoutBankCardError?.gone()
        }
    }

    override fun serverErrorBanks() {
        loadingReceivablesBankAccounts?.gone()
        listRecebimentosBancos?.gone()
        layoutBankCardError?.visible()
    }

    private fun populateBanksNegotiations(banks: List<BankItem>) {
        listRecebimentosBancos?.layoutManager = LinearLayoutManager(requireContext())
        listRecebimentosBancos?.setHasFixedSize(true)
        val adapter = DefaultViewListAdapter(banks, R.layout.item_card_bancos)
        adapter.setBindItemViewType(object : DefaultViewListAdapter.OnBindItemViewType<BankItem> {
            override fun onBind(position: Int, item: BankItem): Int {
                if (item.code == IS_DIGITAL_ACCOUNT && item.agency == DEFAULT_DIGITAL_ACCOUNT && item.account == DEFAULT_DIGITAL_ACCOUNT) {
                    return R.layout.item_prepaid_card_bank_negotiation
                }
                return R.layout.item_card_bancos_negotiations
            }
        })
        adapter.setBindViewHolderCallback(object :
            DefaultViewListAdapter.OnBindViewHolder<BankItem> {

            override fun onBind(item: BankItem, holder: DefaultViewHolderKotlin) {
                if (holder.itemViewType == R.layout.item_card_bancos_negotiations) {
                    holder.mView.meus_recebimentos_nome_banco.text = item.name
                    holder.mView.meus_recebimentos_agencia.text =
                        getString(R.string.text_receivables_agency, item.agency)
                    holder.mView.meus_recebimentos_valor_deposito_banco.text =
                        item.netAmount?.toPtBrRealString()

                    val account = "${item.account}$SEPARATOR${item.accountDigit}"
                    holder.mView.meus_recebimentos_conta.text =
                        getString(R.string.text_receivables_account, account)
                } else
                    holder.mView.textPrepaidBalance.text = item.netAmount?.toPtBrRealString()
            }
        })
        listRecebimentosBancos?.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        presenterBank.onDestroy()
        presenterBalcao.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        presenterBank.onResume(typeTab)
        presenterBalcao.onResume()
    }

    override fun logScreenView(screenName: String) {
        analytics.logScreenView(
            screenName = screenName,
        )
    }

    override fun logException(screenName: String, error: NewErrorMessage) {
        analytics.logException(
            screenName = screenName,
            error = error
        )
    }

    companion object {
        const val IS_DIGITAL_ACCOUNT = "996"
        const val DEFAULT_DIGITAL_ACCOUNT = "0"
        private const val CATEGORY_TYPE_ARGS = "CATEGORY_TYPE_ARGS"
        private const val NEGOTIATION_ARGS = "NEGOTIATION_ARGS"
        private const val DATEINIT_ARGS = "DATEINIT_ARGS"
        private const val DATEEND_ARGS = "DATEEND_ARGS"
        private const val TYPE_CIELO = "CIELO"
        private const val TYPE_MARKET = "MARKET"
        private const val SEPARATOR = "-"

        fun newInstance(
            position: Int,
            dateInit: String? = null,
            dateEnd: String? = null,
            negotiations: Negotiations? = null,
            callBack: (Negotiations) -> Unit
        ): RecebiveisExtratoTypeFragment {
            val bundle = Bundle().apply {
                putInt(CATEGORY_TYPE_ARGS, position)
                negotiations?.let { putParcelable(NEGOTIATION_ARGS, it) }
                dateInit?.let { putString(DATEINIT_ARGS, it) }
                dateEnd?.let { putString(DATEEND_ARGS, it) }
            }
            return RecebiveisExtratoTypeFragment().apply {
                arguments = bundle
                this.callBack = callBack
            }
        }
    }
}