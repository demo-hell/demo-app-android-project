package br.com.mobicare.cielo.pixMVVM.presentation.key.ui.bankAccount

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.databinding.FragmentPixBankAccountSearchBinding
import br.com.mobicare.cielo.databinding.LayoutPixBankAccountKeySearchBinding
import br.com.mobicare.cielo.databinding.LayoutPixBankAccountKeySubtitleBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferBank
import br.com.mobicare.cielo.pixMVVM.presentation.key.adapters.PixTransferBanksAdapter
import br.com.mobicare.cielo.pixMVVM.presentation.key.utils.PixTransferBanksUiState
import br.com.mobicare.cielo.pixMVVM.presentation.key.viewmodel.PixBankAccountKeyViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PixBankAccountSearchFragment : PixBankAccountBaseFragment(), CieloNavigationListener {

    override val viewModel: PixBankAccountKeyViewModel by sharedViewModel()

    private var _binding: FragmentPixBankAccountSearchBinding? = null
    private val binding get() = _binding!!

    private var _bindingSubtitle: LayoutPixBankAccountKeySubtitleBinding? = null

    private var _bindingSearch: LayoutPixBankAccountKeySearchBinding? = null
    private val bindingSearch get() = _bindingSearch!!

    private val banksAdapter = PixTransferBanksAdapter(::onItemTap)

    override val toolbarConfigurator get() = buildCollapsingToolbar(
        layoutMode = CieloCollapsingToolbarLayout.LayoutMode.NOT_SCROLLABLE,
        title = getString(R.string.pix_key_bank_account_title),
        floatingTopSectionView = CieloCollapsingToolbarLayout.FloatingTopSectionView(
            collapsableContentView = _bindingSubtitle?.root,
            fixedContentView = bindingSearch.root
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPixBankAccountSearchBinding.inflate(inflater, container,false).also {
        _binding = it
        _bindingSubtitle = LayoutPixBankAccountKeySubtitleBinding.inflate(inflater, container, false)
        _bindingSearch = LayoutPixBankAccountKeySearchBinding.inflate(inflater, container, false)
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        setupObserver()
        loadTransferBanks()
    }

    override fun onDestroyView() {
        _binding = null
        _bindingSubtitle = null
        _bindingSearch = null
        super.onDestroyView()
    }

    private fun setupRecyclerView() {
        binding.rvBanks.apply {
            adapter = banksAdapter
            addItemDecoration(
                DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
            )
        }
    }

    private fun setupSearchView() {
        bindingSearch.etSearch.setOnSearchRealtime { query ->
            banksAdapter.run {
                filter(query)
                setNotFoundTextVisibility(itemCount == ZERO)
            }
        }
    }

    private fun setupObserver() {
        viewModel.transferBanksUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixTransferBanksUiState.Loading -> handleLoadingState()
                is PixTransferBanksUiState.HideLoading -> setLoadingIndicator(false)
                is PixTransferBanksUiState.Success -> handleSuccessState(state.data)
                is PixTransferBanksUiState.Error -> handleErrorState(state)
                else -> showUnavailableServiceErrorScreen()
            }
        }
    }

    private fun loadTransferBanks() {
        viewModel.run {
            if (isTransferBanksLoaded.not()) getTransferBanks()
        }
    }

    private fun handleLoadingState() {
        setLoadingIndicator(true)
        bindingSearch.etSearch.apply {
            clearText()
            isEnabled = false
        }
    }

    private fun handleSuccessState(banks: List<PixTransferBank>) {
        setLoadingIndicator(false)
        banksAdapter.setItems(banks)
        bindingSearch.etSearch.isEnabled = true
    }

    private fun handleErrorState(state: PixTransferBanksUiState.Error) {
        setLoadingIndicator(false)
        when (state) {
            is PixTransferBanksUiState.UnableToFetchBankListError -> showUnableToFetchBankListErrorScreen()
            is PixTransferBanksUiState.UnavailableServiceError -> showUnavailableServiceErrorScreen()
        }
    }

    private fun onItemTap(bank: PixTransferBank) {
        viewModel.run {
            setSelectedBank(bank)
            if (bankAccount.validateBank) {
                findNavController().navigate(
                    PixBankAccountSearchFragmentDirections
                        .actionPixBankAccountSearchFragmentToPixBankAccountTypeFragment()
                )
            }
        }
    }

    private fun setLoadingIndicator(isVisible: Boolean) {
        binding.apply {
            progressIndicator.visible(isVisible)
            rvBanks.visible(isVisible.not())
            tvNotFound.gone()
        }
    }

    private fun setNotFoundTextVisibility(isVisible: Boolean) {
        binding.apply {
            tvNotFound.visible(isVisible)
            rvBanks.visible(isVisible.not())
        }
    }

    private fun showUnableToFetchBankListErrorScreen() {
        showErrorScreen(
            titleRes = R.string.pix_key_bank_account_error_title,
            messageRes = R.string.pix_key_bank_account_error_message,
            buttonTextRes = R.string.text_error_update,
            onPrimaryButtonClick = {
                it?.dismiss()
                loadTransferBanks()
            }
        )
    }

    private fun showUnavailableServiceErrorScreen() {
        showErrorScreen(
            titleRes = R.string.bs_claim_generic_error_title,
            messageRes = R.string.bs_claim_generic_error_message,
            buttonTextRes = R.string.back,
            onPrimaryButtonClick = { requireActivity().finish() }
        )
    }

    private fun showErrorScreen(
        @StringRes titleRes: Int,
        @StringRes messageRes: Int,
        @StringRes buttonTextRes: Int,
        onPrimaryButtonClick: (Dialog?) -> Unit
    ) {
        HandlerViewBuilderFluiV2.Builder(requireContext()).apply {
            title = getString(titleRes)
            message = getString(messageRes)
            labelPrimaryButton = getString(buttonTextRes)

            onPrimaryButtonClickListener = object : HandlerViewBuilderFluiV2.HandlerViewListener {
                override fun onClick(dialog: Dialog?) {
                    onPrimaryButtonClick(dialog)
                }
            }

            object : HandlerViewBuilderFluiV2.HandlerViewListener {
                override fun onClick(dialog: Dialog?) {
                    requireActivity().finish()
                }
            }.also {
                onBackButtonClickListener = it
                onIconButtonEndHeaderClickListener = it
            }
        }
            .build()
            .show(childFragmentManager, EMPTY)
    }

}