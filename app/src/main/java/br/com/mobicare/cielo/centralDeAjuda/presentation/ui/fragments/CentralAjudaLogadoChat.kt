package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chat.domains.EnumFeatures
import br.com.mobicare.cielo.chat.presentation.ui.ChatDialog
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.HelpCenter.CANAIS_ATENDIMENTO
import br.com.mobicare.cielo.commons.constants.HelpCenter.HELP_CENTER
import br.com.mobicare.cielo.commons.constants.HelpCenter.HELP_CENTE_CAPITALIZE
import br.com.mobicare.cielo.commons.constants.HelpCenter.CHAT
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.listener.LogoutListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import kotlinx.android.synthetic.main.central_ajuda_logado_chat_fragment.*

class CentralAjudaLogadoChat : BaseFragment() {

    private var logoutListener: LogoutListener? = null

    companion object {
        fun create(logoutListener: LogoutListener): CentralAjudaLogadoChat {
            val fragment = CentralAjudaLogadoChat()
            fragment.logoutListener = logoutListener
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.central_ajuda_logado_chat_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        card_view_main?.setOnClickListener {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, HELP_CENTER),
                action = listOf(CANAIS_ATENDIMENTO),
                label = listOf(Label.BOTAO, CHAT)
            )

            val accessToken = UserPreferences.getInstance()?.token!!
            val ec = UserPreferences.getInstance().ecUserLogged

            if (accessToken.isEmpty() || ec.isEmpty()) {
                logoutListener?.onLogout()
            } else {
                ChatDialog.showDialog(requireActivity(), EnumFeatures.CHAT, ec, accessToken, HELP_CENTE_CAPITALIZE)
            }
        }
    }
}