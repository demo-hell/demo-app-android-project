package br.com.mobicare.cielo.turboRegistration.presentation.businessSector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ONE_NEGATIVE
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.FragmentLineBusinessBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.turboRegistration.RegistrationUpdateViewModel
import br.com.mobicare.cielo.turboRegistration.analytics.TurboRegistrationAnalytics
import br.com.mobicare.cielo.turboRegistration.domain.model.Mcc
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationResource
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationStepError
import org.koin.androidx.viewmodel.ext.android.viewModel

class BusinessSectorFragment : BaseFragment(), CieloNavigationListener {

    private val binding: FragmentLineBusinessBinding by viewBinding()
    private val viewModel: BusinessSectorViewModel by viewModel()
    private val registrationViewModel: RegistrationUpdateViewModel by activityViewModels()
    private lateinit var businessAdapter: BusinessSectorAdapter
    private var navigation: CieloNavigation? = null
    private var selectedCode: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doWhenResumed {
            TurboRegistrationAnalytics.screenViewSelfRegistrationBusinessSector()
        }
        setupNavigation()
        viewModel.searchBusinessSector()
        selectedCode = registrationViewModel.getBusinessSector()?.code
        binding.btContinue.isButtonEnabled = selectedCode != null
        setupAdapter()
        setListeners()
        addObserver()
    }

    private fun addObserver() {
        viewModel.businessSector.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is RegistrationResource.Loading -> onLoadSearching()
                is RegistrationResource.Success -> onDataReceived(resource.data)
                is RegistrationResource.Error -> onErrorReceived()
                RegistrationResource.Empty -> {}
            }
        }
    }

    private fun onDataReceived(businessSector: List<Mcc>) {
        if (businessSector.isEmpty()) {
            binding.apply {
                rvBusiness.gone()
                viewError.root.gone()
                layoutNotFound.root.visible()
                progressLayout.root.gone()
                cvContainer.visible()
            }
            TurboRegistrationAnalytics.displayContentBusinessSectorWarning()
        } else {
            binding.apply {
                rvBusiness.visible()
                cvContainer.visible()
                businessAdapter.setData(businessSector)
                registrationViewModel.setAddressId(businessSector.firstOrNull()?.idAddress ?: ZERO.toString())
                viewError.root.gone()
                layoutNotFound.root.gone()
                progressLayout.root.gone()
            }
        }

        selectedCode?.let { selectedCode ->
            val selectedIndex = businessSector.indexOfFirst { it.code == selectedCode }
            if (selectedIndex != ONE_NEGATIVE) {
                viewModel.setSelectedBusinessCode(selectedCode)
                businessAdapter.setSelectedItem(selectedIndex)
            }
        }
    }

    private fun onLoadSearching() {
        binding.apply {
            progressLayout.root.visible()
            layoutNotFound.root.gone()
            rvBusiness.gone()
            viewError.root.gone()
            cvContainer.gone()
        }
    }

    private fun onErrorReceived() {
        binding.apply {
            progressLayout.root.gone()
            layoutNotFound.root.gone()
            rvBusiness.gone()
            viewError.root.visible()
            cvContainer.gone()
        }
    }

    private fun setListeners() {
        binding.svBusiness.setOnSearch {
            viewModel.setQuery(it)
            viewModel.searchBusinessSector()
            binding.layoutNotFound.tvTermsNotFound.text = getString(R.string.label_not_found, it)
        }
        binding.svBusiness.setOnClearSearch {
            viewModel.setQuery(EMPTY)
            viewModel.searchBusinessSector()
        }
        binding.btContinue.setOnClickListener {
            findNavController().navigate(BusinessSectorFragmentDirections.actionNavLineBusinessToNavBankData())
        }
        businessAdapter.setAction {
            selectedCode = it.code
            registrationViewModel.setBusinessSector(it.code)
            binding.btContinue.isButtonEnabled = true
        }
        binding.viewError.btRefresh.setOnClickListener {
            viewModel.setQuery(EMPTY)
            viewModel.searchBusinessSector()
        }
    }

    private fun setupAdapter() {
        businessAdapter = BusinessSectorAdapter()
        binding.rvBusiness.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            val itemDecorator = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            addItemDecoration(itemDecorator)
            adapter = businessAdapter
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
            navigation?.setupToolbar(
                title = getString(R.string.title_your_data),
                isCollapsed = false,
                subtitle = getString(R.string.subtitle_line_of_business)
            )
            navigation?.showBackButton(isShow = true)
            navigation?.onStepChanged(RegistrationStepError.BUSINESS_SECTOR.ordinal)
            navigation?.onAdjustSoftInput(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
    }
}