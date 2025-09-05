package br.com.mobicare.cielo.pix.ui.terms.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.domains.entities.OnboardingItem
import br.com.mobicare.cielo.commons.enums.Onboarding
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.OnboardingAdapter
import br.com.mobicare.cielo.databinding.FragmentPixTermOnboardingBinding
import br.com.mobicare.cielo.pix.constants.IS_PARTNER_ARGS

class PixTermOnboardingFragment : BaseFragment(), CieloNavigationListener {

    private val isPartner by lazy {
        arguments?.getBoolean(IS_PARTNER_ARGS, false) ?: false
    }

    private var navigation: CieloNavigation? = null

    private var binding: FragmentPixTermOnboardingBinding? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View =
            FragmentPixTermOnboardingBinding.inflate(
                    inflater, container, false
            ).also {
                binding = it
            }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupPagerView()
        listenerCallAction(isShowTerms = false)
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().finish()
        return super.onBackButtonClicked()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_toolbar_home_pix))
            navigation?.showContainerButton()
            navigation?.showHelpButton()
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupPagerView() {
        val items = listOf(
            OnboardingItem(
                title = getString(R.string.text_pix_term_onboarding_sales_title),
                subtitle = getString(R.string.text_pix_term_onboarding_sales_subtitle),
                image = R.drawable.ic_pix_adquirencia
            ),
            OnboardingItem(
                title = getString(R.string.text_pix_term_onboarding_receive_title),
                subtitle = getString(R.string.text_pix_term_onboarding_receive_subtitle),
                image = R.drawable.ic_pix_conta_transacional
            )
        )

        binding?.viewPagerPixTermOnboarding?.adapter = OnboardingAdapter(
            items = items,
            layout = R.layout.layout_pix_keys_onboarding_item,
            id = Onboarding.DEFAULT.id
        )
        binding?.indicatorPixTermOnboarding?.setViewPager(binding?.viewPagerPixTermOnboarding)

        setupListener(items)
    }

    private fun setupListener(listItems: List<OnboardingItem>) {
        binding?.viewPagerPixTermOnboarding?.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                listenerCallAction(listItems.size - ONE == position)
            }
        })
    }

    private fun listenerCallAction(isShowTerms: Boolean) {
        binding?.btnNextPixTermOnboarding?.setOnClickListener {
            if (isShowTerms)
                findNavController().navigate(
                    PixTermOnboardingFragmentDirections.actionPixTermOnboardingFragmentToPixTermFragment(
                        isPartner
                    )
                )
            else
                binding?.viewPagerPixTermOnboarding?.currentItem = ONE
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}