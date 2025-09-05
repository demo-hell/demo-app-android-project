package br.com.mobicare.cielo.p2m.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.constants.WhatsApp
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.databinding.FragmentP2mEndBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.p2m.analytics.P2MAnalytics
import br.com.mobicare.cielo.p2m.analytics.P2MGA4
import br.com.mobicare.cielo.p2m.domain.model.TaxModel
import br.com.mobicare.cielo.p2m.presentation.viewmodel.P2mAcreditationViewModel
import br.com.mobicare.cielo.p2m.utils.UiTaxTextState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class P2mEndFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentP2mEndBinding? = null
    private val viewModel: P2mAcreditationViewModel by viewModel()
    private val analytics: P2MAnalytics by inject()
    private val ga4: P2MGA4 by inject()
    private var navigation: CieloNavigation? = null
    private var urlWhatsApp: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentP2mEndBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        observeTaxTextState()
        initViewModel()
    }

    override fun onResume() {
        super.onResume()
        analytics.logScreenView(
            name = P2MAnalytics.SCREENVIEW_P2M_END_REGISTER_ON_WHATS_APP,
            className = this.javaClass
        )
        ga4.logScreenView(P2MGA4.SCREEN_VIEW_P2M_SUCCESS)
    }

    private fun initViewModel() {
        viewModel.getFeatureToggle()
    }

    private fun observeTaxTextState() {
        viewModel.featureToggle.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiTaxTextState.Success -> {
                    state.data?.let { onSuccess(it) }
                }
            }
        }
    }

    private fun onSuccess(taxModel: TaxModel) {
        getUrlWhatsAppBusiness(taxModel)
    }

    private fun getUrlWhatsAppBusiness(taxModel: TaxModel) {
        val url = taxModel.urls.whatsapp_business
        urlWhatsApp = if (url.isEmpty()) {
            WhatsApp.LINK_TO_WHATS_APP_BUSINESS
        } else {
            url
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showContainerButton(isShow = true)
            navigation?.showButton(true)
            navigation?.setTextButton(getString(R.string.btn_open_whatsapp_p2m))
            navigation?.configureCollapsingToolbar(
                CollapsingToolbarBaseActivity.Configurator(
                    show = true,
                    isExpanded = false,
                    disableExpandableMode = false,
                    toolbarMenu = CollapsingToolbarBaseActivity.ToolbarMenu(
                        menuRes = R.menu.menu_common_close_blue,
                        onOptionsItemSelected = {
                            analytics.logScreenActions(
                                P2MAnalytics.P2M_ANALITYCS_END_REGISTER,
                                EMPTY_STRING,
                                Label.BOTAO,
                                P2MAnalytics.P2M_ANALITYCS_CLOSE
                            )
                            returnHome()
                        }
                    ),
                    showBackButton = false
                ))
        }
    }

    override fun onButtonClicked(labelButton: String) {
        analytics.logScreenActions(
            P2MAnalytics.P2M_ANALITYCS_END_REGISTER,
            EMPTY_STRING,
            Label.BOTAO,
            P2MAnalytics.P2M_ANALITYCS_OPEN_WHATS_APP_BNS
        )
        goToOpenWhatsAppBusiness()
    }

    private fun goToOpenWhatsAppBusiness() {
        Utils.openBrowser(requireActivity(), urlWhatsApp)
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            })
        return false
    }

    private fun returnHome() {
        requireActivity().backToHome()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}


