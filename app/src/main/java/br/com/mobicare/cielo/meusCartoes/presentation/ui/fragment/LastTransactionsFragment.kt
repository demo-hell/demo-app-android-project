package br.com.mobicare.cielo.meusCartoes.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.CreditCard.DAY_30
import br.com.mobicare.cielo.commons.constants.CreditCard.PROXY_CARD
import br.com.mobicare.cielo.commons.constants.CreditCard.TITLE_CARD_ARGS
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.SCREEN_CURRENT_PATH
import br.com.mobicare.cielo.commons.utils.configure
import br.com.mobicare.cielo.commons.utils.daysFrom
import br.com.mobicare.cielo.commons.utils.format
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTransicaoObj
import br.com.mobicare.cielo.extrato.presentation.ui.adapters.TimeLineAdapter
import br.com.mobicare.cielo.meusCartoes.presentation.ui.LastTransactionsContract
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.CreditCardsStatementActivity
import br.com.mobicare.cielo.meusCartoes.presenter.LastTransactionsPresenter
import kotlinx.android.synthetic.main.card_error.*
import kotlinx.android.synthetic.main.content_ver_mais.*
import kotlinx.android.synthetic.main.fragment_last_transactions.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.util.*

class LastTransactionsFragment : BaseFragment(), LastTransactionsContract.View {

    private val presenter: LastTransactionsPresenter by inject {
        parametersOf(this)
    }
    private val proxy: String? by lazy {
        arguments?.getString(PROXY_CARD)
    }

    companion object {
        fun create(currentPath: String,
                   proxyCard: String,
                   title: String? = null): LastTransactionsFragment {

            val lastTransFrag = LastTransactionsFragment()
            val param = Bundle()

            param.putString(PROXY_CARD, proxyCard)
            title?.let { param.putString(TITLE_CARD_ARGS, it) }
            currentPath.let { param.putString(SCREEN_CURRENT_PATH, it) }
            lastTransFrag.arguments = param
            return lastTransFrag
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_last_transactions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListener()
    }

    override fun onResume() {
        super.onResume()

        presenter.onResume()
        getStatements()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    private fun setupListener() {
        linearSeeMoreContent?.setOnClickListener {
            requireActivity()
                    .startActivity<CreditCardsStatementActivity>(PROXY_CARD to
                            arguments?.getString(PROXY_CARD),
                            TITLE_CARD_ARGS to arguments?.getString(TITLE_CARD_ARGS, getString(R.string.text_my_cards_title))
                    )
        }

        textUpdateTransactions?.setOnClickListener {
            getStatements()
        }

        text_view_card_error_msg?.text = getString(R.string.error_try_again)
        button_try?.setOnClickListener {
            getStatements()
        }
    }

    private fun getStatements() {
        presenter.fetchStatements(initialDt = Date().daysFrom(DAY_30),
                finalDt = Date().format(),
                proxyCard = proxy)
    }

    override fun showLoading() {
        if (isAdded) {
            layout_card_error.visibility = View.GONE
            progressLastTransactions.visibility = View.VISIBLE
            linearStatementsContent.visibility = View.GONE
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            progressLastTransactions.visibility = View.INVISIBLE
            linearStatementsContent.visibility = View.VISIBLE
        }
    }

    override fun showError() {
        if (isAttached()) {
            layout_card_error.visibility = View.VISIBLE
            progressLastTransactions.visibility = View.INVISIBLE
            linearStatementsListContent.visibility = View.GONE
        }
    }

    override fun showTransactions(transactions: List<ExtratoTransicaoObj>?) {
        if (isAttached()) {
            linearStatementsListContent.visibility = View.VISIBLE
            recyclerStatementsList.configure(requireActivity(),
                    TimeLineAdapter(R.layout.item_extrato_time_line,
                            requireContext(), transactions, null, true))
            text_view_error_msg.visibility = View.GONE
            linearErrorNotTransaction.visibility = View.GONE
        }
    }

    override fun showMessageNotTransactions() {
        text_view_error_msg.text = getText(R.string.extrato_meus_cartoes_empty)
        text_view_error_msg.visibility = View.VISIBLE
        linearErrorNotTransaction.visibility = View.VISIBLE
    }
}