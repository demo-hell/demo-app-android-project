package br.com.mobicare.cielo.recebaMais.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity.Companion.CLOSE_ACTIVITIES_FROM_BACKSTACK
import br.com.mobicare.cielo.commons.ui.help.HelpMainActivity
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.recebaMais.GA_RM_CREDITO_NAO_APROVADO_SCREEN
import br.com.mobicare.cielo.recebaMais.GA_RM_RECEBA_MAIS
import br.com.mobicare.cielo.recebaMais.RM_HELP_ID
import kotlinx.android.synthetic.main.fragment_cielo_credito_nao_aprovado.*


class MyCreditoNaoAprovadoFragment : BaseFragment() {

    companion object {
        fun create(): MyCreditoNaoAprovadoFragment {
            return MyCreditoNaoAprovadoFragment()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cielo_credito_nao_aprovado, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_receba_mais_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_help) {
            sendGaHelpSelected()

            HelpMainActivity.create(requireActivity(), getString(R.string.text_rm_help_title), RM_HELP_ID)
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().hideSoftKeyboard()

        sendGaScreenView()
        buttonInfoAction.setText("Início")

        buttonInfoAction.setOnClickListener {
            LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(Intent(MainBottomNavigationActivity
                            .NAVIGATE_TO_ACTION)
                            .apply {
                                this.putExtra(MainBottomNavigationActivity.HOME_INDEX_KEY,
                                        MainBottomNavigationActivity.HOME_INDEX)
                            })
            LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(Intent(CLOSE_ACTIVITIES_FROM_BACKSTACK))
        }
    }

    //region Event Ga

    private fun sendGaScreenView() {
        Analytics.trackScreenView(
            screenName = GA_RM_CREDITO_NAO_APROVADO_SCREEN,
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