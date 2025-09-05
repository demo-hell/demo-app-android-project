package br.com.mobicare.cielo.newRecebaRapido.presentation.router

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.CollapsingToolbarBaseActivity
import br.com.mobicare.cielo.databinding.FragmentReceiveAutomaticRouterBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.main.domain.Menu
import br.com.mobicare.cielo.main.domain.MenuTarget
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4.Companion.SCREEN_VIEW_GENERIC_ERROR
import br.com.mobicare.cielo.newRecebaRapido.domain.entity.FastRepayRule
import br.com.mobicare.cielo.newRecebaRapido.util.UiReceiveAutomaticRouterState
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReceiveAutomaticRouterFragment : BaseFragment(), CieloNavigationListener {
    private val viewModel: ReceiveAutomaticRouterViewModel by viewModel()
    private var binding: FragmentReceiveAutomaticRouterBinding? = null
    private val ga4: RAGA4 by inject()

    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentReceiveAutomaticRouterBinding.inflate(inflater, container, false)
        .also {
            binding = it
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupObserver()
        initiateReceiveAutomaticProcess()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showHelpButton(isShow = false)
            navigation?.showContainerButton(isShow = false)
            navigation?.configureCollapsingToolbar(
                CollapsingToolbarBaseActivity.Configurator(
                    show = false,
                    isExpanded = false,
                    disableExpandableMode = false,
                    toolbarMenu = CollapsingToolbarBaseActivity.ToolbarMenu(
                        menuRes = R.menu.menu_common_only_faq_blue,
                        onOptionsItemSelected = {}
                    ),
                    showBackButton = true
                )
            )
        }
    }

    private fun setupObserver() {
        viewModel.receiveAutomaticRouterMutableLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiReceiveAutomaticRouterState.ShowOnBoarding -> onShowOnBoarding()
                is UiReceiveAutomaticRouterState.ShowHome -> onShowHome()
                is UiReceiveAutomaticRouterState.ShowLoading -> showLoading()
                is UiReceiveAutomaticRouterState.HideLoading -> hideLoading()
                is UiReceiveAutomaticRouterState.ShowGenericError -> showGenericError(uiState.error)
                is UiReceiveAutomaticRouterState.ShowIneligibleError -> showIneligibleError(uiState.fastRepayRule)
                is UiReceiveAutomaticRouterState.ShowContractedServiceError -> showContractedServiceError(uiState.fastRepayRule)
            }
        }
    }

    private fun initiateReceiveAutomaticProcess() {
        viewModel.initiateReceiveAutomaticVerificationFlow()
    }

    private fun onShowOnBoarding() {
        findNavController().safeNavigate(
            ReceiveAutomaticRouterFragmentDirections
                .actionReceiveAutomaticRouterFragmentToReceiveAutomaticOnBoardingFragment()
        )
    }

    private fun onShowHome() {
        findNavController().safeNavigate(
            ReceiveAutomaticRouterFragmentDirections
                .actionReceiveAutomaticRouterFragmentToReceiveAutomaticHomeFragment()
        )
    }

    private fun showLoading() {
        navigation?.showLoading(
            message = R.string.p2m_load_wait,
        )
    }

    private fun hideLoading() {
        navigation?.hideLoading()
    }

    private fun showGenericError(error: NewErrorMessage?) {
        doWhenResumed {
            trackGenericError(error)
            navigation?.showCustomHandlerView(
                title = getString(R.string.commons_generic_error_title),
                message = error?.message ?: getString(R.string.commons_generic_error_message),
                labelSecondButton = getString(R.string.text_try_again_label),
                isShowFirstButton = false,
                isShowButtonClose = true,
                isShowSecondButton = true,
                callbackSecondButton = {
                    trackClick(
                        RAGA4.SCREEN_VIEW_GENERIC_ERROR,
                        getString(R.string.text_try_again_label),
                    )
                    initiateReceiveAutomaticProcess()
                },
                callbackClose = ::goToHome,
                callbackBack = ::goToHome,
            )
        }
    }

    private fun showIneligibleError(fastRepayRule: FastRepayRule?) {
        doWhenResumed {
            trackException(
                RAGA4.SCREEN_VIEW_INELIGIBLE_ERROR,
                fastRepayRule,
            )
            navigation?.showCustomHandlerView(
                title = getString(R.string.receive_auto_ineligible_error_title),
                message = getString(R.string.receive_auto_ineligible_error_message),
                labelSecondButton = getString(R.string.receive_auto_bt_tax_plans),
                isShowFirstButton = false,
                isShowButtonClose = true,
                isShowSecondButton = true,
                callbackSecondButton = {
                    trackClick(
                        RAGA4.SCREEN_VIEW_INELIGIBLE_ERROR,
                        getString(R.string.receive_auto_bt_tax_plans),
                    )
                    goToTaxPlans()
                },
                callbackClose = ::goToHome,
                callbackBack = ::goToHome,
            )
        }
    }

    private fun showContractedServiceError(fastRepayRule: FastRepayRule?) {
        doWhenResumed {
            trackException(
                RAGA4.SCREEN_VIEW_CONTRACTED_SERVICE_ERROR,
                fastRepayRule,
            )
            navigation?.showCustomHandlerView(
                title = getString(R.string.receive_auto_contracted_server_error_title),
                message = getString(R.string.receive_auto_contracted_server_error_message),
                labelSecondButton = getString(R.string.back),
                isShowFirstButton = false,
                isShowButtonClose = true,
                isShowSecondButton = true,
                callbackSecondButton = {
                    trackClick(
                        RAGA4.SCREEN_VIEW_CONTRACTED_SERVICE_ERROR,
                        getString(R.string.back),
                    )
                    goToHome()
                },
                callbackClose = ::goToHome,
                callbackBack = ::goToHome,
            )
        }
    }

    private fun goToTaxPlans() {
        Router.navigateTo(
            requireContext(),
            Menu(
                Router.APP_ANDROID_RATES, EMPTY,
                listOf(),
                getString(R.string.txp_header),
                false,
                EMPTY,
                listOf(),
                show = false,
                showItems = false,
                menuTarget = MenuTarget(),
            ),
        ).also {
            activity?.finishAndRemoveTask()
        }
    }

    private fun goToHome() {
        requireActivity().backToHome()
    }

    private fun trackException(
        screenName: String,
        fastRepayRule: FastRepayRule?,
    ) {
        ga4.logExceptionWithFastRepayRule(
            screenName = screenName,
            fastRepayRule = fastRepayRule,
        )
    }

    private fun trackGenericError(error: NewErrorMessage?) {
        ga4.logException(
            screenName = SCREEN_VIEW_GENERIC_ERROR,
            error = error,
        )
    }

    private fun trackClick(
        screenName: String,
        contentName: String,
    ) {
        ga4.logClick(
            screenName = screenName,
            contentName = contentName,
        )
    }
}
