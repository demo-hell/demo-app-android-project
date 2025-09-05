package br.com.mobicare.cielo.suporteTecnico.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.SERVICES_TECHNICAL_SUPPORT
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.TECHNICAL_SUPPORT_HELP_CENTER
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.Values.GO_INITIAL_SCREEN
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.Values.OPEN_REQUEST
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.Values.REQUEST_SUPPORT_ONLINE
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.Values.UPDATE_ADDRESS
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments.NewTechnicalSupportFragment
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.router.FRAGMENT_TO_ROUTER
import br.com.mobicare.cielo.commons.router.RouterFragmentInActivity
import br.com.mobicare.cielo.commons.router.TITLE_ROUTER_FRAGMENT
import br.com.mobicare.cielo.commons.router.deeplink.MEU_CADASTRO_TITLE
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentRequestTicketSupportBinding
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.MeuCadastroFragmentAtualNovo
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.RequestTicketSupportViewModel
import br.com.mobicare.cielo.suporteTecnico.utils.UIStateRequestTicketSupport
import org.koin.androidx.viewmodel.ext.android.viewModel
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class RequestTicketSupportFragment : BaseFragment(), CieloNavigationListener {
    private var _binding: FragmentRequestTicketSupportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RequestTicketSupportViewModel by viewModel()

    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRequestTicketSupportBinding.inflate(
        inflater, container, false
    ).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupObservers()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        onReload()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.setupToolbar(isCollapsed = false)
            navigation?.showToolbar(isShow = true)
            navigation?.showBackButton(isShow = true)
            navigation?.showCloseButton(isShow = false)
            navigation?.showHelpButton(isShow = false)
        }
    }

    private fun setupObservers() {
        viewModel.merchantLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIStateRequestTicketSupport.Loading -> showLoading()
                is UIStateRequestTicketSupport.AuthorizationSuccess -> {
                    setupViewAuthorization()
                }

                is UIStateRequestTicketSupport.AuthorizationError -> {
                    setupViewNotAuthorization()
                }

                else -> onLoadingError()
            }
        }
    }

    private fun onReload() {
        viewModel.getMerchant()
    }

    private fun setupViewAuthorization() {
        GA4.logScreenView(GA4.ScreenView.CALL_OPENING)
        GA4.logScreenView(SERVICES_TECHNICAL_SUPPORT)
        binding.apply {
            containerOpenTickets.visible()
            containerUpdateAddress.gone()
            errorInclude.root.gone()
            progress.root.gone()
        }
    }

    private fun setupViewNotAuthorization() {
        GA4.logWarningDisplayContent(SERVICES_TECHNICAL_SUPPORT, UPDATE_ADDRESS)
        GA4.logWarningDisplayContent(TECHNICAL_SUPPORT_HELP_CENTER, UPDATE_ADDRESS)
        binding.apply {
            containerOpenTickets.gone()
            errorInclude.root.gone()
            progress.root.gone()
            containerUpdateAddress.visible()
        }
    }

    private fun showLoading() {
        binding.apply {
            progress.root.visible()
            errorInclude.root.gone()
            containerOpenTickets.gone()
            containerUpdateAddress.gone()
        }
    }

    private fun onLoadingError() {
        binding.apply {
            progress.root.gone()
            containerOpenTickets.gone()
            containerUpdateAddress.gone()
            errorInclude.root.visible()

            errorInclude.btnReload.setOnClickListener {
                onReload()
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnTestManually.setOnClickListener {
                GA4.logClick(SERVICES_TECHNICAL_SUPPORT, REQUEST_SUPPORT_ONLINE)
                openRouterFragment(true)
            }
            btnOpenTicket.setOnClickListener {
                GA4.logClick(SERVICES_TECHNICAL_SUPPORT, OPEN_REQUEST)
                navigateToTicket()
            }
            btnScreenHome.setOnClickListener {
                GA4.logClick(TECHNICAL_SUPPORT_HELP_CENTER, GO_INITIAL_SCREEN)
                val intent = Intent(requireActivity(), MainBottomNavigationActivity::class.java)
                startActivity(intent)
                requireActivity().supportFragmentManager.popBackStack()
            }
            btnUpdateAddress.setOnClickListener {
                GA4.logClick(TECHNICAL_SUPPORT_HELP_CENTER, UPDATE_ADDRESS)
                openRouterFragment(false)
            }
        }
    }

    private fun openRouterFragment(isTechnicalSupport: Boolean) {

        Intent(context, RouterFragmentInActivity::class.java).apply {
            putExtra(
                FRAGMENT_TO_ROUTER,
                getFragmentName(isTechnicalSupport)
            )
            putExtra(TITLE_ROUTER_FRAGMENT, getFragmentTitle(isTechnicalSupport))
            startActivity(this)
        }
    }

    private fun getFragmentName(isTechnicalSupport: Boolean): String? {
        return if (isTechnicalSupport) {
            NewTechnicalSupportFragment::class.java.canonicalName
        } else {
            MeuCadastroFragmentAtualNovo::class.java.canonicalName
        }
    }

    private fun getFragmentTitle(isTechnicalSupport: Boolean): String? {
        return if (isTechnicalSupport) {
            getString(R.string.text_technical_suppport_title)
        } else {
            MEU_CADASTRO_TITLE
        }
    }

    private fun navigateToTicket() {
        findNavController().navigate(
            RequestTicketSupportFragmentDirections.actionRequestTicketSupportFragmentToMerchantEquipamentsFragment()
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigation?.goToHome()
    }
}