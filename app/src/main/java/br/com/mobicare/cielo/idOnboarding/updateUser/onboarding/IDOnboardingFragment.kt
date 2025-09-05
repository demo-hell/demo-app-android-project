package br.com.mobicare.cielo.idOnboarding.updateUser.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingBinding
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_INTRODUCTION
import br.com.mobicare.cielo.idOnboarding.updateUser.onboarding.adapter.IDOnboardingAdapter
import br.com.mobicare.cielo.idOnboarding.updateUser.onboarding.adapter.IDOnboardingItem
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class IDOnboardingFragment : BaseFragment(), CieloNavigationListener, IDOnboardingContract.View {

    private val presenter: IDOnboardingPresenter by inject { parametersOf(this) }
    private val analyticsGA: IDOnboardingP1AnalyticsGA by inject()
    private var navigation: CieloNavigation? = null

    private var _binding: FragmentIdOnboardingBinding? = null
    private val binding get() = _binding
    private var sizeItems: Int = 4

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIdOnboardingBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupPagerView()
        listenerCallAction()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupPagerView() {
        val items = listOf(
            IDOnboardingItem(
                title = getString(R.string.id_onboarding_experience_title),
                subtitle = getString(R.string.id_onboarding_experience_message),
                image = R.drawable.ic_10
            ),
            IDOnboardingItem(
                title = getString(R.string.id_onboarding_control_title),
                subtitle = getString(R.string.id_onboarding_control_message),
                image = R.drawable.ic_10_different_profiles
            ),
            IDOnboardingItem(
                title = getString(R.string.id_onboarding_different_profiles_title),
                subtitle = getString(R.string.id_onboarding_different_profiles_message),
                image = R.drawable.ic_10_control,
                buttonText = getString(R.string.id_onboarding_different_profiles_btn),
                isShowButton = true
            ),
            IDOnboardingItem(
                title = getString(R.string.id_onboarding_validate_data_title),
                subtitle = getString(R.string.id_onboarding_validate_data_message),
                image = R.drawable.ic_gestao_de_acesso_seguro,
                buttonText = getString(R.string.id_onboarding_experience_btn),
                isShowButton = true
            )
        )

        binding?.apply {
            viewPagerOnboarding.adapter = IDOnboardingAdapter(
                items, this@IDOnboardingFragment
            )
            indicatorOnboarding.setViewPager(viewPagerOnboarding)
        }
        setupListener(items)
    }

    private fun setupListener(listItems: List<IDOnboardingItem>) {
        sizeItems = listItems.size
        analyticsGA.logIDScreenViewStep(ANALYTICS_ID_SCREEN_VIEW_INTRODUCTION, ONE)
        binding?.viewPagerOnboarding?.addOnPageChangeListener(object :
            ViewPager.SimpleOnPageChangeListener() {

            override fun onPageSelected(position: Int) {
                controllerVisibleBottom(listItems.size - ONE == position)
                analyticsGA.logIDScreenViewStep(ANALYTICS_ID_SCREEN_VIEW_INTRODUCTION, position + ONE)
            }
        })
    }

    private fun controllerVisibleBottom(visible: Boolean) {
        binding?.apply {
            btnNextOnboarding.visible(visible.not())
            btnStartOnboarding.visible(visible)
        }
    }

    private fun listenerCallAction() {
        binding?.apply {
            btnNextOnboarding.setOnClickListener {
                viewPagerOnboarding.currentItem =
                    viewPagerOnboarding.currentItem + ONE
            }
            btnStartOnboarding.setOnClickListener {
                analyticsGA.logIDStartValidationSignUp(ANALYTICS_ID_SCREEN_VIEW_INTRODUCTION, sizeItems)
                presenter.saveUserViewedIDOnboarding()
            }
            btnHelp.setOnClickListener {
                onShowHelpCenter()
            }
        }
    }

    override fun onShowHelpCenter() {
        requireActivity().openFaq(
            tag = ConfigurationDef.TAG_HELP_CENTER_IDENTIDADE_DIGITAL,
            subCategoryName = getString(R.string.id_onboarding_name)
        )
    }

    override fun onStartID() {
        findNavController().safeNavigate(
                IDOnboardingFragmentDirections
                    .actionIdOnboardingFragmentToIdOnboardingP1CompletionStatusFragment()
        )
    }
}