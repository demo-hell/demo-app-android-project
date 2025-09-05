package br.com.mobicare.cielo.home.presentation.arv.ui.customview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_HOME_ARV_MODAL_RANGE
import br.com.mobicare.cielo.arv.domain.model.ArvAnticipation
import br.com.mobicare.cielo.arv.presentation.ArvNavigationFlowActivity
import br.com.mobicare.cielo.arv.utils.ArvConstants.ARV_ANTICIPATION
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.FragmentArvCardAlertHomeBinding
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4.Companion.SCREEN_VIEW_HOME
import br.com.mobicare.cielo.home.presentation.arv.viewmodel.ArvCardAlertHomeViewModel
import br.com.mobicare.cielo.home.presentation.arv.viewmodel.UiArvCardAlertState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArvCardAlertHomeFragment : BaseFragment() {
    private val viewModel: ArvCardAlertHomeViewModel by viewModel()
    private val arvAnalytics: ArvAnalyticsGA4 by inject()
    private var arvCieloAnticipation: ArvAnticipation? = null

    private val binding: FragmentArvCardAlertHomeBinding by viewBinding()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideView()
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewModel.getArvCardInformation()
    }

    override fun onResume() {
        super.onResume()
        trackScreenView()
    }

    private fun trackScreenView() {
        if (binding.root.isVisible()) {
            arvAnalytics.logViewPromotion(SCREEN_VIEW_HOME)
        }
    }

    private fun setupObservers() {
        viewModel.arvCardAlertLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiArvCardAlertState.ShowArvCardAlert -> {
                    configureArvCardAlertView(uiState.arvAnticipation)
                }
                is UiArvCardAlertState.HideArvCardAlert -> hideView()
                is UiArvCardAlertState.Error -> {
                    hideView()
                    trackError(uiState.error)
                }
                else -> hideView()
            }
        }
    }

    private fun hideView() {
        binding.root.gone()
    }

    private fun showView() {
        binding.root.visible()
    }

    private fun trackPromotionClick() {
        arvAnalytics.logPromotionClick()
    }

    private fun trackError(error: NewErrorMessage) {
        arvAnalytics.logException(
            screenName = SCREEN_VIEW_HOME_ARV_MODAL_RANGE,
            error = error,
        )
    }

    private fun configureArvCardAlertView(arvAnticipation: ArvAnticipation) {
        arvCieloAnticipation = arvAnticipation

        with(binding) {
            btArvAlert.setOnClickListener {
                trackPromotionClick()
                navigateToArvSingleAnticipation()
            }

            val description = getString(R.string.card_home_arv_alert_description, arvAnticipation.grossAmount?.toPtBrRealString())
            tvArvAlertDescription.text = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            showView()
        }
    }

    private fun navigateToArvSingleAnticipation() {
        val intent =
            Intent(requireContext(), ArvNavigationFlowActivity::class.java).apply {
                putExtra(ARV_ANTICIPATION, arvCieloAnticipation)
            }
        requireActivity().startActivity(intent)
    }

    companion object {
        fun newInstance(): ArvCardAlertHomeFragment {
            return ArvCardAlertHomeFragment()
        }
    }
}
