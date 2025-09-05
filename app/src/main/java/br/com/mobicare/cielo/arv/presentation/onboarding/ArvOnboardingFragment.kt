package br.com.mobicare.cielo.arv.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4
import br.com.mobicare.cielo.arv.analytics.ArvAnalyticsGA4Constants.SCREEN_VIEW_ARV_INTRODUCTION
import br.com.mobicare.cielo.arv.presentation.ArvEffectiveTimeViewModel
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.OnboardingItem
import br.com.mobicare.cielo.commons.enums.Onboarding
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.OnboardingAdapter
import br.com.mobicare.cielo.databinding.FragmentArvOnboardingBinding
import br.com.mobicare.cielo.extensions.safeNavigate
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ArvOnboardingFragment : BaseFragment(), CieloNavigationListener {

    private val viewModel: ArvOnboardingViewModel by viewModel()
    private val effetiveTimeViewModel: ArvEffectiveTimeViewModel by viewModel()
    private var binding: FragmentArvOnboardingBinding? = null
    private val arvAnalytics: ArvAnalyticsGA4 by inject()
    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentArvOnboardingBinding.inflate(inflater, container, false)
        .also { fragment ->
            binding = fragment
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupPagerView()
        setupListeners()
        setupObserver()
    }

    override fun onResume() {
        super.onResume()
        trackScreenView(
            binding?.vpArvOnboarding?.currentItem ?: ZERO
        )
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showHelpButton(isShow = false)
            navigation?.setupToolbar(isCollapsed = false)
            navigation?.showContainerButton(isShow = false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupPagerView() {
        val items = listOf(
            OnboardingItem(
                title = getString(R.string.anticipation_what_is_it_title),
                subtitle = getString(R.string.anticipation_what_is_it_message),
                image = R.drawable.ic_147_anticipation
            ),
            OnboardingItem(
                title = getString(R.string.anticipation_attention_to_the_schedule_title),
                subtitle = getString(
                    R.string.anticipation_attention_to_the_schedule_message,
                    effetiveTimeViewModel.arvEffectiveTimeLiveData.value
                ),
                image = R.drawable.ic_148_anticipation
            ),
            OnboardingItem(
                title = getString(R.string.anticipation_brand_title),
                subtitle = getString(R.string.anticipation_brand_message),
                image = R.drawable.ic_149_anticipation
            )
        )

        binding?.apply {
            vpArvOnboarding.adapter = OnboardingAdapter(
                items = items,
                layout = R.layout.layout_pix_keys_onboarding_item,
                id = Onboarding.DEFAULT.id,
                titleStyleRes = R.style.Label_700_brand_600_montserrat_bold_center
            )
            indicatorArvOnboarding.setViewPager(vpArvOnboarding)
        }

        setupListener(items)
    }

    private fun setupListener(listItems: List<OnboardingItem>) {
        binding?.vpArvOnboarding?.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                controllerButton(listItems.size - ONE == position)
                arvAnalytics.logScreenView(
                    SCREEN_VIEW_ARV_INTRODUCTION,
                    (position + 1).toString()
                )
            }
        })
    }

    private fun controllerButton(visible: Boolean) {
        binding?.apply {
            if (visible) {
                btnNextArvOnboarding.gone()
                btnLetsGoArvHome.visible()
            } else {
                btnNextArvOnboarding.visible()
                btnLetsGoArvHome.gone()
            }
        }
    }

    private fun setupListeners() {
        binding?.apply {
            btnLetsGoArvHome.setOnClickListener {
                viewModel.userViewArvOnboarding()
            }

            btnNextArvOnboarding.setOnClickListener {
                vpArvOnboarding.currentItem = vpArvOnboarding.currentItem + ONE
            }
        }
    }

    private fun setupObserver() {
        viewModel.arvOnboardingLiveData.observe(viewLifecycleOwner) {
            onShowHome()
        }
    }

    private fun onShowHome() {
        findNavController().safeNavigate(
            ArvOnboardingFragmentDirections
                .actionArvOnboardingFragmentToArvHomeFragment()
        )
    }

    private fun trackScreenView(position: Int) {
        arvAnalytics.logScreenView(
            SCREEN_VIEW_ARV_INTRODUCTION,
            (position + 1).toString()
        )
    }
}