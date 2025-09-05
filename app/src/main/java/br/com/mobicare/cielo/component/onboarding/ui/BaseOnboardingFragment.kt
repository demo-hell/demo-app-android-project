package br.com.mobicare.cielo.component.onboarding.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout.Configurator
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout.LayoutMode
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.component.onboarding.adapter.BaseOnboardingAdapter
import br.com.mobicare.cielo.component.onboarding.model.BaseOnboardingPage
import br.com.mobicare.cielo.component.onboarding.viewModel.BaseOnboardingViewModel
import br.com.mobicare.cielo.databinding.FragmentBaseOnboardingBinding
import br.com.mobicare.cielo.extensions.visible
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseOnboardingFragment : BaseFragment(), CieloNavigationListener {
    private val viewModel: BaseOnboardingViewModel by viewModel()

    private var binding: FragmentBaseOnboardingBinding? = null
    private var navigation: CieloNavigation? = null

    protected val currentPage get() = binding?.vpOnboarding?.currentItem ?: ZERO

    abstract fun getPages(): List<BaseOnboardingPage>

    abstract fun getTextFinishButton(): String

    abstract fun getUserPreferencesViewOnboardingKey(): String

    abstract fun navigateTo(): () -> Unit

    abstract fun onClickBackButton(): () -> Unit

    protected open fun logScreenView(): () -> Unit = {}

    protected open fun logClickFinishOnboardingButton(): () -> Unit = {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentBaseOnboardingBinding.inflate(
        inflater,
        container,
        false,
    ).also { binding = it }.root

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onResume() {
        super.onResume()

        setupNavigation()
        setupView()
        setupListener()
        setupObserver()
        logScreenView().invoke()
    }

    override fun onBackButtonClicked(): Boolean {
        return if (currentPage == ZERO) {
            false
        } else {
            binding?.vpOnboarding?.currentItem = currentPage - ONE
            logScreenView().invoke()
            true
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                setNavigationListener(this@BaseOnboardingFragment)
                configureCollapsingToolbar(
                    Configurator(
                        layoutMode = LayoutMode.BLANK,
                    ),
                )
            }
        }
    }

    private fun setupView() {
        binding?.apply {
            btnFinish.text = getTextFinishButton()
            vpOnboarding.adapter = BaseOnboardingAdapter(getPages())
            ciOnboarding.setViewPager(vpOnboarding)
        }
        buttonsController(currentPage)
    }

    private fun setupListener() {
        binding?.apply {
            btnJump.setOnClickListener(::onClickJumpPageButton)
            btnNext.setOnClickListener(::onClickNextPageButton)
            btnFinish.setOnClickListener(::onClickFinishButton)
            ivBackButton.setOnClickListener(::onClickBackButton)
            vpOnboarding.addOnPageChangeListener(onPageListener())
        }
    }

    private fun setupObserver() {
        viewModel.uiOnboardingState.observe(viewLifecycleOwner) {
            navigateTo().invoke()
        }
    }

    private fun onClickJumpPageButton(view: View) {
        binding?.vpOnboarding?.currentItem = getPages().size - ONE
    }

    private fun onClickNextPageButton(view: View) {
        binding?.vpOnboarding?.apply { currentItem++ }
        logScreenView().invoke()
    }

    private fun onClickFinishButton(view: View) {
        viewModel.saveViewOnboarding(getUserPreferencesViewOnboardingKey())
        logClickFinishOnboardingButton().invoke()
    }

    private fun onClickBackButton(view: View) = onClickBackButton().invoke()

    private fun onPageListener(): ViewPager.OnPageChangeListener {
        return object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                buttonsController(position)
            }
        }
    }

    private fun buttonsController(position: Int) {
        val isLastPage = position == getPages().size - ONE
        binding?.apply {
            btnFinish.visible(isLastPage)
            btnJump.visible(isLastPage.not())
            btnNext.visible(isLastPage.not())
            dividerBtn.visible(isLastPage.not())
        }
    }
}
