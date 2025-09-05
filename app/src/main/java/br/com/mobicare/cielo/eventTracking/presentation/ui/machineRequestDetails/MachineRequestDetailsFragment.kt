package br.com.mobicare.cielo.eventTracking.presentation.ui.machineRequestDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import br.com.cielo.libflue.util.ONE
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.router.Router
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.normalizeToLowerSnakeCase
import br.com.mobicare.cielo.commons.utils.recycler.CircleIndicatorItemDecoration
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.MachineRequestDetailsFragmentBinding
import br.com.mobicare.cielo.eventTracking.domain.model.CieloFilterType.DESINSTALACAO
import br.com.mobicare.cielo.eventTracking.domain.model.EventRequestStatus
import br.com.mobicare.cielo.eventTracking.utils.MenuTranslator
import br.com.mobicare.cielo.extensions.capitalizeWords
import br.com.mobicare.cielo.extensions.formatStringToPhone
import br.com.mobicare.cielo.extensions.ifNullSimpleLine
import br.com.mobicare.cielo.main.domain.Menu

class MachineRequestDetailsFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null

    private val binding: MachineRequestDetailsFragmentBinding by viewBinding()

    private val args: MachineRequestDetailsFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.setupToolbar(
                title = getString(R.string.machine_request_details_title),
                isCollapsed = false,
                subtitle = null
            )
            navigation?.showBackButton(isShow = true)
            navigation?.showHelpButton(isShow = false)
        }
        setupListeners()

        initScreen()
    }

    private fun initScreen() {
        args.machineRequestItem?.also { machineRequest ->
            binding.apply {

                machineRecyclerEquipments.apply {
                    LinearSnapHelper().attachToRecyclerView(this)
                    layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    adapter = MachineDetailsAdapter(
                        machineRequest.requestType.lowercase().replaceFirstChar { it.uppercase() },
                        machineRequest.requestMachine
                    )
                    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            machineRecyclerEquipments.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            if ((machineRequest.requestMachine?.size ?: ZERO) > ONE) {
                                if (machineRecyclerEquipments.height > ZERO
                                    && machineRecyclerEquipments.getChildAt(ZERO) != null
                                    && machineRecyclerEquipments.getChildAt(ZERO).height > ZERO
                                ) {
                                    machineRecyclerEquipments.addItemDecoration(CircleIndicatorItemDecoration(requireContext()))
                                }
                            }
                        }
                    })
                }

                itemMachineSolicitationDateValue.text = machineRequest.requestDate
                machineDetailEstimatedDateValue.text = machineRequest.requestAttendedDate
                machineDetailEstablishmentValue.text = machineRequest.requestEstablishment
                machineDetailContactDataValue.text = getString(
                    R.string.machine_detail_contact,
                    machineRequest.requestContact?.name?.capitalizeWords().ifNullSimpleLine(),
                    machineRequest.requestContact?.telephone?.formatStringToPhone().ifNullSimpleLine()
                )
                machineDetailModeValue.text =
                    machineRequest.requestMachine?.firstOrNull()?.modality?.lowercase()?.replaceFirstChar { it.uppercase() }.ifNullSimpleLine()
                machineClNewEquipment.gone()

                machineDetailExplain.gone()
                machineRequest.requestStatus?.let { machineRequestStatus ->
                    machineDetailStatusValue.text = getString(machineRequestStatus.statusText)
                    machineDetailIconStatus.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(),
                            machineRequestStatus.statusIcon
                        )
                    )

                    when (machineRequestStatus) {
                        EventRequestStatus.ATTENDED -> {
                            machineDetailEstimatedDate.text = getString(R.string.attended_date)
                            machineDetailEstimatedDateValue.text = machineRequest.requestAttendedDate
                            when (machineRequest.requestType.normalizeToLowerSnakeCase()) {
                                DESINSTALACAO.normalizeToLowerSnakeCase() -> {
                                    machineDetailExplain.visible()
                                    machineDetailExplain.text = getString(R.string.five_days_to_remove)
                                }

                                else -> {
                                    machineDetailExplain.gone()
                                }
                            }
                        }

                        EventRequestStatus.UNREALIZED -> {
                            machineDetailEstimatedDate.gone()
                            machineDetailEstimatedDateValue.gone()
                            if (!machineRequest.requestReason.isNullOrEmpty()) {
                                machineDetailStatusDesc.visible()
                                machineDetailStatusDesc.text = machineRequest.requestReason
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun setupListeners() {

        val requestSupport = MenuTranslator(
            code = Router.APP_ANDROID_HELP_DESK,
            name = getString(R.string.text_technical_suppport_title)
        )

        binding.apply {
            emptyListRequestSupport.setOnClickListener {
                navigate(requestSupport)
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
}