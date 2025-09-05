package br.com.mobicare.cielo.extrato.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.constants.CreditCard.DATE_TIME_LINE
import br.com.mobicare.cielo.commons.constants.CreditCard.PROXY_CARD
import br.com.mobicare.cielo.commons.constants.CreditCard.READ_ONLY_STATEMENTS
import br.com.mobicare.cielo.commons.constants.CreditCard.STATEMENT_FOOTER
import br.com.mobicare.cielo.commons.constants.CreditCard.WITH_CREDIT_CARD_STATEMENTS
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.ActivityDetector.Companion.getActivityDetector
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTransicaoObj
import br.com.mobicare.cielo.extrato.presentation.presenter.ExtratoTimeLinePresenter
import br.com.mobicare.cielo.extrato.presentation.ui.ExtratoTimeLineContract
import br.com.mobicare.cielo.extrato.presentation.ui.adapters.EndlessRecyclerViewScrollListener
import br.com.mobicare.cielo.extrato.presentation.ui.adapters.TimeLineAdapter
import kotlinx.android.synthetic.main.content_error.*
import kotlinx.android.synthetic.main.content_extrato_footer.*
import kotlinx.android.synthetic.main.fragment_extrato_time_line.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class ExtratoTimeLineFragment : BaseFragment(), ExtratoTimeLineContract.View, TimeLineAdapter.OnClickExtratoItemListener {

    private val presenter: ExtratoTimeLinePresenter by inject {
        parametersOf(this)
    }
    private val readOnlyStatements: Boolean?
        get() = arguments?.getBoolean(READ_ONLY_STATEMENTS, false)

    var date: String? = null
    private var proxyCard: String = "00"
    private var scrollEventAdded = false
    private var reload: Boolean = true

    var statementListener: OnStatementSelectedListener? = null

    interface OnStatementSelectedListener {
        fun onStatementSelect(statement: ExtratoTransicaoObj)
    }

    companion object {
        fun getInstance(date: String? = null,
                        statementFooter: Boolean = true,
                        proxyCard: String? = "00",
                        withCreditCardStatements: Boolean = false,
                        readOnlyStatements: Boolean = false): ExtratoTimeLineFragment {
            val fragment = ExtratoTimeLineFragment()
            val bundle = Bundle()
            bundle.putString(DATE_TIME_LINE, date)
            bundle.putString(PROXY_CARD, proxyCard)

            bundle.putBoolean(STATEMENT_FOOTER, statementFooter)
            bundle.putBoolean(READ_ONLY_STATEMENTS, readOnlyStatements)
            bundle.putBoolean(WITH_CREDIT_CARD_STATEMENTS, withCreditCardStatements)

            fragment.arguments = bundle
            return fragment
        }
    }

    override fun logout(error: String?) {
        if (isAttached()) {
            AlertDialogCustom.Builder(this.context, getString(R.string.ga_extrato_day))
                    .setTitle(R.string.ga_extrato)
                    .setMessage(error)
                    .setCancelable(false)
                    .setBtnRight(getString(R.string.ok))
                    .setOnclickListenerRight {
                        if (isAttached()) {
                            Utils.logout(requireActivity())
                        }
                    }
                    .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPause() {
        super.onPause()
        setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_extrato_time_line, container, false)

    override fun onStart() {
        super.onStart()
        val bundle = this.arguments
        if (bundle != null) {
            date = bundle.getString(DATE_TIME_LINE)
            proxyCard = bundle.getString(PROXY_CARD) ?: proxyCard
        }

        addScrollEvent()

        if (reload) {
            presenter.callAPI(date, proxyCard)
            reload = false
        }
    }

    override fun addScrollEvent() {
        if (scrollEventAdded.not()) {
            val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            recycler_view_extrato_time_line.layoutManager = linearLayoutManager

            recycler_view_extrato_time_line.addOnScrollListener(object :
                    EndlessRecyclerViewScrollListener(linearLayoutManager) {
                override fun onLoadMore() {
                    val adapter = recycler_view_extrato_time_line.adapter as TimeLineAdapter
                    if (!adapter.mIsLoading) {
                        presenter.callAPI(date, proxyCard)
                        adapter.showLoading()
                    }
                }
            })

            scrollEventAdded = true
        }
    }

    override fun removeScrollEvent() {
        recycler_view_extrato_time_line.clearOnScrollListeners()
    }

    override fun showProgress() {
        progress_extrato_list.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progress_extrato_list.visibility = View.GONE
    }

    override fun showError(error: ErrorMessage) {
        if (isAttached()) {
            Analytics.trackError(getActivityDetector().screenCurrentPath(), error)

            if (presenter.nextPage > ONE) {
                AlertDialogCustom.Builder(requireContext(), getString(R.string.ga_extrato_day))
                        .setMessage(error.message)
                        .setBtnRight(getString(R.string.ok))
                        .show()
                (recycler_view_extrato_time_line.adapter as TimeLineAdapter).hideLoading()
            } else {
                relativeExtratoTimelineError.visibility = View.VISIBLE
                text_view_error_msg.text = error.message
                onClickTryAgain()
            }
        }
    }

    private fun onClickTryAgain() {
        content_extrato_footer.visibility = View.GONE
        button_error_try.setOnClickListener {
            relativeExtratoTimelineError.visibility = View.GONE
            presenter.callAPI(date, proxyCard)
        }
    }

    override fun showEmptyMsg(msgId: Int) {
        hideProgress()
        (recycler_view_extrato_time_line.adapter as? TimeLineAdapter)?.let { adapter ->
            adapter.hideLoading()
        }
        text_view_error_msg.text = getString(msgId)
        relativeExtratoTimelineError.visibility = View.VISIBLE
        button_error_try.text = getString(R.string.home_minhas_vendas_atualizar)
        onClickTryAgain()
    }

    override fun loadFooter(quantity: Int, amount: String?) {
        content_extrato_footer.visibility = View.VISIBLE
        var messageResource: Int = R.string.extrato_quantidade_vendas_plural
        if (quantity == ZERO) {
            messageResource = R.string.extrato_quantidade_vendas_zero
        } else if (quantity == ONE) {
            messageResource = R.string.extrato_quantidade_vendas_singular
        }
        textview_extrato_footer_quantity.text = resources.getString(messageResource, String.format("%02d", quantity))
        textview_extrato_footer_amount.text = amount
    }

    override fun loadTimeLine(transactions: ArrayList<ExtratoTransicaoObj>?) {
        recycler_view_extrato_time_line.layoutManager = (androidx.recyclerview.widget.LinearLayoutManager(context))

        if (readOnlyStatements != null && readOnlyStatements == true) {
            recycler_view_extrato_time_line.adapter = (TimeLineAdapter(R.layout.item_statement_read_only,
                    requireActivity(), transactions, this))
        } else {
            recycler_view_extrato_time_line.adapter = (TimeLineAdapter(R.layout.item_extrato_time_line,
                    requireActivity(), transactions, this))
        }
    }

    override fun appendTimeLine(transactions: ArrayList<ExtratoTransicaoObj>) {
        if (recycler_view_extrato_time_line.adapter == null) {
            loadTimeLine(transactions)
        } else {
            (recycler_view_extrato_time_line.adapter as TimeLineAdapter).appendList(transactions)
            (recycler_view_extrato_time_line.adapter as TimeLineAdapter).hideLoading()
        }
    }

    override fun onClickItem(item: ExtratoTransicaoObj) {
        statementListener?.onStatementSelect(item)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Analytics.trackScreenView(
                screenName = getActivityDetector().screenCurrentPath(),
                screenClass = this.javaClass
        )
    }
}