package br.com.mobicare.cielo.openFinance.presentation.manager.sharedData.sent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.RoleWithoutAccessHandler
import br.com.mobicare.cielo.commons.utils.getScreenHeight
import br.com.mobicare.cielo.databinding.OpenFinanceSentFragmentBinding
import br.com.mobicare.cielo.openFinance.presentation.manager.adapter.OpenFinanceSharedDataAdapter
import br.com.mobicare.cielo.openFinance.presentation.manager.sharedData.OpenFinanceSharedDataViewModel
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateConsents
import br.com.mobicare.cielo.openFinance.presentation.utils.UIStateShowFinalList
import br.com.mobicare.cielo.openFinance.presentation.utils.VisibilitySharedData
import org.koin.androidx.viewmodel.ext.android.viewModel

class OpenFinanceSentFragment : BaseFragment(), CieloNavigationListener {
    private var binding: OpenFinanceSentFragmentBinding? = null
    private lateinit var adapterSharedData: OpenFinanceSharedDataAdapter
    private val dataSharedViewModel: OpenFinanceSharedDataViewModel by viewModel()
    private var navigation: CieloNavigation? = null
    private var totalItensSent = ONE_NEGATIVE
    private var oldState = ZERO

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = OpenFinanceSentFragmentBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    private fun setMinimumHeight() {
        binding?.root?.minimumHeight = requireActivity().getScreenHeight()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.apply {
                setNavigationListener(this@OpenFinanceSentFragment)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        observeConsentsSent()
        observeFinalList()
        setMinimumHeight()
    }

    override fun onResume() {
        super.onResume()
        binding?.containerShowFinalListSent.gone()
        dataSharedViewModel.reloadPageSent()
        setRecycler()
    }

    private fun observeConsentsSent() {
        dataSharedViewModel.getConsentsSentLiveData.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is UIStateConsents.Success -> {
                    totalItensSent = uiState.data?.summary?.totalQuantity ?: ONE_NEGATIVE
                    uiState.data?.items?.let {
                        uiStateSuccessConsentsSent()
                        adapterSharedData.update(it)
                    }
                }

                is UIStateConsents.ErrorWithoutAccess -> {
                    RoleWithoutAccessHandler.showNoAccessAlert(requireActivity())
                }

                is UIStateConsents.Error -> {
                    uiStateErrorConsentsSent()
                }

                is UIStateConsents.Loading -> {
                    uiStateLoadingConsentsSent()
                }
            }
        }
    }

    private fun setRecycler() {
        adapterSharedData = OpenFinanceSharedDataAdapter()
        binding?.apply {
            listSent.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = adapterSharedData
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        scrollStateChanged(recyclerView, newState)
                    }

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        scrolled(recyclerView)
                    }
                })
            }
        }
    }

    private fun scrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        binding?.listSent?.post {
            val isScrollingDownAndReachedEnd =
                recyclerView.canScrollVertically(ONE).not() && newState > oldState
            if (isScrollingDownAndReachedEnd) {
                oldState = newState
                dataSharedViewModel.getNextPageSent()
            }
        }
    }

    private fun scrolled(recyclerView: RecyclerView) {
        val layoutManager = LinearLayoutManager::class.java.cast(recyclerView.layoutManager)
        val totalItemCount = layoutManager?.itemCount
        if (totalItemCount == totalItensSent) {
            dataSharedViewModel.checkFinalListSent(true)
        } else {
            dataSharedViewModel.checkFinalListSent(false)
        }
    }

    private fun observeFinalList() {
        dataSharedViewModel.showFinalListSent.observe(viewLifecycleOwner) { uiState ->
            binding?.apply {
                when (uiState) {
                    is UIStateShowFinalList.ShowFinalList -> {
                        containerShowFinalListSent.visible()
                    }

                    is UIStateShowFinalList.HideFinalList -> {
                        containerShowFinalListSent.gone()
                    }
                }
            }
        }
    }

    private fun uiStateSuccessConsentsSent() {
        binding?.apply {
            shimmerIndicatorSent.let { VisibilitySharedData.closeShimmerLoading(it) }
            listSent.let { VisibilitySharedData.showList(it) }
        }
    }

    private fun uiStateErrorConsentsSent() {
        binding?.apply {
            shimmerIndicatorSent.let { VisibilitySharedData.closeShimmerLoading(it) }
            listSent.let { VisibilitySharedData.hideList(it) }
            dontHaveSharedDataSent.let { VisibilitySharedData.showNoDataMessage(it) }
        }
    }

    private fun uiStateLoadingConsentsSent() {
        binding?.apply {
            shimmerIndicatorSent?.let { VisibilitySharedData.showShimmerLoading(it) }
            listSent?.let { VisibilitySharedData.hideList(it) }
            dontHaveSharedDataSent?.let { VisibilitySharedData.hideNoDataMessage(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}