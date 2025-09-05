package br.com.mobicare.cielo.newRecebaRapido.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import br.com.cielo.libflue.util.extensions.visible
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.domains.entities.OnboardingItem
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.databinding.FragmentReceiveAutomaticOnboardingBinding
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.newRecebaRapido.presentation.onboarding.adapter.OnBoardingReceiveAutomaticAdapter
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReceiveAutomaticOnBoardingFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentReceiveAutomaticOnboardingBinding? = null
    private val viewModel: ReceiveAutomaticOnBoardingViewModel by viewModel()
    private var navigation: CieloNavigation? = null

    private val ga4: RAGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentReceiveAutomaticOnboardingBinding.inflate(inflater, container, false)
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
        ga4.logScreenView(RAGA4.SCREEN_VIEW_INTRODUCTION)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this@ReceiveAutomaticOnBoardingFragment)
            navigation?.showHelpButton(isShow = false)
            navigation?.configureCollapsingToolbar(
                CollapsingToolbarBaseActivity.Configurator(
                    show = true,
                    isExpanded = false,
                    disableExpandableMode = true,
                    showBackButton = true,
                )
            )
        }
    }

    private fun setupPagerView() {
        val items = listOf(
            OnboardingItem(
                title = getString(R.string.receive_auto_intro_title_one),
                subtitle = getString(R.string.receive_auto_intro_description_one),
                image = R.drawable.img_153_onboard_rr_1
            ),
            OnboardingItem(
                title = getString(R.string.receive_auto_intro_title_two),
                subtitle = getString(R.string.receive_auto_intro_description_two),
                image = R.drawable.img_154_onboard_rr_2
            ),
            OnboardingItem(
                title = getString(R.string.receive_auto_intro_title_three),
                subtitle = getString(R.string.receive_auto_intro_description_three),
                image = R.drawable.img_155_onboard_rr_3
            )
        )

        binding?.apply {
            vpReceiveAutomaticOnBoarding.adapter = OnBoardingReceiveAutomaticAdapter(
                items = items
            )
            indicatorReceiveAutomaticOnBoarding.setViewPager(vpReceiveAutomaticOnBoarding)
        }

        setupListener(items)
    }

    private fun setupListener(listItems: List<OnboardingItem>) {
        binding?.vpReceiveAutomaticOnBoarding?.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                controllerButton(listItems.size - ONE == position)
            }
        })
    }

    private fun controllerButton(isVisible: Boolean) {
          binding?.apply {
              btnNextOnboarding.visible(isVisible.not())
              btnLetsGoHome.visible(isVisible)
          }
    }

    private fun setupListeners() {
        binding?.apply {
            btnLetsGoHome.setOnClickListener {
                viewModel.userViewReceiveAutomaticOnBoarding()
                ga4.logClick(
                    screenName = RAGA4.SCREEN_VIEW_INTRODUCTION,
                    contentComponent = RAGA4.AUTOMATIC_RECEIVE,
                    contentName = RAGA4.LETS_GO_LABEL
                )
            }
            btnNextOnboarding.setOnClickListener {
                vpReceiveAutomaticOnBoarding.currentItem += ONE
            }
        }
    }

    private fun setupObserver() {
        viewModel.getFastOnBoardingLiveData.observe(viewLifecycleOwner) {
            onShowHome()
        }
    }

    private fun onShowHome() {
        findNavController().safeNavigate(
            ReceiveAutomaticOnBoardingFragmentDirections
                .actionGetFastIntroFragmentToReceiveAutomaticHomeFragment()

        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}