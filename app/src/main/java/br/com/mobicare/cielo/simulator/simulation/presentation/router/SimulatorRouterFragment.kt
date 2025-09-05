package br.com.mobicare.cielo.simulator.simulation.presentation.router

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentRouterSimulatorBinding
import br.com.mobicare.cielo.simulator.analytics.SalesSimulatorGA4
import br.com.mobicare.cielo.simulator.simulation.presentation.state.UiSimulatorProductState
import br.com.mobicare.cielo.simulator.simulation.presentation.viewModel.SimulatorViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SimulatorRouterFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null
    private var binding: FragmentRouterSimulatorBinding? = null
    private val viewModel: SimulatorViewModel by sharedViewModel()
    private val ga4: SalesSimulatorGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentRouterSimulatorBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupObserver()
    }

    private fun setupNavigation() {
        navigation = (requireActivity() as? CieloNavigation)?.also {
            it.configureCollapsingToolbar(
                CieloCollapsingToolbarLayout.Configurator(
                    layoutMode = CieloCollapsingToolbarLayout.LayoutMode.BLANK
                ),
            )
        }
    }

    private fun setupObserver() {
        viewModel.simulatorProductState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UiSimulatorProductState.ShowLoading -> onShowLoading()
                is UiSimulatorProductState.Success -> onShowSuccess()
                is UiSimulatorProductState.Error -> onShowError(uiState.error)
                UiSimulatorProductState.HideLoading -> onHideLoading()
            }
        }
    }

    private fun onHideLoading() {
        navigation?.hideAnimatedLoading()
        navigation?.showContent(true)
    }

    private fun onShowError(error: NewErrorMessage?) {
        ga4.logException(SalesSimulatorGA4.SCREEN_VIEW_SIMULATOR, error)
        showErrorBs()
    }

    private fun onShowSuccess() {
        findNavController().navigate(SimulatorRouterFragmentDirections.actionSimulatorRouterFragmentToSimulationValueFragment())
    }

    private fun onShowLoading() {
        navigation?.showAnimatedLoading()
    }

    override fun onBackButtonClicked(): Boolean {
        navigation?.goToHome()
        return super.onBackButtonClicked()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun showBottomSheetGoToHome(
        @StringRes title: Int,
        @StringRes message: Int,
        @DrawableRes image: Int,
    ) {
        navigation?.showCustomHandlerView(
            title = getString(title),
            message = getString(message),
            contentImage = image,
            labelFirstButton = getString(R.string.text_try_again_label),
            callbackFirstButton = {
                viewModel.getSimulatorProducts()
            },
            labelSecondButton = getString(R.string.go_to_initial_screen),
            callbackSecondButton = {
                goToHome()
            },
            callbackBack = ::goToHome,
            isShowButtonClose = true,
            callbackClose = ::goToHome
        )
    }

    private fun goToHome() {
        requireActivity().finish()
    }

    private fun showErrorBs() {
        showBottomSheetGoToHome(
            title = R.string.commons_generic_error_title,
            message = R.string.pos_virtual_error_message_generic,
            image = R.drawable.img_90_celular_atencao,
        )
    }

}