package br.com.mobicare.cielo.simulator.simulation.presentation.simulation

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.field.TextFieldFlui
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_MILLION
import br.com.mobicare.cielo.commons.constants.ZERO_TEXT
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.moneyToDoubleValue
import br.com.mobicare.cielo.commons.utils.showKeyboard
import br.com.mobicare.cielo.commons.utils.textToMoneyBigDecimalFormat
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.FragmentSimulationValueBinding
import br.com.mobicare.cielo.databinding.LayoutFooterBinding
import br.com.mobicare.cielo.simulator.analytics.SalesSimulatorGA4
import br.com.mobicare.cielo.simulator.simulation.presentation.viewModel.SimulatorViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.math.BigDecimal

class SimulationValueFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null
    private var binding: FragmentSimulationValueBinding? = null
    private var footerBinding: LayoutFooterBinding? = null
    private val viewModel: SimulatorViewModel by sharedViewModel()

    private val args: SimulationValueFragmentArgs by navArgs()

    private val ga4: SalesSimulatorGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        LayoutFooterBinding.inflate(
            inflater, container, false
        ).also { footerBinding = it }

        return FragmentSimulationValueBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(SalesSimulatorGA4.SCREEN_VIEW_SIMULATOR)
    }

    private fun setupNavigation() {
        navigation = (requireActivity() as? CieloNavigation)?.also {
            it.configureCollapsingToolbar(
                CieloCollapsingToolbarLayout.Configurator(
                    isExpanded = true,
                    layoutMode = CieloCollapsingToolbarLayout.LayoutMode.NOT_SCROLLABLE,
                    toolbar = CieloCollapsingToolbarLayout.Toolbar(
                        title = getString(R.string.text_simulator_sale_value),
                        titleAppearance = CieloCollapsingToolbarLayout.ToolbarTitleAppearance(
                            expanded = R.style.bold_montserrat_24_cloud_800
                        ),
                        showBackButton = true
                    ),
                    footerView = footerBinding?.root
                )
            )
        }
        navigation?.setNavigationListener(this)
    }

    private fun setupListeners() {
        binding?.tvFieldSaleValue?.apply {
            prefixTextStyle = R.style.medium_montserrat_24_cloud_800
            textStyle = R.style.medium_montserrat_40_cloud_800
            iconError = R.drawable.ic_close_round_danger_400_16_dp
            errorTextStyle = R.style.medium_montserrat_12_danger_400
            setInputTypeTextField(InputType.TYPE_CLASS_NUMBER)
            requestFocus()
            requestFocusFromTouch()
            post {
                text = viewModel.simulationValue?.toPtBrRealString() ?: ZERO_TEXT
                requireActivity().showKeyboard(this)
            }
            setValidators(
                TextFieldFlui.Validator(
                    rule = { it.moneyToDoubleValue() >= ONE },
                    errorMessage = getString(R.string.text_simulator_select_error_minimum),
                    onResult = { isValid, _ ->
                        footerBinding?.btPrimary?.isButtonEnabled = isValid
                    }),
                TextFieldFlui.Validator(
                    rule = { it.moneyToDoubleValue() <= ONE_MILLION },
                    errorMessage = getString(R.string.text_simulator_select_error_maximum),
                    onResult = { isValid, _ ->
                        footerBinding?.btPrimary?.isButtonEnabled = isValid
                    })
            )
        }

        footerBinding?.btPrimary?.apply {
            text =
                getString(if (args.updateResult) R.string.sales_simulator_update else R.string.text_next_label)
            visible()
            setOnClickListener {
                val simulationValue =
                    binding?.tvFieldSaleValue?.getTextField()?.textToMoneyBigDecimalFormat()
                        ?: BigDecimal.ZERO

                viewModel.updateValue(simulationValue)

                if (args.updateResult) {
                    findNavController().navigateUp()
                } else findNavController().navigate(
                    SimulationValueFragmentDirections.actionSimulationValueFragmentToSimulationProductSelectionFragment()
                )
            }
        }
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