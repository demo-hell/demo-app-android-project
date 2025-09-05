package br.com.mobicare.cielo.openFinance.presentation.manager

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.OnboardingItem
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.databinding.OpenFinanceOnboardingActivityBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.openFinance.presentation.manager.adapter.OnboardingOPFAdapter
import org.koin.android.ext.android.inject

class OpenFinanceOnBoardingActivity : BaseLoggedActivity(), CieloNavigation {

    private var _binding: OpenFinanceOnboardingActivityBinding? = null
    private val binding get() = _binding
    private var navigationListener: CieloNavigationListener? = null
    private val preferences: UserPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = OpenFinanceOnboardingActivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setupPagerView()
        listenerCallAction()
    }

    private fun setupPagerView() {
        val items = listOf(
            OnboardingItem(
                title = getString(R.string.to_know_opf_onboarding),
                subtitle = getString(R.string.to_know_opf_onboarding_desc),
                image = R.drawable.ic_opf_onboarding
            ),
            OnboardingItem(
                title = getString(R.string.payments_opf_onboarding),
                subtitle = getString(R.string.payments_opf_onboarding_desc),
                image = R.drawable.ic_opf_transfer
            ),
            OnboardingItem(
                title = getString(R.string.practicality_opf_onboarding),
                subtitle = getString(R.string.practicality_opf_onboarding_desc),
                image = R.drawable.img_155_onboard_rr_3
            ),
        )

        binding?.apply {
            viewPagerOpenFinanceOnboarding.adapter = OnboardingOPFAdapter(
                items = items
            )
            indicatorOpenFinanceOnboarding.setViewPager(viewPagerOpenFinanceOnboarding)
        }

        setupListener(items)
    }

    private fun setupListener(listItems: List<OnboardingItem>) {
        binding?.viewPagerOpenFinanceOnboarding?.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                controllerVisibleBottom(listItems.size - ONE == position)
            }
        })
    }

    private fun controllerVisibleBottom(visible: Boolean) {
        binding?.apply {
            btnNextOpenFinanceOnboarding.visible(visible.not())
            btnShowOpenFinanceOnboarding.visible(visible)
        }
    }

    private fun listenerCallAction() {
        binding?.apply {
            btnNextOpenFinanceOnboarding.setOnClickListener {
                viewPagerOpenFinanceOnboarding.currentItem =
                    viewPagerOpenFinanceOnboarding.currentItem + ONE
            }
            btnShowOpenFinanceOnboarding.setOnClickListener {
                preferences.saveOnboardingOpenFinanceWasViewed()
                finish()
            }
            btnBack.setOnClickListener {
                when (viewPagerOpenFinanceOnboarding.currentItem) {
                    ZERO -> finish()
                    else -> viewPagerOpenFinanceOnboarding.currentItem =
                        viewPagerOpenFinanceOnboarding.currentItem - ONE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        preferences.saveOnboardingOpenFinanceWasViewed()
        _binding = null
    }

    override fun setNavigationListener(listener: CieloNavigationListener) {
        navigationListener = listener
    }
}