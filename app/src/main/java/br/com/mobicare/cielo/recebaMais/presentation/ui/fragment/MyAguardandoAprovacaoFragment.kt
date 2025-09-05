package br.com.mobicare.cielo.recebaMais.presentation.ui.fragment

import android.os.Bundle
import android.view.*
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.recebaMais.GA_RM_AGUARDANDO_APROVACAO_SCREEN
import br.com.mobicare.cielo.recebaMais.GA_RM_RECEBA_MAIS
import br.com.mobicare.cielo.recebaMais.RM_HELP_ID
import br.com.mobicare.cielo.recebaMais.domains.entities.Contract
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanContract
import kotlinx.android.synthetic.main.include_credito_em_aprovacao.*


class MyAguardandoAprovacaoFragment : BaseFragment(){

     lateinit var contract: Contract
     lateinit var listener: UserLoanContract.View.UserLoanScreen

    companion object {
        fun create(contract: Contract, listener: UserLoanContract.View.UserLoanScreen): MyAguardandoAprovacaoFragment {
            return MyAguardandoAprovacaoFragment().apply {
                this.contract = contract
                this.listener = listener
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cielo_credito_em_aprovacao, container, false)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_receba_mais_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_help) {
            sendGaHelpSelected()
            HelpMainActivity.create(activity!!, getString(R.string.text_rm_help_title), RM_HELP_ID)
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().hideSoftKeyboard()
        initView()
        sendGaScreenView()
    }

    private fun initView() {
        bt_Update_aguardando_aprovacao.setOnClickListener {
            listener.showSimulation()
        }
    }

    //region Event Ga

    private fun sendGaScreenView() {
        Analytics.trackScreenView(
            screenName = GA_RM_AGUARDANDO_APROVACAO_SCREEN,
            screenClass = this.javaClass
        )
    }

    private fun sendGaHelpSelected() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, GA_RM_RECEBA_MAIS),
            action = listOf(Action.HEADER),
            label = listOf(Label.TOOLTIP)
        )
    }

    //endregion


}