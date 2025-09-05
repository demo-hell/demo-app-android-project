package br.com.mobicare.cielo.pix.ui.extract.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.OnboardingItem
import br.com.mobicare.cielo.commons.enums.Onboarding
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.OnboardingAdapter
import br.com.mobicare.cielo.databinding.FragmentPixOnboardingExtractBinding
import kotlinx.android.synthetic.main.fragment_pix_onboarding_extract.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PixOnboardingExtractFragment : BaseFragment(), CieloNavigationListener,
    PixOnboardingExtractContract.View {

    private val presenter: PixOnboardingExtractPresenter by inject {
        parametersOf(this)
    }

    private var navigation: CieloNavigation? = null

    private var _binding: FragmentPixOnboardingExtractBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPixOnboardingExtractBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupPagerView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_toolbar_extract_pix))
            navigation?.showContainerButton()
            navigation?.showHelpButton()
            navigation?.showContent(isShow = true)
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupPagerView() {
        val items = listOf(
            OnboardingItem(
                title = getString(R.string.text_onboarding_extract_pix_title_page_one),
                subtitle = getString(R.string.text_onboarding_extract_pix_subtitle_page_one),
                image = R.drawable.ic_pix_extract_1
            ),
            OnboardingItem(
                title = getString(R.string.text_onboarding_extract_pix_title_page_two),
                subtitle = getString(R.string.text_onboarding_extract_pix_subtitle_page_two),
                image = R.drawable.ic_pix_extract_2
            ),
            OnboardingItem(
                title = getString(R.string.text_onboarding_extract_pix_title_page_three),
                subtitle = getString(R.string.text_onboarding_extract_pix_subtitle_page_three),
                image = R.drawable.ic_pix_extract_3
            ),
        )

        binding?.viewPagerOnboardingExtractPix?.adapter = OnboardingAdapter(
            items = items,
            layout = R.layout.layout_pix_keys_onboarding_item,
            id = Onboarding.DEFAULT.id
        )
        binding?.indicatorOnboardingExtractPix?.setViewPager(view_pager_onboarding_extract_pix)

        setupListener(items)
        listenerCallAction(items)
    }

    private fun setupListener(listItems: List<OnboardingItem>) {
        binding?.viewPagerOnboardingExtractPix?.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                controllerButton(listItems.size - ONE == position)
            }
        })
    }

    private fun controllerButton(visible: Boolean) {
        if (visible) {
            binding?.containerActionNextAndJumpPix?.gone()
            binding?.btnShowExtractPix?.visible()
        } else {
            binding?.containerActionNextAndJumpPix?.visible()
            binding?.btnShowExtractPix?.gone()
        }
    }

    private fun listenerCallAction(items: List<OnboardingItem>) {
        callNextPage()
        skipOnboarding(items)
        saveShowOnboarding()
    }

    private fun callNextPage() {
        binding?.btnNextOnboardingExtractPix?.setOnClickListener {
            val newItem = (binding?.viewPagerOnboardingExtractPix?.currentItem ?: ZERO) + ONE
            binding?.viewPagerOnboardingExtractPix?.currentItem = newItem

        }
    }

    private fun skipOnboarding(listItems: List<OnboardingItem>) {
        binding?.btnSkipOnboardingExtractPix?.setOnClickListener {
            binding?.viewPagerOnboardingExtractPix?.currentItem = listItems.size - ONE
        }
    }

    private fun saveShowOnboarding() {
        binding?.btnShowExtractPix?.setOnClickListener {
            presenter.saveShowPixOnboardingExtract()
        }
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().finish()
        return super.onBackButtonClicked()
    }

    override fun onShowPixExtract() {
        findNavController().navigate(
            PixOnboardingExtractFragmentDirections.actionPixOnboardingExtractFragmentToPixExtractFragment()
        )
    }
}