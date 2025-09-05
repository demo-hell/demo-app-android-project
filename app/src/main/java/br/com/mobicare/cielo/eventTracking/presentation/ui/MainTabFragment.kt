package br.com.mobicare.cielo.eventTracking.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.TECHNICAL_SUPPORT_MY_REQUESTS
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.FragmentMainTabBinding
import br.com.mobicare.cielo.eventTracking.presentation.ui.callsRequest.CallsRequestFragment
import br.com.mobicare.cielo.eventTracking.presentation.ui.machineRequest.MachineRequestFragment
import br.com.mobicare.cielo.eventTracking.utils.ScreenData
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import com.google.android.material.tabs.TabLayoutMediator
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4


class MainTabFragment : BaseFragment(), CieloNavigationListener {

    private val binding: FragmentMainTabBinding by viewBinding()
    private var navigation: CieloNavigation? = null
    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        GA4.logScreenView(TECHNICAL_SUPPORT_MY_REQUESTS)
        savedInstanceState?.getInt(CURRENT_TAB_POSITION)?.let { tabPosition ->
            binding.eventViewPager.post {
                binding.eventViewPager.currentItem = tabPosition
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val currentTabPosition = binding.eventViewPager.currentItem
        outState.putInt(CURRENT_TAB_POSITION, currentTabPosition)
    }

    override fun onResume() {
        super.onResume()
        setupNavigation()
        if (binding.eventViewPager.adapter == null) {
            setupViewPager()
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showHelpButton(isShow = true)
            navigation?.showContainerButton(isShow = false)
        }
    }

    private fun setupViewPager() {
        if (binding.eventViewPager.adapter == null) {
            val isCallsEnabled = FeatureTogglePreference.instance.getFeatureTogle(
                FeatureTogglePreference.GENERAL_EVENT_TRACKING)

            val fragmentsData = mutableListOf(
                ScreenData(getString(R.string.tab_title_machines), true) { MachineRequestFragment() },
                ScreenData(getString(R.string.tab_title_calls), isCallsEnabled) { CallsRequestFragment() })

            val adapter = EventTrackingPagerAdapter(childFragmentManager, lifecycle, fragmentsData)
            binding.eventViewPager.adapter = adapter

            tabLayoutMediator?.detach()
            tabLayoutMediator = TabLayoutMediator(binding.eventsTabLayout, binding.eventViewPager) { tab, position ->
                tab.text = adapter.getEnabledTitles()[position]
            }.also { it.attach() }
        }
    }

    companion object {
        private const val CURRENT_TAB_POSITION = "current_tab_position"
    }
}