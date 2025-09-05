package br.com.mobicare.cielo.openFinance.presentation.manager.newShare.redirect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils.openBrowser
import br.com.mobicare.cielo.databinding.OpenFinanceRedirectFragmentBinding
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateAutomaticRedirect
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateRedirect
import kotlinx.android.synthetic.main.open_finance_flow_new_share_activity.compSteps
import org.koin.androidx.viewmodel.ext.android.viewModel

class OpenFinanceRedirectFragment : BaseFragment(), CieloNavigationListener {
    private var binding: OpenFinanceRedirectFragmentBinding? = null
    private var navigation: CieloNavigation? = null
    private val args: OpenFinanceRedirectFragmentArgs by navArgs()
    private val redirectViewModel: OpenFinanceRedirectViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = OpenFinanceRedirectFragmentBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    private val toolbarDefault
        get() = CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.NOT_SCROLLABLE,
            toolbar = CieloCollapsingToolbarLayout.Toolbar(
                title = getString(R.string.new_share_opf),
                showBackButton = false,
                menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                    menuRes = R.menu.menu_common_close_blue,
                    onOptionsItemSelected = {
                        requireActivity().finish()
                    }
                )
            )
        )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        configureToolbar()
        actualStep()
        setListeners()
        redirectViewModel.defineExpiredTimeConsent()
        redirectViewModel.automaticRedirect()
        observeTimeRedirect()
        observeAutomaticRedirect()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
        }
    }

    private fun configureToolbar() {
        navigation?.configureCollapsingToolbar(toolbarDefault)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun actualStep() {
        activity?.compSteps?.stepActive(R.layout.open_finance_redirect_fragment, null)
    }

    private fun setListeners() {
        binding?.apply {
            btnRedirect.setOnClickListener {
                openBrowser(requireActivity(), args.stringRedirectUrl)
                requireActivity().finish()
            }
            btnCloseConsent.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun observeTimeRedirect() {
        redirectViewModel.redirectLiveData.observe(viewLifecycleOwner) { state ->
            binding?.apply {
                when (state) {
                    is UIStateRedirect.ConsentActive -> {
                        btnRedirect.visible()
                        infoExpiredTime.gone()
                    }
                    is UIStateRedirect.ExpiredTimeConsent -> {
                        btnRedirect.gone()
                        btnCloseConsent.visible()
                        infoExpiredTime.visible()
                    }
                }
            }
        }
    }

    private fun observeAutomaticRedirect() {
        redirectViewModel.automaticRedirectLiveData.observe(viewLifecycleOwner) { state ->
            if (state is UIStateAutomaticRedirect.AutomaticRedirect) {
                openBrowser(requireActivity(), args.stringRedirectUrl)
                requireActivity().finish()
            }
        }
    }
}