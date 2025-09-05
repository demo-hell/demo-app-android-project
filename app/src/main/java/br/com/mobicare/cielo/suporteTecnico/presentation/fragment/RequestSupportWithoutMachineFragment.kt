package br.com.mobicare.cielo.suporteTecnico.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.SERVICES_TECHNICAL_SUPPORT
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentRequestSupportWithoutMachineBinding
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.MerchantEquipmentsViewModel
import br.com.mobicare.cielo.suporteTecnico.utils.UiStateEquipments
import br.com.mobicare.cielo.taxaPlanos.domain.TerminalsResponse
import org.koin.androidx.viewmodel.ext.android.viewModel
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class RequestSupportWithoutMachineFragment :
    BaseFragment(),
    CieloNavigationListener {
    private var _binding: FragmentRequestSupportWithoutMachineBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MerchantEquipmentsViewModel by viewModel()
    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentRequestSupportWithoutMachineBinding
        .inflate(inflater, container, false)
        .also {
            _binding = it
        }.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        onReload()
        setupNavigation()
        setupListeners()
        setupObservers()
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

    private fun setupListeners() {
        binding.apply {
            btnAccessHelpCenter.setOnClickListener {
                navigation?.startHelpCenter(ConfigurationDef.TAG_HELP_CENTER_SUPPORT_TECHNICAL)
            }
        }
    }

    private fun setupObservers() {
        viewModel.merchantEquipments.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateEquipments.ShowLoading -> onShowLoading()
                is UiStateEquipments.HideLoading -> onHideLoading()
                is UiStateEquipments.Success -> onEquipmentSuccess(state.data)
                is UiStateEquipments.ErrorWithoutMachine ->
                    state.error?.let {
                        onEquipmentErrorWithoutMachine(it)
                    }

                else -> onEquipmentError()
            }
        }
    }

    fun onReload() {
        viewModel.getMerchantEquipment()
    }

    private fun onShowLoading() {
        binding.apply {
            containerWithoutMachine.gone()
            progress.root.visible()
            errorInclude.root.gone()
        }
    }

    private fun onHideLoading() {
        binding.apply {
            containerWithoutMachine.gone()
            progress.root.gone()
            errorInclude.root.gone()
        }
    }

    private fun onEquipmentError() {
        binding.apply {
            containerWithoutMachine.gone()
            progress.root.gone()
            errorInclude.root.visible()
            errorInclude.btnReload.setOnClickListener {
                onReload()
            }
        }
    }

    private fun onEquipmentErrorWithoutMachine(error: NewErrorMessage) {
        GA4.logException(
            screenName = SERVICES_TECHNICAL_SUPPORT,
            errorCode = error.httpCode.toString(),
            errorMessage = error.message,
        )
        binding.apply {
            containerWithoutMachine.visible()
            progress.root.gone()
            errorInclude.root.gone()
        }
    }

    private fun onEquipmentSuccess(data: TerminalsResponse?) {
        binding.apply {
            containerWithoutMachine.gone()
            progress.root.gone()
            errorInclude.root.gone()
        }
        navigateToEquipments()
    }

    private fun navigateToEquipments() {
        findNavController().navigate(
            RequestSupportWithoutMachineFragmentDirections
                .actionRequestSupportWithoutMachineFragmentToRequestTicketSupportFragment(),
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigation?.goToHome()
    }
}
