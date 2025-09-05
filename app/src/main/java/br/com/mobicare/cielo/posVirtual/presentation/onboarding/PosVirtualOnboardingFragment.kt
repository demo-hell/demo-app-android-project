package br.com.mobicare.cielo.posVirtual.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.databinding.FragmentPosVirtualOnboardingBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference.Companion.POS_ACCREDITATION_ENABLE
import br.com.mobicare.cielo.posVirtual.analytics.PosVirtualAnalytics
import br.com.mobicare.cielo.posVirtual.presentation.onboarding.adapter.PosVirtualOnboardingAdapter
import br.com.mobicare.cielo.posVirtual.presentation.onboarding.adapter.PosVirtualOnboardingItem
import org.jetbrains.anko.browse
import org.koin.android.ext.android.inject


private const val URL_CRED_TAP_ON_PHONE = "https://onboarding.cielo.com.br/site-pos-virtual"

class PosVirtualOnboardingFragment :
    BaseFragment(),
    CieloNavigationListener {

    private var binding: FragmentPosVirtualOnboardingBinding? = null
    private var navigation: CieloNavigation? = null

    private val ga4: PosVirtualAnalytics by inject()

    private lateinit var items: List<PosVirtualOnboardingItem>

    private val currentPage get() = binding?.vpOnboarding?.currentItem ?: ZERO
    private val screenPath
        get() =
            PosVirtualAnalytics.SCREEN_VIEW_ONBOARDING_STEPS.format(
                currentPage + ONE,
            )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentPosVirtualOnboardingBinding
        .inflate(
            inflater,
            container,
            false,
        ).also { binding = it }
        .root

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()

        setupNavigation()
        setupPageView()
        setupListener()
        logScreenView()
    }

    override fun onBackButtonClicked(): Boolean =
        if (currentPage == ZERO) {
            false
        } else {
            binding?.vpOnboarding?.currentItem = currentPage - ONE
            logScreenView()
            true
        }

    override fun onButtonClicked(labelButton: String) {
        navigateToAccreditation()
        logButtonStart()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                setNavigationListener(this@PosVirtualOnboardingFragment)
                configureCollapsingToolbar(
                    CollapsingToolbarBaseActivity.Configurator(
                        show = false,
                    ),
                )
                showButton(false)
                setTextButton(getString(R.string.label_button_start_pos_virtual_onboarding))
            }
        }
    }

    private fun setupPageView() {
        items =
            listOf(
                PosVirtualOnboardingItem(
                    title = getString(R.string.txt_title_pos_virtual_onboarding_page_one),
                    subtitle = getString(R.string.txt_description_pos_virtual_onboarding_page_one),
                    image = R.drawable.img_152_vendas,
                ),
                PosVirtualOnboardingItem(
                    title = getString(R.string.txt_title_pos_virtual_onboarding_page_two),
                    subtitle = getString(R.string.txt_description_pos_virtual_onboarding_page_two),
                    image = R.drawable.img_139_venda,
                ),
                PosVirtualOnboardingItem(
                    title = getString(R.string.txt_title_pos_virtual_onboarding_page_three),
                    subtitle = getString(R.string.txt_description_pos_virtual_onboarding_page_three),
                    image = R.drawable.img_77_link_pagamento_servicos,
                ),
                PosVirtualOnboardingItem(
                    title = getString(R.string.txt_title_pos_virtual_onboarding_page_four),
                    subtitle = getString(R.string.txt_description_pos_virtual_onboarding_page_four),
                    image = R.drawable.img_110_pix_01,
                ),
            )

        binding?.apply {
            vpOnboarding.adapter = PosVirtualOnboardingAdapter(items)
            ciOnboarding.setViewPager(vpOnboarding)
        }
    }

    private fun setupListener() {
        binding?.apply {
            btnJump.setOnClickListener {
                vpOnboarding.currentItem = items.size - ONE
            }

            btnNext.setOnClickListener {
                vpOnboarding.currentItem++
                logScreenView()
            }

            ivBackButton.setOnClickListener {
                requireActivity().finish()
            }

            vpOnboarding.addOnPageChangeListener(
                object :
                    ViewPager.SimpleOnPageChangeListener() {
                    override fun onPageSelected(position: Int) {
                        buttonsController(position)
                    }
                },
            )
        }
    }

    private fun navigateToAccreditation() {
        doWhenResumed {
            val accreditationEnable =
                FeatureTogglePreference.instance.getFeatureTogle(POS_ACCREDITATION_ENABLE)
            if (accreditationEnable) {
                findNavController().navigate(
                    PosVirtualOnboardingFragmentDirections.actionPosVirtualOnboardingToPosVirtualAccreditation()
                )
            } else {
                requireActivity().also {
                    it.browse(URL_CRED_TAP_ON_PHONE)
                    it.finish()
                }
            }
        }
    }

    private fun buttonsController(position: Int) {
        val showRowButton = position < items.size - ONE

        navigation?.showButton(showRowButton.not())
        binding?.llButtons?.visible(showRowButton)
    }

    private fun logScreenView() = ga4.logScreenView(screenPath)

    private fun logButtonStart() = ga4.logBeginCheckoutOnboarding(screenPath)
}
