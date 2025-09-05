package br.com.mobicare.cielo.simulator.simulation.presentation.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.enum.CieloCardBrandIcons
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.BottomSheetSimulationDetailsBinding
import br.com.mobicare.cielo.databinding.FragmentSimulationResultBinding
import br.com.mobicare.cielo.databinding.LayoutFooterBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.simulator.analytics.SalesSimulatorGA4
import br.com.mobicare.cielo.simulator.simulation.domain.model.Simulation
import br.com.mobicare.cielo.simulator.simulation.presentation.state.UiSimulatorResultState
import br.com.mobicare.cielo.simulator.simulation.presentation.viewModel.SimulatorViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.math.BigDecimal

class SimulationResultFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null
    private var binding: FragmentSimulationResultBinding? = null
    private var footerBinding: LayoutFooterBinding? = null
    private val viewModel: SimulatorViewModel by sharedViewModel()

    private val showReceiveTotalValueViewModel: SaleSimulatorReceiveTotalValueViewModel by sharedViewModel()

    private val ga4: SalesSimulatorGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        LayoutFooterBinding.inflate(
            inflater, container, false
        ).also { footerBinding = it }

        return FragmentSimulationResultBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
        setupObservers()
        viewModel.getSimulation()
    }

    private fun setupNavigation() {
        navigation = (requireActivity() as? CieloNavigation)?.also {
            it.configureCollapsingToolbar(
                CieloCollapsingToolbarLayout.Configurator(
                    isExpanded = true,
                    layoutMode = CieloCollapsingToolbarLayout.LayoutMode.NOT_SCROLLABLE,
                    toolbar = CieloCollapsingToolbarLayout.Toolbar(
                        title = getString(R.string.sales_simulator_simulate_result_title),
                        showBackButton = true
                    ),
                    footerView = footerBinding?.root
                )
            )
        }
        navigation?.setNavigationListener(this)
    }

    private fun setupListeners() {
        binding?.tvValue?.apply {
            text =
                viewModel.simulationValue?.toPtBrRealString() ?: BigDecimal.ZERO.toPtBrRealString()
            setOnClickListener {
                findNavController().navigate(
                    SimulationResultFragmentDirections.actionSimulationResultFragmentToSimulationValueFragment()
                    .apply {
                        setUpdateResult(true)
                    })
            }
        }

        footerBinding?.apply {
            btPrimary.gone()
            btSecondary.apply {
                setText(getString(R.string.sales_simulator_redo_simulation))
                visible()
                setOnClickListener {
                    ga4.logResultRedoButtonClick(viewModel.simulatorSelectedPaymentType.value)
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun showDetailsBs(simulatorResult: Simulation) {
        CieloContentBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = getString(R.string.sales_simulator_simulation_details)
            ),
            contentLayoutRes = R.layout.bottom_sheet_simulation_details,
            onContentViewCreated = { view, _ ->
                val bsBinding = BottomSheetSimulationDetailsBinding.bind(view)
                bsBinding.apply {
                    ivBrandIcon.setImageResource(
                        CieloCardBrandIcons.getCardBrandIconResourceId(
                            simulatorResult.cardBrandCode
                        )
                    )
                    tvBrand.text = viewModel.simulatorSelectedProduct.value?.cardBrandName.toLowerCasePTBR().capitalizePTBR()
                    tvSellType.text =
                        viewModel.simulatorSelectedPaymentType.value?.productDescription.toLowerCasePTBR()
                            .capitalizePTBR()
                    tvReceivingTerm.text = resources.getQuantityString(
                        R.plurals.recepitDeadline,
                        simulatorResult.receivableRemainingDays?.toInt() ?: ZERO,
                        simulatorResult.receivableRemainingDays
                    )
                }
            },
            disableExpandableMode = true
        ).show(childFragmentManager, tag)
    }

    private fun setupObservers() {
        viewModel.simulatorResultState.observe(viewLifecycleOwner) { resultState ->
            when (resultState) {
                UiSimulatorResultState.HideLoading -> onHideLoading()
                is UiSimulatorResultState.Error -> showErrorBs(resultState.error)
                UiSimulatorResultState.ShowLoading -> onShowLoading()
                is UiSimulatorResultState.Success -> setupResult(resultState.simulatorResult)
            }
        }
    }

    private fun setupResult(simulatorResult: Simulation) {
        binding?.apply {
            when (simulatorResult.flexibleTerm) {
                true -> {
                    ftPaymentTermBadge.root.gone()
                    ftPaymentTermRABadge.root.visible()
                }

                else -> {
                    ftPaymentTermBadge.root.visible()
                    ftPaymentTermRABadge.root.gone()
                }
            }

            rvInstallments.apply {
                showReceiveTotalValueViewModel.receiveTotalValueLiveData.observe(viewLifecycleOwner) {
                    adapter = SimulationInstallmentAdapter(simulatorResult, it)
                    addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                }
            }

            tvDetails.setOnClickListener {
                showDetailsBs(simulatorResult)
            }
        }

        ga4.logResultDisplayContent(viewModel.simulatorSelectedPaymentType.value, simulatorResult)
    }

    private fun onHideLoading() {
        binding?.apply {
            headerContentGroup.visible()
            contentContentGroup.visible()
            shimmerHeader.gone()
            shimmerContent.gone()
            shimmerHeader.stopShimmer()
            shimmerContent.stopShimmer()
            footerBinding?.btSecondary?.isEnabled = true
        }
    }

    private fun onShowLoading() {
        binding?.apply {
            headerContentGroup.gone()
            contentContentGroup.gone()
            shimmerHeader.visible()
            shimmerContent.visible()
            shimmerHeader.startShimmer()
            shimmerContent.startShimmer()
            footerBinding?.btSecondary?.isEnabled = false
        }
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
                viewModel.getSimulation()
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
        activity?.finish()
    }

    private fun showErrorBs(error: NewErrorMessage?) {
        ga4.logException(SalesSimulatorGA4.SCREEN_VIEW_SIMULATOR_SALE_TYPE, error)
        showBottomSheetGoToHome(
            title = R.string.commons_generic_error_title,
            message = R.string.pos_virtual_error_message_generic,
            image = R.drawable.img_90_celular_atencao,
        )
    }

    override fun onBackButtonClicked(): Boolean {
        navigation?.goToHome()
        return super.onBackButtonClicked()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}