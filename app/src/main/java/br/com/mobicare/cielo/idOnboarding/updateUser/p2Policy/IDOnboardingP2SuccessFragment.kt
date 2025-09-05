package br.com.mobicare.cielo.idOnboarding.updateUser.p2Policy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingP2SuccessBinding
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_DOCUMENTS_SENT_ANALYSIS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_DOCUMENTS_SENT_SUCCESS
import org.koin.android.ext.android.inject

class IDOnboardingP2SuccessFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null
    private var binding: FragmentIdOnboardingP2SuccessBinding? = null
    private val analyticsGA: IDOnboardingP2AnalyticsGA by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentIdOnboardingP2SuccessBinding
        .inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupClickListeners()
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_DOCUMENTS_SENT_ANALYSIS)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupClickListeners() {
        binding?.apply {
            analyticsGA.logIDP2SuccessDisplay()
            btnClose.setOnClickListener {
                baseLogout()
            }
            btnNext.setOnClickListener {
                baseLogout()
            }
        }
    }

    override fun onBackButtonClicked(): Boolean {
        baseLogout()
        return super.onBackButtonClicked()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}