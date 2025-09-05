package br.com.mobicare.cielo.pix.ui.home.onboarding

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
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidResponse
import br.com.mobicare.cielo.pix.constants.PREPAID_RESPONSE_ARGS
import br.com.mobicare.cielo.pixMVVM.presentation.home.ui.PixOnboardingHomeFragmentDirections
import kotlinx.android.synthetic.main.fragment_pix_onboarding_home.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PixOnboardingHomeFragment : BaseFragment(), CieloNavigationListener,
    PixOnboardingHomeContract.View {

    private val prepaid: PrepaidResponse? by lazy {
        arguments?.getParcelable(PREPAID_RESPONSE_ARGS)
    }

    private val presenter: PixOnboardingHomePresenter by inject {
        parametersOf(this)
    }

    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_pix_onboarding_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupPagerView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(R.string.text_toolbar_home_pix))
            navigation?.showContainerButton()
            navigation?.showHelpButton()
            navigation?.showContent(isShow = true)
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupPagerView() {
        val items = listOf(
            OnboardingItem(
                title = getString(R.string.text_onboarding_home_pix_title_page_one),
                subtitle = getString(R.string.text_onboarding_home_pix_subtitle_page_one),
                image = R.drawable.ic_pix
            ),
            OnboardingItem(
                title = getString(R.string.text_onboarding_home_pix_title_page_two),
                subtitle = getString(R.string.text_onboarding_home_pix_subtitle_page_two),
                image = R.drawable.ic_transferencia_ok
            ),
            OnboardingItem(
                title = getString(R.string.text_onboarding_home_pix_title_page_three),
                subtitle = getString(R.string.text_onboarding_home_pix_subtitle_page_three),
                image = R.drawable.ic_pix_01
            ),
            OnboardingItem(
                title = getString(R.string.text_onboarding_home_pix_title_page_four),
                subtitle = getString(R.string.text_onboarding_home_pix_subtitle_page_four),
                image = R.drawable.ic_pix_saque_e_troco_01
            ),
            OnboardingItem(
                title = getString(R.string.text_onboarding_home_pix_title_page_five),
                subtitle = getString(R.string.text_onboarding_home_pix_subtitle_page_five),
                image = R.drawable.ic_pix_saque_e_troco_02
            )
        )

        view_pager_onboarding_home_pix?.adapter = OnboardingAdapter(
            items = items,
            layout = R.layout.layout_pix_keys_onboarding_item,
            id = Onboarding.DEFAULT.id
        )
        indicator_onboarding_home_pix?.setViewPager(view_pager_onboarding_home_pix)

        setupListener(items)
        listenerCallAction(items)
    }

    private fun setupListener(listItems: List<OnboardingItem>) {
        view_pager_onboarding_home_pix?.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                controllerButton(listItems.size - ONE == position)
            }
        })
    }

    private fun controllerButton(visible: Boolean) {
        if (visible) {
            container_action_next_and_jump_pix?.gone()
            btn_show_home_pix?.visible()
        } else {
            container_action_next_and_jump_pix?.visible()
            btn_show_home_pix?.gone()
        }
    }

    private fun listenerCallAction(items: List<OnboardingItem>) {
        callNextPage()
        skipOnboarding(items)
        saveShowOnboarding()
    }

    private fun callNextPage() {
        btn_next_onboarding_home_pix?.setOnClickListener {
            view_pager_onboarding_home_pix?.currentItem =
                view_pager_onboarding_home_pix.currentItem + ONE
        }
    }

    private fun skipOnboarding(listItems: List<OnboardingItem>) {
        btn_skip_onboarding_home_pix?.setOnClickListener {
            view_pager_onboarding_home_pix?.currentItem = listItems.size - ONE
        }
    }

    private fun saveShowOnboarding() {
        btn_show_home_pix?.setOnClickListener {
            presenter.saveShowPixOnboardingHome()
        }
    }

    override fun onShowPixHome() {
        findNavController().navigate(
            PixOnboardingHomeFragmentDirections.actionPixOnboardingHomeFragmentToPixHomeFragment(
                prepaid ?: PrepaidResponse()
            )
        )
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().finish()
        return super.onBackButtonClicked()
    }
}