package br.com.mobicare.cielo.suporteTecnico.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.biometricToken.presentation.selfie.BiometricTokenSelfieFragment.Companion.TAG
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.ScreenView.PATH_PROBLEM
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentProblemListBinding
import br.com.mobicare.cielo.databinding.ItemProblemListBinding
import br.com.mobicare.cielo.suporteTecnico.data.EquipmentEligibilityResponse
import br.com.mobicare.cielo.suporteTecnico.data.OpenTicket
import br.com.mobicare.cielo.suporteTecnico.data.Option
import br.com.mobicare.cielo.suporteTecnico.data.ProblemEquipments
import br.com.mobicare.cielo.suporteTecnico.presentation.adapter.ProblemEquipmentsAdapter
import br.com.mobicare.cielo.suporteTecnico.presentation.viewModel.ProblemListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class ProblemListFragment : BaseFragment(), CieloNavigationListener {

    private var _binding: FragmentProblemListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapterProblemEquipments: ProblemEquipmentsAdapter
    private val args: ProblemListFragmentArgs by navArgs()
    private val viewModel: ProblemListViewModel by viewModel()
    private lateinit var openTicket: OpenTicket
    private var navigation: CieloNavigation? = null
    private var issueCode: String = EMPTY
    private var issueDescription: String = EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openTicket = args.requestTicket
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentProblemListBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupObservers()
        onReload()
        GA4.logScreenView(PATH_PROBLEM)
    }

    override fun onResume() {
        super.onResume()
        onReload()
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
            navigation?.showHelpButton(isShow = true)
        }
    }

    private fun setupObservers() {
        viewModel.problemsEquipments.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> onEquipmentShowLoading()
                is UiState.HideLoading -> onEquipmentHideLoading()
                is UiState.Success -> onEquipmentSuccess(state.data)
                else -> onEquipmentError(false)
            }
        }
        viewModel.equipmentsIsEligibility.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> onEquipmentShowLoading()
                is UiState.HideLoading -> onEquipmentHideLoading()
                is UiState.Success -> onEquipmentEligibilitySuccess(state.data)
                else -> onEquipmentError(true)
            }
        }
    }

    private fun onTap(problemEquipments: ProblemEquipments) {
        issueCode = problemEquipments.code
        issueDescription = problemEquipments.description
        problemEquipments.options?.let { listOptions ->
            openBottomSheet(
                listOptions,
                issueDescription,
                issueCode
            )
        }
    }

    private fun onEmptySubProblems() {
        checkEligibility(args.requestTicket.technologyType, issueCode)
    }

    private fun onEquipmentError(isCheckEligibility: Boolean) {
        binding.apply {
            containerProblems.gone()
            progress.root.gone()
            errorInclude.root.visible()
            errorInclude.btnReload.setOnClickListener {
                if (isCheckEligibility)
                    checkEligibility(openTicket.technologyType, issueCode)
                else
                    onReload()
            }
        }
    }

    private fun onEquipmentShowLoading() {
        binding.apply {
            containerProblems.gone()
            progress.root.visible()
            errorInclude.root.gone()
        }
    }

    private fun onEquipmentHideLoading() {
        binding.apply {
            containerProblems.visible()
            progress.root.gone()
            errorInclude.root.gone()
        }
    }

    private fun onEquipmentEligibilitySuccess(equipmentEligibilityResponse: EquipmentEligibilityResponse?) {
        equipmentEligibilityResponse?.let {
            if (it.eligibility) {
                navigateToResumeOpenTicket(
                    args.requestTicket.copy(issueCode = issueCode.toInt()),
                    issueDescription
                )
            } else {
                navigateToCentralOpenTicket()
            }
        }
    }

    private fun onEquipmentSuccess(data: List<ProblemEquipments>?) {
        adapterProblemEquipments = ProblemEquipmentsAdapter(::onTap, ::onEmptySubProblems)
        data?.let { equipments ->
            adapterProblemEquipments.setItems(equipments)
        }

        binding.rvProblemList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterProblemEquipments
        }

        binding.apply {
            containerProblems.visible()
            progress.root.gone()
            errorInclude.root.gone()
        }
    }

    private fun openBottomSheet(subProblems: List<Option>, title: String, issueCode: String) {

        GA4.logClick(PATH_PROBLEM, title)
        GA4.logScreenViewProblem(PATH_PROBLEM, title)
        CieloListBottomSheet
            .create(
                headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                    title = title,
                    titleAppearance = R.style.bold_montserrat_16_cloud_800
                ),
                layoutItemRes = R.layout.item_problem_list,
                data = subProblems,
                onViewBound = { option, _, itemView ->
                    val ItemProblemBinding = ItemProblemListBinding.bind(itemView)
                    ItemProblemBinding.tvTitleItemView.text = option.description
                },
                onItemClicked = { subProblem, _, itemView ->

                    GA4.logClickProblem(PATH_PROBLEM, subProblem.description, title)
                    checkEligibility(args.requestTicket.technologyType, issueCode)
                    itemView.dismiss()
                }
            ).show(childFragmentManager, TAG)
    }

    private fun checkEligibility(technology: String?, code: String) {
        technology?.let { viewModel.getEligibility(it.lowercase(), code) }
    }

    private fun navigateToResumeOpenTicket(openTicket: OpenTicket, problem: String) {
        findNavController().navigate(
            ProblemListFragmentDirections.actionProblemListFragmentToResumeOpenTicketFragment(
                openTicket,
                problem
            )
        )
    }

    private fun navigateToCentralOpenTicket() {
        findNavController().navigate(
            ProblemListFragmentDirections.actionProblemListFragmentToCentralOpenTicketFragment()
        )
    }

    private fun onReload() {
        openTicket.technologyType?.let { viewModel.getProblemEquipments() }
    }
}