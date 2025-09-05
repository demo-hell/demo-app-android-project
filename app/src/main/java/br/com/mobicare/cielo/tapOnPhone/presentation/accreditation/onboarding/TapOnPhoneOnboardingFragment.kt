package br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentTapOnPhoneAccreditationOnboardingBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneAnalytics
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneGA4
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.onboarding.adapter.TapOnPhoneOnboardingAdapter
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.onboarding.adapter.TapOnPhoneOnboardingItem
import org.koin.android.ext.android.inject

class TapOnPhoneOnboardingFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null

    private var _binding: FragmentTapOnPhoneAccreditationOnboardingBinding? = null
    private val binding get() = _binding

    private val analytics: TapOnPhoneAnalytics by inject()
    private val ga4: TapOnPhoneGA4 by inject()

    private lateinit var items: List<TapOnPhoneOnboardingItem>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentTapOnPhoneAccreditationOnboardingBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupPagerView()
        listenerCallAction()
    }

    override fun onResume() {
        super.onResume()
        binding?.viewPagerOnboarding?.currentItem?.let { index ->
            items.getOrNull(index)?.title.orEmpty().let { title ->
                analytics.logOnboardingStep(index, title, javaClass)
                ga4.logOnBoardingStepScreenView(index, title)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.showBackIcon()
            navigation?.showHelpButton()
            navigation?.showCloseButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupPagerView() {
        items = listOf(
            TapOnPhoneOnboardingItem(
                title = getString(R.string.tap_on_phone_onboarding_title_1),
                subtitle = getString(R.string.tap_on_phone_onboarding_message_1),
                image = R.drawable.ic_48_light
            ),
            TapOnPhoneOnboardingItem(
                title = getString(R.string.tap_on_phone_onboarding_title_2),
                subtitle = getString(R.string.tap_on_phone_onboarding_message_2),
                image = R.drawable.ic_40
            ),
            TapOnPhoneOnboardingItem(
                title = getString(R.string.tap_on_phone_onboarding_title_3),
                subtitle = getString(R.string.tap_on_phone_onboarding_message_3),
                image = R.drawable.ic_36
            ),
            TapOnPhoneOnboardingItem(
                title = getString(R.string.tap_on_phone_onboarding_title_4),
                subtitle = getString(R.string.tap_on_phone_onboarding_message_4),
                image = R.drawable.ic_141_nfc
            )
        )

        binding?.apply {
            viewPagerOnboarding.adapter = TapOnPhoneOnboardingAdapter(items)
            indicatorOnboarding.setViewPager(viewPagerOnboarding)
        }
        setupListener(items)
    }

    private fun setupListener(listItems: List<TapOnPhoneOnboardingItem>) {
        binding?.viewPagerOnboarding?.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                controllerVisibleBottom(listItems.size - ONE == position)
                listItems.getOrNull(position)?.title.orEmpty().let { title ->
                    analytics.logOnboardingStep(position, title, this@TapOnPhoneOnboardingFragment.javaClass)
                    ga4.logOnBoardingStepScreenView(position, title)
                }
            }
        })
    }

    private fun controllerVisibleBottom(visible: Boolean) {
        binding?.apply {
            btnNextOnboardingTapOnPhone.visible(visible.not())
            btnLetsStartTapOnPhone.visible(visible)
        }
    }

    private fun listenerCallAction() {
        binding?.apply {
            btnNextOnboardingTapOnPhone.setOnClickListener(::onNextClick)
            btnLetsStartTapOnPhone.setOnClickListener(::onLetsStartClick)
        }
    }

    private fun onNextClick(view: View) {
        binding?.viewPagerOnboarding?.apply {
            currentItem += ONE
        }
    }

    private fun onLetsStartClick(view: View) {
        analytics.logScreenActions(
            flowName = TapOnPhoneAnalytics.ONBOARDING_NUMBER.format(items.size),
            labelName = binding?.btnLetsStartTapOnPhone?.text.toString()
        )
        ga4.logOnBoardingBeginCheckout(
            pageIndex = items.lastIndex,
            title = items.lastOrNull()?.title.orEmpty()
        )
        findNavController().navigate(
            TapOnPhoneOnboardingFragmentDirections.actionTapOnPhoneOnboardingFragmentToTapOnPhoneAccreditationOfferFragment()
        )
    }

}