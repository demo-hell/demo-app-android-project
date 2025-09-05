package br.com.mobicare.cielo.eventTracking.presentation.ui.callsRequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.SIX
import br.com.cielo.libflue.util.TWO
import br.com.cielo.libflue.util.extensions.visible
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.FragmentCallsRequestBinding
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterChip
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterChipType
import br.com.mobicare.cielo.eventTracking.presentation.analytics.CallsTrackingGA4
import br.com.mobicare.cielo.eventTracking.utils.BottomSheetFilterSelect
import br.com.mobicare.cielo.eventTracking.utils.CallRequestItem
import br.com.mobicare.cielo.eventTracking.utils.EventsRequestResource
import br.com.mobicare.cielo.eventTracking.utils.FilterBottomSheetEvents
import br.com.mobicare.cielo.eventTracking.utils.MenuTranslator
import br.com.mobicare.cielo.eventTracking.utils.ShimmerCallRequestItem
import br.com.mobicare.cielo.main.domain.Menu
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CallsRequestFragment : BaseFragment() {

    private val binding: FragmentCallsRequestBinding by viewBinding()
    private val viewModel: CallsRequestViewModel by viewModel()
    private var isScrolled = false
    private var isChipChecked = false
    private var hasSearch = false
    private val callsTrackingGA4: CallsTrackingGA4 by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.startFilter(getFirstFilter())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFilterChanges()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.callsRequestsRecycler.layoutManager = LinearLayoutManager(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.callsEventList.observe(viewLifecycleOwner) { callResource ->
                when (callResource) {
                    EventsRequestResource.Loading -> requestCallsEventLoading()
                    is EventsRequestResource.Error -> requestCallsEventListError(callResource)
                    is EventsRequestResource.Success -> requestCallsEventSuccess(callResource)
                }
            }
        }
    }

    private fun requestCallsEventLoading() {
        binding.callsRequestClEmptyList.gone()
        binding.callsRequestClError.gone()
        binding.clHorizontalScroll.gone()
        binding.sfSearchEvents.gone()
        binding.callsRequestClEmptySearch.gone()

        binding.callsRequestsRecycler.visible()
        binding.filterHorizontalScrollShimmer.visible()

        val shimmerList = mutableListOf<CallRequestItem>().apply {
            repeat(SIX) {
                add(
                    ShimmerCallRequestItem()
                )
            }
        }
        binding.callsRequestsRecycler.adapter = CallsRequestAdapter(shimmerList)
    }

    private fun requestCallsEventListError(eventsRequestResource: EventsRequestResource.Error) {
        binding.callsRequestsRecycler.gone()
        binding.callsRequestClEmptyList.gone()
        binding.filterHorizontalScrollShimmer.gone()

        binding.callsRequestClError.visible()
        binding.clHorizontalScroll.visible()

        val currentSelectedFilter = viewModel.getCurrentSelectedFilterStatus()
        callsTrackingGA4.logCallsRequestException(currentSelectedFilter, eventsRequestResource.error.newErrorMessage)

        binding.callsRequestsReload.setOnClickListener {
            viewModel.requestCallsEventList()
        }
    }

    private fun requestCallsEventSuccess(eventsRequestResource: EventsRequestResource.Success<List<CallRequestItem>>) {
        binding.callsRequestClError.gone()
        binding.callsRequestClEmptyList.gone()
        binding.filterHorizontalScrollShimmer.gone()
        binding.clHorizontalScroll.visible()
        binding.sfSearchEvents.visible(isChipChecked)

        binding.callsRequestsRecycler.adapter = CallsRequestAdapter(eventsRequestResource.data, binding.sfSearchEvents.getText())
        binding.callsRequestsRecycler.visible()

        if (eventsRequestResource.data.isEmpty() && !binding.sfSearchEvents.isVisible()) {
            callsTrackingGA4.logEmptyCallsRequestScreenView(viewModel.getCurrentSelectedFilterStatus())
            binding.callsRequestsRecycler.gone()
            binding.callsRequestClEmptyList.visible()
        } else {
            callsTrackingGA4.logCallsRequestScreenView(viewModel.getCurrentSelectedFilterStatus())
        }

        if (eventsRequestResource.data.isEmpty() && binding.sfSearchEvents.isVisible()){
            binding.callsRequestsRecycler.gone()
            binding.callsRequestClEmptySearch.visible()
        }

        binding.callsRequestNestedScroll.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            isScrolled = scrollY > ZERO
        }
    }

    private fun observeFilterChanges() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.callsFilterList.observe(viewLifecycleOwner) { filterCallsRequest ->
                binding.filterChipGroup.apply {
                    removeAllViews()
                    setupListeners(filterCallsRequest?.lastOrNull()?.all { it.currentSelected <= ZERO } == true)
                    filterCallsRequest?.lastOrNull()?.forEach { cieloFilterChip ->
                            val chip = (Chip.inflate(requireContext(), R.layout.filter_chip, null) as Chip).apply {
                                if (cieloFilterChip.filterType == CieloFilterChipType.SEARCH){
                                    id = View.generateViewId()
                                    chipIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_search_br)
                                    chipIconTint = AppCompatResources.getColorStateList(requireContext(), R.color.cielo_accent_500)
                                    closeIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.close)
                                    isChipIconVisible = true
                                    isCloseIconVisible = isChipChecked
                                    isChecked = isChipChecked
                                    text = cieloFilterChip.filterName

                                    setOnCheckedChangeListener { _, isChecked ->
                                        if (isChecked){
                                            isChipChecked = true
                                            this.isChecked = isChipChecked
                                            this.isCloseIconVisible = true
                                            binding.sfSearchEvents.visible()
                                            binding.sfSearchEvents.requestFocus()
                                            val paddingTop = resources.getDimensionPixelSize(R.dimen.dimen_120dp)
                                            binding.callsRequestsRecycler.setPadding(ZERO, paddingTop, ZERO, ZERO)
                                        }else{
                                            isChipChecked = false
                                            this.isChecked = isChipChecked
                                            this.isCloseIconVisible = false
                                            binding.sfSearchEvents.clearFocus()
                                            binding.sfSearchEvents.gone()
                                            clearSearchFilter(cieloFilterChip, filterCallsRequest)
                                            hasSearch = isChipChecked
                                            val paddingTop = resources.getDimensionPixelSize(R.dimen.dimen_80dp)
                                            binding.callsRequestsRecycler.setPadding(ZERO, paddingTop, ZERO, ZERO)
                                        }
                                    }
                                    updateSearch(cieloFilterChip, filterCallsRequest)

                                }else{
                                    id = View.generateViewId()
                                    text = cieloFilterChip.filterPossibilities.getOrNull(cieloFilterChip.currentSelected)
                                        ?: cieloFilterChip.filterName ?: cieloFilterChip.filterPossibilities.getOrNull(ZERO)
                                    isChecked = cieloFilterChip.isChecked
                                    isCheckable = false

                                    setOnClickListener {
                                        showFilterSelectBottomSheet(cieloFilterChip, filterCallsRequest)
                                    }
                                }
                            }
                            addView(chip)
                    }
                }
            }
        }
    }

    private fun clearSearchFilter(cieloFilterChip: CieloFilterChip, filterCallsRequest: MutableSet<List<CieloFilterChip>>) {
        if (!isChipChecked && !binding.sfSearchEvents.isVisible() && hasSearch){
            binding.sfSearchEvents.clearText()
            viewModel.setSearchQuery("")
            updateCallFilter(filterCallsRequest, cieloFilterChip, ONE_NEGATIVE)
        }
        if (!isChipChecked && !binding.sfSearchEvents.isVisible() && !hasSearch){
            binding.sfSearchEvents.clearText()
        }
    }

    private fun updateSearch(cieloFilterChip: CieloFilterChip, filterCallsRequest: MutableSet<List<CieloFilterChip>>){

        binding.sfSearchEvents.setOnSearch {
                hasSearch = true
                viewModel.setSearchQuery(binding.sfSearchEvents.getText())
                binding.sfSearchEvents.clearFocus()
                binding.sfSearchEvents.gone()
                callsTrackingGA4.logCallsSearchRequest(viewModel.getCurrentSelectedFilterStatus(), binding.sfSearchEvents.getText())
                updateCallFilter(filterCallsRequest, cieloFilterChip, ONE_NEGATIVE)
            }
    }

    private fun showFilterSelectBottomSheet(cieloFilterChip: CieloFilterChip, filterCallsRequest: MutableSet<List<CieloFilterChip>>) {
        BottomSheetFilterSelect.show(this@CallsRequestFragment, cieloFilterChip) { bottomSheetEvents ->
            when (bottomSheetEvents) {
                is FilterBottomSheetEvents.MainButtonClick -> updateCallFilter(
                    filterCallsRequest,
                    cieloFilterChip,
                    bottomSheetEvents.selected
                )

                FilterBottomSheetEvents.SecondaryButtonClick -> updateCallFilter(
                    filterCallsRequest,
                    cieloFilterChip,
                    ONE_NEGATIVE
                )

                FilterBottomSheetEvents.HeaderOnCloseTap -> {}
            }
        }
    }

    private fun updateCallFilter(filterCallsRequest: MutableSet<List<CieloFilterChip>>, cieloFilterChip: CieloFilterChip, selected: Int) {
        val filter = filterCallsRequest.last().map { mapCieloFilterChip ->
            if (mapCieloFilterChip.id == cieloFilterChip.id) {
                mapCieloFilterChip.copy(currentSelected = selected)
            } else {
                mapCieloFilterChip
            }
        }
        viewModel.updateCallsFilter(filter)
    }

    private fun navigate(menu: MenuTranslator) {
        Router.navigateTo(requireContext(), menu.toMenu(), object : Router.OnRouterActionListener {
            override fun actionNotFound(action: Menu) {}
        })
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
                callsRequestsNothingYet.text = getString(R.string.my_calls_nothing_yet_string)
                callsRequestsNothingYetDesc.visible()
                emptyListRequestMaterial.text = getString(R.string.request_materials)
                emptyListRequestMaterial.setOnClickListener {
                    callsTrackingGA4.logEmptyCallsRequestButtonClick(viewModel.getCurrentSelectedFilterStatus(), requestMaterial.name)
                    navigate(requestMaterial)
                }
                emptyListRequestMachine.text = getString(R.string.request_machine)
                emptyListRequestMachine.setOnClickListener {
                    callsTrackingGA4.logEmptyCallsRequestButtonClick(viewModel.getCurrentSelectedFilterStatus(), requestMachine.name)
                    navigate(requestMachine)
                }
                emptyListRequestSupport.text = getString(R.string.text_technical_suppport_title)
                emptyListRequestSupport.setOnClickListener {
                    callsTrackingGA4.logEmptyCallsRequestButtonClick(viewModel.getCurrentSelectedFilterStatus(), requestSupport.name)
                    navigate(requestSupport)
                }
            }
        } else {
            binding.apply {
                callsRequestsNothingYet.text = getString(R.string.my_requests_nothing_yet_filtered_string)
                callsRequestsNothingYetDesc.gone()
                emptyListRequestMaterial.text = getString(R.string.clean_last_filter)
                emptyListRequestMaterial.setOnClickListener {
                    viewModel.clearLastSelectedFilter()
                }
                emptyListRequestMachine.text = getString(R.string.clean_all_filter)
                emptyListRequestMachine.setOnClickListener {
                    viewModel.startFilter(getFirstFilter())
                }
                emptyListRequestSupport.text = getString(R.string.back_to_begin)
                emptyListRequestSupport.setOnClickListener {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        binding.callsRequestsRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState != ZERO){
                    requireActivity().hideSoftKeyboard()
                }
            }
        })
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
            filterName = getString(R.string.request_search),
            filterBottomSheetTitle = null,
            filterPossibilities = listOf(),
            filterType = CieloFilterChipType.SEARCH,
        )
    )
}