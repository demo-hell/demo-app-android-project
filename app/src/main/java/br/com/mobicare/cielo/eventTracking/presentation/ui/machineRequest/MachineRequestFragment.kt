package br.com.mobicare.cielo.eventTracking.presentation.ui.machineRequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.SIX
import br.com.cielo.libflue.util.THREE
import br.com.cielo.libflue.util.TWO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.MachineRequestTabFragmentBinding
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterChip
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterChipType
import br.com.mobicare.cielo.eventTracking.presentation.analytics.EventTrackingGA4
import br.com.mobicare.cielo.eventTracking.presentation.analytics.EventTrackingGA4.Companion.UNDEFINED_MACHINE_NAME
import br.com.mobicare.cielo.eventTracking.presentation.ui.MainTabFragmentDirections
import br.com.mobicare.cielo.eventTracking.utils.BottomSheetFilterSelect
import br.com.mobicare.cielo.eventTracking.utils.EventsRequestResource
import br.com.mobicare.cielo.eventTracking.utils.FilterBottomSheetEvents
import br.com.mobicare.cielo.eventTracking.utils.MachineRequestItem
import br.com.mobicare.cielo.eventTracking.utils.MenuTranslator
import br.com.mobicare.cielo.eventTracking.utils.ShimmerMachineRequestItem
import br.com.mobicare.cielo.main.domain.Menu
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MachineRequestFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null

    private val binding: MachineRequestTabFragmentBinding by viewBinding()
    private val machineRequestViewModel: MachineRequestViewModel by viewModel()

    private val eventTrackingGA4: EventTrackingGA4 by inject()

    private var isScrolled = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        machineRequestViewModel.startFilter(getFirstFilter())
    }

    private fun getFirstFilter() = listOf(
        CieloFilterChip(
            id = ONE,
            filterBottomSheetTitle = getString(R.string.my_request_filter_request_status_title),
            filterPossibilities = requireContext().resources.getStringArray(R.array.filter_events_status).map { it },
            filterType = CieloFilterChipType.STATUS
        ),
        CieloFilterChip(
            id = TWO,
            filterName = getString(R.string.my_request_filter_request_date_title),
            filterBottomSheetTitle = getString(R.string.my_request_filter_request_date_title),
            filterPossibilities = requireContext().resources.getStringArray(R.array.filter_request_date).map { it },
            filterType = CieloFilterChipType.DATE
        ),
        CieloFilterChip(
            id = THREE,
            filterName = getString(R.string.my_request_filter_request_type_title),
            filterBottomSheetTitle = getString(R.string.my_request_filter_request_type_title),
            filterPossibilities = requireContext().resources.getStringArray(R.array.filter_machine_request_type).map { it },
            filterType = CieloFilterChipType.REQUEST
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.showHelpButton(isShow = true)
            navigation?.showBackButton(isShow = true)
            navigation?.setupToolbar(
                title = getString(R.string.my_requests_title_string),
                isCollapsed = isScrolled,
                subtitle = getString(R.string.my_requests_subtitle_string)
            )
        }
        observeFilterChanges()
        setupRecyclerView()
    }

    private fun setupListeners(isFirstFilter: Boolean) {

        if (isFirstFilter) {
            val requestMaterial = MenuTranslator(
                code = Router.APP_ANDROID_SUPPLIES, name = getString(R.string.request_materials)
            )
            val requestMachine = MenuTranslator(
                code = Router.APP_ANDROID_NEW_TERMINAL, name = getString(R.string.request_machine)
            )
            val requestSupport = MenuTranslator(
                code = Router.APP_ANDROID_HELP_DESK, name = getString(R.string.text_technical_suppport_title)
            )
            binding.apply {
                myRequestsNothingYet.text = getString(R.string.my_requests_nothing_yet_string)
                myRequestsNothingYetDesc.visible()
                emptyListRequestMaterial.text = getString(R.string.request_materials)
                emptyListRequestMaterial.setOnClickListener {
                    eventTrackingGA4.logEmptyMachineRequestButtonClick(machineRequestViewModel.getCurrentSelectedFilterStatus(), requestMaterial.name)
                    navigate(requestMaterial)
                }
                emptyListRequestMachine.text = getString(R.string.request_machine)
                emptyListRequestMachine.setOnClickListener {
                    eventTrackingGA4.logEmptyMachineRequestButtonClick(machineRequestViewModel.getCurrentSelectedFilterStatus(), requestMachine.name)
                    navigate(requestMachine)
                }
                emptyListRequestSupport.text = getString(R.string.text_technical_suppport_title)
                emptyListRequestSupport.setOnClickListener {
                    eventTrackingGA4.logEmptyMachineRequestButtonClick(machineRequestViewModel.getCurrentSelectedFilterStatus(), requestSupport.name)
                    navigate(requestSupport)
                }
            }
        } else {
            binding.apply {
                myRequestsNothingYet.text = getString(R.string.my_requests_nothing_yet_filtered_string)
                myRequestsNothingYetDesc.gone()
                emptyListRequestMaterial.text = getString(R.string.clean_last_filter)
                emptyListRequestMaterial.setOnClickListener {
                    machineRequestViewModel.clearLastSelectedFilter()
                }
                emptyListRequestMachine.text = getString(R.string.clean_all_filter)
                emptyListRequestMachine.setOnClickListener {
                    machineRequestViewModel.startFilter(getFirstFilter())
                }
                emptyListRequestSupport.text = getString(R.string.back_to_begin)
                emptyListRequestSupport.setOnClickListener {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }

    private fun navigate(menu: MenuTranslator) {
        Router.navigateTo(requireContext(), menu.toMenu(), object : Router.OnRouterActionListener {
            override fun actionNotFound(action: Menu) {
                //Do nothing
            }
        })
    }

    private fun setupRecyclerView() {
        binding.myRequestsRecycler.layoutManager = LinearLayoutManager(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            machineRequestViewModel.deliveryEventList.observe(viewLifecycleOwner) { machineRequestResource ->
                when (machineRequestResource) {
                    EventsRequestResource.Loading -> requestDeliveryEventLoading()
                    is EventsRequestResource.Error -> requestDeliveryEventListError(machineRequestResource)
                    is EventsRequestResource.Success -> requestDeliveryEventSuccess(machineRequestResource)
                }
            }
        }
    }

    private fun requestDeliveryEventLoading() {
        binding.myRequestClEmptyList.gone()
        binding.myRequestClError.gone()
        binding.filterHorizontalScroll.gone()

        binding.myRequestsRecycler.visible()
        binding.filterHorizontalScrollShimmer.visible()

        val shimmerList = mutableListOf<MachineRequestItem>().apply {
            repeat(SIX) {
                add(
                    ShimmerMachineRequestItem()
                )
            }
        }
        binding.myRequestsRecycler.adapter = MachineRequestAdapter(shimmerList) { _, _ ->
            //Do nothing
        }
    }

    private fun requestDeliveryEventListError(eventsRequestResource: EventsRequestResource.Error) {
        binding.myRequestsRecycler.gone()
        binding.myRequestClEmptyList.gone()
        binding.filterHorizontalScrollShimmer.gone()

        binding.myRequestClError.visible()
        binding.filterHorizontalScroll.visible()

        val currentSelectedFilter = machineRequestViewModel.getCurrentSelectedFilterStatus()
        eventTrackingGA4.logMachineRequestException(currentSelectedFilter, eventsRequestResource.error.newErrorMessage)


        binding.myRequestsReload.setOnClickListener {
            machineRequestViewModel.requestDeliveryEventList()
        }
    }

    private fun requestDeliveryEventSuccess(eventsRequestResource: EventsRequestResource.Success<List<MachineRequestItem>>) {
        binding.myRequestClError.gone()
        binding.myRequestClEmptyList.gone()
        binding.filterHorizontalScrollShimmer.gone()
        binding.filterHorizontalScroll.visible()

        binding.myRequestsRecycler.adapter = MachineRequestAdapter(eventsRequestResource.data) { itemView, machineRequest ->

            val machineName =
                machineRequest.requestMachine?.firstOrNull()?.name?.normalizeToLowerSnakeCase() ?: UNDEFINED_MACHINE_NAME
            val status = getString(machineRequest.requestStatus?.statusText ?: ZERO)
            eventTrackingGA4.logMachineListClick(machineRequestViewModel.getCurrentSelectedFilterStatus(), machineName, status)
            val action =
                MainTabFragmentDirections.actionMainTabFragmentToMachineRequestDetailsFragment()
                    .setMachineRequestItem(machineRequest)
            itemView.findNavController().navigate(action)
        }
        binding.myRequestsRecycler.visible()

        if (eventsRequestResource.data.isEmpty()) {
            eventTrackingGA4.logEmptyMachineRequestScreenView(machineRequestViewModel.getCurrentSelectedFilterStatus())
            binding.myRequestsRecycler.gone()
            binding.myRequestClEmptyList.visible()
        } else {
            eventTrackingGA4.logMachineRequestScreenView(machineRequestViewModel.getCurrentSelectedFilterStatus())
        }

        binding.machineRequestNestedScroll.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            isScrolled = scrollY > ZERO
        }
    }

    private fun observeFilterChanges() {
        viewLifecycleOwner.lifecycleScope.launch {
            machineRequestViewModel.cieloFilterMachineList.observe(viewLifecycleOwner) { filterMachineRequest ->
                binding.filterChipGroup.apply {
                    removeAllViews()
                    setupListeners(filterMachineRequest?.lastOrNull()?.all { it.currentSelected <= ZERO } == true)
                    filterMachineRequest?.lastOrNull()?.forEach { cieloFilterChip ->
                        val chip = (Chip.inflate(requireContext(), R.layout.filter_chip, null) as Chip).apply {
                            id = View.generateViewId()
                            text = cieloFilterChip.filterPossibilities.getOrNull(cieloFilterChip.currentSelected)
                                ?: cieloFilterChip.filterName ?: cieloFilterChip.filterPossibilities.getOrNull(ZERO)
                            isChecked = cieloFilterChip.isChecked
                            isCheckable = false

                            setOnClickListener {
                                showFilterSelectBottomSheet(cieloFilterChip, filterMachineRequest)
                            }
                        }
                        addView(chip)
                    }
                }
            }
        }
    }

    private fun showFilterSelectBottomSheet(cieloFilterChip: CieloFilterChip, filterMachineRequest: MutableSet<List<CieloFilterChip>>) {
        BottomSheetFilterSelect.show(this@MachineRequestFragment, cieloFilterChip) { bottonSheetEvents ->
            when (bottonSheetEvents) {
                is FilterBottomSheetEvents.MainButtonClick -> updateMachineFilter(
                    filterMachineRequest,
                    cieloFilterChip,
                    bottonSheetEvents.selected
                )

                FilterBottomSheetEvents.SecondaryButtonClick -> updateMachineFilter(
                    filterMachineRequest,
                    cieloFilterChip,
                    ONE_NEGATIVE
                )

                FilterBottomSheetEvents.HeaderOnCloseTap -> {
                    //Do nothing
                }
            }
        }
    }

    private fun updateMachineFilter(filterMachineRequest: MutableSet<List<CieloFilterChip>>, cieloFilterChip: CieloFilterChip, selected: Int) {
        val filter = filterMachineRequest.last().map { mapCieloFilterChip ->
            if (mapCieloFilterChip.id == cieloFilterChip.id) {
                mapCieloFilterChip.copy(currentSelected = selected)
            } else {
                mapCieloFilterChip
            }
        }
        machineRequestViewModel.updateMachineFilter(filter)
    }
}