package br.com.mobicare.cielo.suporteTecnico.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.bottomsheet.CieloContentBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.inputtext.CieloTextInputField
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.FIVE
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.OPEN_REQUEST
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.databinding.BottomSheetLogicalNumberBinding
import br.com.mobicare.cielo.databinding.FragmentMerchantEquipmentsBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.suporteTecnico.data.OpenTicket
import br.com.mobicare.cielo.suporteTecnico.presentation.adapter.EquipmentAdapter
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.MerchantEquipmentsViewModel
import br.com.mobicare.cielo.suporteTecnico.utils.UiStateEquipments
import br.com.mobicare.cielo.taxaPlanos.domain.TaxaPlanosMachine
import br.com.mobicare.cielo.taxaPlanos.domain.TerminalsResponse
import org.koin.androidx.viewmodel.ext.android.viewModel
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class MerchantEquipmentsFragment :
    BaseFragment(),
    CieloNavigationListener {
    private var _binding: FragmentMerchantEquipmentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterEquipments: EquipmentAdapter
    private val viewModel: MerchantEquipmentsViewModel by viewModel()
    private var rentalEquipments: Boolean? = null

    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentMerchantEquipmentsBinding
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
        GA4.logScreenView(OPEN_REQUEST)
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
            filterLogicNumber.setOnSearchRealtime { query ->
                adapterEquipments.run {
                    filter(query)
                    setNotFoundTextVisibility(itemCount == ZERO, query)
                }
            }
        }
    }

    private fun setNotFoundTextVisibility(
        isVisible: Boolean,
        query: String,
    ) {
        binding.apply {
            if (isVisible) {
                tvNotFound.text = getString(R.string.label_not_found, query)
                containerNotFound.visible()
            } else {
                containerNotFound.gone()
            }

            rvEquipaments.visible(isVisible.not())
        }
    }

    private fun setupObservers() {
        viewModel.merchantEquipments.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiStateEquipments.ShowLoading -> onShowLoading()
                is UiStateEquipments.HideLoading -> onHideLoading()
                is UiStateEquipments.Success -> onEquipmentSuccess(state.data)
                else ->
                    onEquipamentError()
            }
        }
    }

    fun onReload() {
        viewModel.getMerchantEquipment()
    }

    private fun onShowLoading() {
        binding.apply {
            containerOpenTicket.gone()
            progress.root.visible()
            errorInclude.root.gone()
        }
    }

    private fun onHideLoading() {
        binding.apply {
            containerOpenTicket.visible()
            progress.root.gone()
            errorInclude.root.gone()
        }
    }

    private fun onEquipamentError() {
        binding.apply {
            containerOpenTicket.gone()
            progress.root.gone()
            errorInclude.root.visible()
            errorInclude.btnReload.setOnClickListener {
                onReload()
            }
        }
    }

    private fun onEquipmentSuccess(data: TerminalsResponse?) {
        adapterEquipments =
            EquipmentAdapter(onTap = { selectedMachine ->
                setupBottomSheet(selectedMachine)
            })

        data?.terminals?.let { terminals ->
            adapterEquipments.setItems(terminals)
        }

        binding.rvEquipaments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterEquipments
        }
        binding.apply {
            containerOpenTicket.visible()
            progress.root.gone()
            errorInclude.root.gone()
        }
        rentalEquipments = data?.rentalEquipments
    }

    private fun navigateToTicket(openTicket: OpenTicket) {
        findNavController().navigate(
            MerchantEquipmentsFragmentDirections.actionMerchantEquipmentsFragmentToProblemListFragment(
                openTicket,
            ),
        )
    }

    private fun setupBottomSheet(selectedMachine: TaxaPlanosMachine) {
        activity?.hideSoftKeyboard()
        GA4.logClick(OPEN_REQUEST, selectedMachine.model.toString())

        CieloContentBottomSheet
            .create(
                headerConfigurator =
                    CieloBottomSheet.HeaderConfigurator(
                        title = getString(R.string.provide_version_number),
                        showCloseButton = false,
                    ),
                contentLayoutRes = R.layout.bottom_sheet_logical_number,
                onContentViewCreated = { view, bs ->
                    BottomSheetLogicalNumberBinding.bind(view).apply {
                        tifNumberVersion.apply {
                            setValidators(
                                CieloTextInputField.Validator(
                                    rule = { it.extractedValue.length > FIVE },
                                    onResult = { isValid, _ ->
                                        val enabled: Boolean
                                        if (isValid.not()) {
                                            this.setError(getString(R.string.version_number_invalid))
                                            enabled = false
                                        } else {
                                            this.unsetError()
                                            enabled = true
                                        }
                                        btnConfirm.isButtonEnabled = enabled
                                    },
                                ),
                            )
                            this.validationMode = CieloTextInputField.ValidationMode.TEXT_CHANGED
                        }

                        btnConfirm.setOnClickListener {
                            if (tifNumberVersion.textInputEditText.text
                                    .toString()
                                    .length > FIVE
                            ) {
                                navigateToTicket(
                                    openTicketData(
                                        taxaPlansMachine = selectedMachine,
                                        versionNumber = tifNumberVersion.textInputEditText.text.toString(),
                                    ),
                                )
                                bs.dismiss()
                            } else {
                                tifNumberVersion.setError(getString(R.string.version_number_invalid))
                            }
                        }

                        tvHelpAboutMachine.setOnClickListener {
                            bs.dismiss()
                            openBottomSheetHelpAboutMachine()
                        }
                    }
                },
                disableExpandableMode = true,
            ).show(requireActivity().supportFragmentManager, EMPTY)
    }

    private fun openBottomSheetHelpAboutMachine() {
        findNavController().navigate(
            MerchantEquipmentsFragmentDirections.actionMerchantEquipmentsFragmentToHelpAboutMachineFragment(),
        )
    }

    private fun openTicketData(
        taxaPlansMachine: TaxaPlanosMachine,
        versionNumber: String,
    ): OpenTicket =
        OpenTicket(
            issueCode = null,
            contactName = null,
            phones = emptyList(),
            edited = null,
            openingHourCode = null,
            openingHourText = null,
            address = null,
            logicalNumber = taxaPlansMachine.logicalNumber,
            logicalNumberDigit = taxaPlansMachine.logicalNumberDigit,
            version = versionNumber,
            technologyType = taxaPlansMachine.technology,
            index = null,
            rentalMachine = rentalEquipments,
        )
}
