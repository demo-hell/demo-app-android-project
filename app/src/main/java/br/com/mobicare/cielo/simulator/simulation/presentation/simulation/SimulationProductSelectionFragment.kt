package br.com.mobicare.cielo.simulator.simulation.presentation.simulation

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.enum.CieloCardBrandIcons
import br.com.cielo.libflue.field.CieloSelectField
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.spannable.addSpannable
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.FragmentSimulationProductSelectionBinding
import br.com.mobicare.cielo.databinding.LayoutFooterBinding
import br.com.mobicare.cielo.databinding.LayoutSimulatorBrandItemBinding
import br.com.mobicare.cielo.databinding.LayoutSimulatorPaymentTypeItemBinding
import br.com.mobicare.cielo.eventTracking.utils.dividerItemDecoration
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.extensions.updateMargins
import br.com.mobicare.cielo.simulator.analytics.SalesSimulatorGA4
import br.com.mobicare.cielo.simulator.simulation.presentation.viewModel.SimulatorViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.math.BigDecimal

class SimulationProductSelectionFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null
    private var binding: FragmentSimulationProductSelectionBinding? = null
    private var footerBinding: LayoutFooterBinding? = null
    private val viewModel: SimulatorViewModel by sharedViewModel()

    private val ga4: SalesSimulatorGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        LayoutFooterBinding.inflate(
            inflater, container, false
        ).also { footerBinding = it }

        return FragmentSimulationProductSelectionBinding.inflate(
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
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(SalesSimulatorGA4.SCREEN_VIEW_SIMULATOR_SALE_TYPE)
    }

    private fun setupNavigation() {
        navigation = (requireActivity() as? CieloNavigation)?.also {
            it.configureCollapsingToolbar(
                CieloCollapsingToolbarLayout.Configurator(
                    isExpanded = true,
                    layoutMode = CieloCollapsingToolbarLayout.LayoutMode.NOT_SCROLLABLE,
                    toolbar = CieloCollapsingToolbarLayout.Toolbar(
                        title = getString(R.string.text_toolbar_simulator), showBackButton = true
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
                findNavController().navigateUp()
            }
        }

        binding?.btnBrandSelection?.apply {
            text = viewModel.simulatorSelectedProduct.value?.cardBrandName.toLowerCasePTBR()
                .capitalizePTBR()
            setOnSelectListener(::onSelectBrand)
        }

        binding?.btnSellTypeSelection?.apply {
            text =
                viewModel.simulatorSelectedPaymentType.value?.productDescription.toLowerCasePTBR()
                    .capitalizePTBR()
            setOnSelectListener(::onSelectPaymentType)
        }
        setupFooter()
    }

    private fun setupFooter() {
        footerBinding?.apply {
            btPrimary.apply {
                text = getString(R.string.sales_simulator_simulate_label_button)
                visible()
                setOnClickListener {
                    ga4.logClick(
                        screenName = SalesSimulatorGA4.SCREEN_VIEW_SIMULATOR_SALE_TYPE,
                        contentName = SalesSimulatorGA4.SIMULATE,
                        contentComponent = viewModel.simulatorSelectedProduct.value?.cardBrandName,
                        itemName = viewModel.simulatorSelectedPaymentType.value?.productDescription
                    )
                    findNavController().navigate(
                        SimulationProductSelectionFragmentDirections.actionSimulationProductSelectionFragmentToSimulationResultFragment()
                    )
                }
            }
            btSecondary.apply {
                gone()
            }
        }
    }

    private fun onSelectBrand(
        option: CieloSelectField.Option?, cieloSelectField: CieloSelectField
    ) {
        var tempBrand: Pair<String?, String?> = with(viewModel.simulatorSelectedProduct.value) {
            Pair(
                this?.cardBrandCode, this?.cardBrandName
            )
        }
        CieloListBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = getString(R.string.sales_simulator_brand_selection_title),
            ),
            dividerItemDecoration = dividerItemDecoration(requireContext()),
            layoutItemRes = R.layout.layout_simulator_brand_item,
            data = viewModel.simulatorProducts?.products?.map {
                Pair(
                    it.cardBrandCode, it.cardBrandName
                )
            }.orEmpty(),
            initialSelectedItem = tempBrand,
            onViewBound = { brand, isSelected, itemView ->
                LayoutSimulatorBrandItemBinding.bind(itemView).apply {
                    with(viewModel.simulatorProducts?.products) {
                        when (brand.first) {
                            this?.first()?.cardBrandCode -> this@apply.root.updateMargins(
                                top = itemView.resources.getDimensionPixelOffset(
                                    R.dimen.dimen_8dp
                                )
                            )
                        }
                    }
                    radioButton.isChecked = isSelected
                    brandIcon.setImageResource(
                        CieloCardBrandIcons.getCardBrandIconResourceId(
                            brand.first?.toInt()
                        )
                    )
                    tvBrandName.text = brand.second.toLowerCasePTBR().capitalizePTBR()
                }
            },
            onItemClicked = { brand, _, bottomSheet ->
                tempBrand = brand
                viewModel.updateSelectedBrand(tempBrand.first)
                bottomSheet.dismiss()
            },
            disableExpandableMode = true,
        ).show(childFragmentManager, tag)
    }

    private fun onSelectPaymentType(
        option: CieloSelectField.Option?, cieloSelectField: CieloSelectField
    ) {
        var tempPaymentType: Pair<Int?, String?> =
            with(viewModel.simulatorSelectedPaymentType.value) {
                Pair(
                    this?.productCode, this?.productDescription
                )
            }
        CieloListBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = getString(R.string.sales_simulator_payment_type_selection_title),
            ),
            dividerItemDecoration = dividerItemDecoration(requireContext()),
            layoutItemRes = R.layout.layout_simulator_payment_type_item,
            data = viewModel.availablePaymentTypes?.map {
                Pair(
                    it.productCode, it.productDescription
                )
            }.orEmpty(),
            initialSelectedItem = tempPaymentType,
            onViewBound = { paymentType, isSelected, itemView ->
                LayoutSimulatorPaymentTypeItemBinding.bind(itemView).apply {
                    radioButton.isChecked = isSelected
                    tvPaymentType.text =
                        with(paymentType.second.toLowerCasePTBR().capitalizePTBR()) {
                            when (this) {
                                getString(R.string.text_simulator_installment_store) -> {
                                    formatTitleAndSubtitle(
                                        R.string.text_simulator_installment_store,
                                        R.string.text_simulator_fees_store
                                    )

                                }

                                else -> this
                            }
                        }
                }
            },
            onItemClicked = { paymentType, _, bottomSheet ->
                tempPaymentType = paymentType
                viewModel.updateSelectedPaymentType(paymentType.first)
                bottomSheet.dismiss()
            },
            disableExpandableMode = true,
        ).show(childFragmentManager, tag)
    }

    private fun formatTitleAndSubtitle(
        @StringRes title: Int, @StringRes subtitle: Int
    ) = SpannableStringBuilder().apply {
        append(
            getString(title).addSpannable(
                TextAppearanceSpan(
                    requireContext(), R.style.medium_montserrat_16_neutral_800
                )
            )
        )
        append(Text.NEW_LINE)
        append(
            getString(subtitle).addSpannable(
                TextAppearanceSpan(
                    requireActivity(), R.style.medium_montserrat_12_neutral_600
                )
            )
        )
    }

    private fun setupObservers() {
        viewModel.simulatorSelectedProduct.observe(viewLifecycleOwner) { selectedProduct ->
            binding?.apply {
                btnBrandSelection.apply {
                    setSelectedOption(selectedProduct?.let {
                        CieloSelectField.Option(
                            it.cardBrandCode, it.cardBrandName.toLowerCasePTBR().capitalizePTBR()
                        )
                    })
                }
                btnSellTypeSelection.isFieldEnabled = selectedProduct != null
            }
        }

        viewModel.simulatorSelectedPaymentType.observe(viewLifecycleOwner) { selectedPaymentType ->
            binding?.btnSellTypeSelection?.setSelectedOption(selectedPaymentType?.let {
                CieloSelectField.Option(
                    it.productCode, it.productDescription.toLowerCasePTBR().capitalizePTBR()
                )
            })

            footerBinding?.apply {
                btPrimary.isButtonEnabled = selectedPaymentType != null
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