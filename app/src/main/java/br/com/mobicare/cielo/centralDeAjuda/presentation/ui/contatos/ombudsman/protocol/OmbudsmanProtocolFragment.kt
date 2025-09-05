package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.protocol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.alert.CieloAskQuestionDialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics
import br.com.mobicare.cielo.chat.domains.EnumFeatures
import br.com.mobicare.cielo.chat.presentation.ui.ChatDialog
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_ombudsman_protocol.*
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

private const val TAG = "OmbudsmanProtocolFragment"

class OmbudsmanProtocolFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_ombudsman_protocol, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        trackScreenView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_toolbar_ombudsman))
            navigation?.showButton()
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupListeners() {
        btn_not_protocol?.setOnClickListener {
            GA4.logOmbudsmanDisplayContent()
            CieloAskQuestionDialogFragment
                    .Builder()
                    .title(getString(R.string.text_title_dialog_no_protocol))
                    .message(getString(R.string.text_subtitle_dialog_no_protocol))
                    .cancelTextButton(getString(R.string.cancelar))
                    .positiveTextButton(getString(R.string.text_btn_dialog_no_protocol))
                    .build().let {
                        it.onPositiveButtonClickListener = View.OnClickListener {
                            GA4.logOmbudsmanClick(getString(R.string.text_btn_dialog_no_protocol))
                            ChatDialog.showDialog(requireActivity(), EnumFeatures.CHAT, TAG)
                        }
                        it.show(childFragmentManager, TAG)
                    }
        }

        btn_with_protocol?.setOnClickListener {
            findNavController().navigate(
                    OmbudsmanProtocolFragmentDirections.actionOmbudsmanProtocolFragmentToOmbudsmanPersonalDataFragment())
        }
    }

    private fun trackScreenView(){
        if (isAttached()) {
            GA4.logScreenView(TechnicalSupportAnalytics.ScreenView.HELP_CENTER_OMBUDSMAN)
        }
    }
}