package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.ui.decode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.enums.PixQRCodeScreenEnum
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.viewModel.PixValidateQRCodeViewModel
import br.com.mobicare.cielo.pixMVVM.utils.PixConstants.ERROR_READ_QR_CODE
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PixDecodeQRCodeFragment :
    BaseFragment(),
    CieloNavigationListener {
    private val viewModel: PixValidateQRCodeViewModel by sharedViewModel()

    private var navigation: CieloNavigation? = null

    private val toolbarConfigurator get() =
        CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.BLANK,
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            PixDecodeQRCodeScreen(
                onClickBack = ::onClickBack,
                onClickEnterCode = ::onClickEnterCode,
                onSuccessReadQRCode = ::onSuccessReadQRCode,
                onErrorReadQRCode = ::onErrorReadQRCode,
            )
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
    }

    override fun onResume() {
        super.onResume()
        setupNavigation()
        viewModel.setScreenOriginDecode(PixQRCodeScreenEnum.DECODE)
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
        viewModel.qrCode.observe(viewLifecycleOwner) { qrCode ->
            if (qrCode.isNotEmpty()) navigateToValidateQRCode()
        }
    }

    private fun onClickBack() {
        requireActivity().finish()
    }

    private fun onClickEnterCode(labelButton: String) {
        findNavController().safeNavigate(
            PixDecodeQRCodeFragmentDirections.actionPixDecodeQRCodeFragmentToPixCopyPasteFragment(),
        )
    }

    private fun onSuccessReadQRCode(qrCode: String) {
        viewModel.setQRCode(qrCode)
    }

    private fun onErrorReadQRCode() {
        viewModel.setQRCode(ERROR_READ_QR_CODE)
    }

    private fun navigateToValidateQRCode() {
        findNavController().safeNavigate(
            PixDecodeQRCodeFragmentDirections.actionPixDecodeQRCodeFragmentToPixValidateQRCodeFragment(),
        )
    }
}
