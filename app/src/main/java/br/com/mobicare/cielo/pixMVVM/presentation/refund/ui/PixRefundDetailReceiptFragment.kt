package br.com.mobicare.cielo.pixMVVM.presentation.refund.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.databinding.FragmentPixRefundDetailReceiptBinding
import br.com.mobicare.cielo.pixMVVM.domain.model.PixRefundDetailFull
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler.PixRefundResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.PixRefundViewSelector
import br.com.mobicare.cielo.pixMVVM.presentation.refund.utils.PixRefundDetailUiState
import br.com.mobicare.cielo.pixMVVM.presentation.refund.viewmodel.PixCreateRefundViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PixRefundDetailReceiptFragment : PixRefundBaseFragment() {

    private val viewModel: PixCreateRefundViewModel by sharedViewModel()

    private var _binding: FragmentPixRefundDetailReceiptBinding? = null
    val binding get() = requireNotNull(_binding)

    private val refundResultHandler: PixRefundResultHandler by inject()

    private val refundDetailFull get() = viewModel.refundDetailFull

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentPixRefundDetailReceiptBinding.inflate(inflater, container, false).apply {
        _binding = this
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupGetRefundDetailObserver()
        loadRefundReceipt()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun loadRefundReceipt() {
        refundDetailFull?.let {
            setupReceipt(it)
        }.ifNull {
            viewModel.getRefundDetail()
        }
    }

    private fun setupNavigation() {
        navigation?.apply {
            configureCollapsingToolbar(
                CieloCollapsingToolbarLayout.Configurator(
                    toolbar = CieloCollapsingToolbarLayout.Toolbar(
                        title = getString(R.string.pix_transfer_receipt_title),
                        showBackButton = true,
                        onBackPressed = { navigateToPixHomeExtract(null) }
                    )
                )
            )
            showContent(true)
        }
    }

    private fun setupGetRefundDetailObserver() {
        viewModel.refundDetailUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PixRefundDetailUiState.Loading -> showLoading()
                is PixRefundDetailUiState.Success -> setupReceipt(requireNotNull(refundDetailFull))
                is PixRefundDetailUiState.Error -> showErrorMessageScreen()
            }
        }
    }

    private fun showLoading() {
        navigation?.showAnimatedLoading()
    }

    private fun setupReceipt(receipt: PixRefundDetailFull) {
        navigation?.showContent(true)

        binding.content.addView(
            PixRefundViewSelector(
                inflater = layoutInflater,
                data = receipt,
            ).invoke(
                refundResultHandler(requireNotNull(receipt.refundDetail))
            )
        )
    }

    private fun showErrorMessageScreen() {
        navigation?.hideAnimatedLoading()

        showMessageScreen(
            title = getString(R.string.commons_generic_error_title),
            message = getString(R.string.commons_generic_error_message),
            imageRes = R.drawable.ic_07,
            showCloseIconButton = true,
            primaryButtonText = getString(R.string.text_try_again_label),
            onPrimaryButtonTap = {
                it?.dismiss()
                loadRefundReceipt()
            },
            onDismissTap = ::navigateToPixHomeExtract
        )
    }

}