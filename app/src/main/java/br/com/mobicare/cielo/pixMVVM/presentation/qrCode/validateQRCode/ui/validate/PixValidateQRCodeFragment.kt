package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.ui.validate

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.screen.HandlerViewBuilderFluiV2
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.DEFAULT_ERROR_MESSAGE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.pixMVVM.domain.model.PixDecodeQRCode
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.enums.PixQRCodeScreenEnum
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixDecodeQRCodeUIState
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.viewModel.PixValidateQRCodeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PixValidateQRCodeFragment : BaseFragment() {
    private val viewModel: PixValidateQRCodeViewModel by sharedViewModel()

    private var navigation: CieloNavigation? = null

    private val toolbarConfigurator
        get() =
            CieloCollapsingToolbarLayout.Configurator(
                layoutMode = CieloCollapsingToolbarLayout.LayoutMode.BLANK,
            )

    private val onClickPrimaryButtonHandlerError =
        object : HandlerViewBuilderFluiV2.HandlerViewListener {
            override fun onClick(dialog: Dialog?) {
                dialog?.dismiss()
                onErrorTryAgain()
            }
        }

    private val onClickBackAndCloseButtonHandlerError =
        object : HandlerViewBuilderFluiV2.HandlerViewListener {
            override fun onClick(dialog: Dialog?) {
                dialog?.dismiss()
                requireActivity().finish()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ComposeView(requireContext())

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupObserver()
        viewModel.validateQRCode()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.also {
                it.showContent(true)
                it.configureCollapsingToolbar(toolbarConfigurator)
            }
        }
    }

    private fun setupObserver() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is PixDecodeQRCodeUIState.ShowLoading -> showLoading()
                is PixDecodeQRCodeUIState.HideLoading -> hideLoading()
                is PixDecodeQRCodeUIState.NavigateToPixQRCodePaymentInsertAmount -> {
                    navigateToPixQRCodePaymentInsertAmount(uiState.pixDecodeQRCode, uiState.isPixTypeChange)
                }
                is PixDecodeQRCodeUIState.NavigateToPixQRCodePaymentSummary -> {
                    navigateToPixQRCodePaymentSummary(uiState.pixDecodeQRCode)
                }
                is PixDecodeQRCodeUIState.GenericError -> showError(uiState.error)
                is PixDecodeQRCodeUIState.CloseActivity -> requireActivity().finish()
                is PixDecodeQRCodeUIState.DoNothing -> Unit
            }
        }
    }

    private fun showLoading() {
        navigation?.showAnimatedLoading()
    }

    private fun hideLoading() {
        navigation?.hideAnimatedLoading()
    }

    private fun onErrorTryAgain() {
        when (viewModel.getScreenOriginDecode()) {
            PixQRCodeScreenEnum.COPY_PASTE -> navigateToPixCopyPasteFragment()
            PixQRCodeScreenEnum.DECODE -> navigateToPixDecodeQRCodeFragment()
        }
    }

    private fun navigateToPixCopyPasteFragment() {
        findNavController().safeNavigate(
            PixValidateQRCodeFragmentDirections.actionPixValidateQRCodeFragmentToPixQRCodeCopyPasteFragment(),
        )
    }

    private fun navigateToPixDecodeQRCodeFragment() {
        findNavController().safeNavigate(
            PixValidateQRCodeFragmentDirections.actionPixValidateQRCodeFragmentToPixQRCodeDecodeFragment(),
        )
    }

    private fun navigateToPixQRCodePaymentInsertAmount(
        qrCode: PixDecodeQRCode,
        isPixTypeChange: Boolean,
    ) {
        findNavController().safeNavigate(
            PixValidateQRCodeFragmentDirections.actionPixValidateQRCodeFragmentToPixQRCodePaymentInsertAmountFragment(
                false,
                isPixTypeChange,
                qrCode,
            ),
        )
    }

    private fun navigateToPixQRCodePaymentSummary(qrCode: PixDecodeQRCode) {
        findNavController().safeNavigate(
            PixValidateQRCodeFragmentDirections.actionPixValidateQRCodeFragmentToPixQRCodePaymentSummaryFragment(
                qrCode,
            ),
        )
    }

    private fun showError(error: NewErrorMessage?) {
        doWhenResumed {
            navigation?.showHandlerViewV2(
                title = getString(R.string.pix_qr_code_validate_bs_generic_error_title),
                message =
                    error?.message.takeIf {
                        it.isNullOrBlank().not() || it != DEFAULT_ERROR_MESSAGE
                    } ?: getString(R.string.pix_qr_code_validate_bs_generic_error_message),
                labelPrimaryButton = getString(R.string.text_try_again_label),
                onPrimaryButtonClickListener = onClickPrimaryButtonHandlerError,
                onBackButtonClickListener = onClickBackAndCloseButtonHandlerError,
                onIconButtonEndHeaderClickListener = onClickBackAndCloseButtonHandlerError,
            )
        }
    }
}
