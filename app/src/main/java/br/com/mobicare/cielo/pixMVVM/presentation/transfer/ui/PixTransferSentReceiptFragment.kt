package br.com.mobicare.cielo.pixMVVM.presentation.transfer.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentPixTransferSentReceiptBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.toHomePix
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.receipt.sent.PixReceiptTransferSentViewBuilder
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.utils.PixTransferReceiptUiState
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.viewmodel.PixTransferViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PixTransferSentReceiptFragment : BaseFragment() {

    private val viewModel: PixTransferViewModel by sharedViewModel()

    private var _binding: FragmentPixTransferSentReceiptBinding? = null
    val binding get() = _binding!!

    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPixTransferSentReceiptBinding.inflate(inflater, container, false).apply {
        _binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupObserver()
        loadTransferDetail()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun loadTransferDetail() = viewModel.getTransferDetails()

    private fun setupNavigation() {
        navigation = (requireActivity() as? CieloNavigation)?.also {
            it.configureCollapsingToolbar(
                CieloCollapsingToolbarLayout.Configurator(
                    toolbar = CieloCollapsingToolbarLayout.Toolbar(
                        title = getString(R.string.pix_transfer_receipt_title),
                        showBackButton = false,
                        menu = CieloCollapsingToolbarLayout.ToolbarMenu(
                            menuRes = R.menu.menu_common_close_blue,
                            onOptionsItemSelected = { item ->
                                if (item.itemId == R.id.action_close) finish()
                            }
                        )
                    )
                )
            )
        }
    }

    private fun setupObserver() {
        viewModel.receiptState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixTransferReceiptUiState.Loading -> handleLoadingState()
                is PixTransferReceiptUiState.Success -> handleSuccessState(state.result)
                is PixTransferReceiptUiState.Error -> handleErrorState()
            }
        }
    }

    private fun handleLoadingState() {
        navigation?.showAnimatedLoading()
    }

    private fun handleSuccessState(result: PixTransferDetail) {
        navigation?.run {
            hideAnimatedLoading()
            binding.content.addView(
                PixReceiptTransferSentViewBuilder(layoutInflater, result).build()
            )
        }
    }

    private fun handleErrorState() {
        doWhenResumed {
            navigation?.run {
                hideAnimatedLoading()
                showCustomHandlerView(
                    title =  getString(R.string.commons_generic_error_title),
                    message = getString(R.string.commons_generic_error_message),
                    contentImage = R.drawable.ic_07,
                    isShowFirstButton = true,
                    isShowButtonClose = true,
                    isShowSecondButton = false,
                    labelFirstButton = getString(R.string.text_try_again_label),
                    callbackFirstButton = { loadTransferDetail() },
                    callbackBack = ::finish,
                    callbackClose = ::finish,
                )
            }
        }
    }

    private fun finish() = requireActivity().toHomePix()

}