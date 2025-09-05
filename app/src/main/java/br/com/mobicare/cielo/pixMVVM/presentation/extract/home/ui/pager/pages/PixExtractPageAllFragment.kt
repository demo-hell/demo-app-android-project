package br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.pages

import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.pixMVVM.domain.model.PixExtract
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.enums.PixReceiptsTab
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.listener.PixHomeExtractListener
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.model.PixExtractHomeArgs
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.PixHomeExtractFragmentDirections
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.PixExtractPageBaseFragment
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.viewModel.PixExtractPageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PixExtractPageAllFragment private constructor() : PixExtractPageBaseFragment() {
    private lateinit var _pixHomeExtractListener: PixHomeExtractListener

    private val _viewModel: PixExtractPageViewModel by viewModel()

    override val viewModel: PixExtractPageViewModel
        get() = _viewModel

    override val pageType: PixReceiptsTab
        get() = PixReceiptsTab.TRANSFER

    override val pixHomeExtractListener: PixHomeExtractListener
        get() = _pixHomeExtractListener

    override fun onItemClick(receipt: Any) {
        if (receipt is PixExtract.PixExtractReceipt) {
            findNavController().safeNavigate(
                PixHomeExtractFragmentDirections
                    .actionPixHomeExtractFragmentToPixNewExtractDetailFragment(
                        false,
                        PixExtractHomeArgs(transactionCode = receipt.transactionCode, idEndToEnd = receipt.idEndToEnd),
                    ),
            )
        }
    }

    companion object {
        fun create(listener: PixHomeExtractListener) =
            PixExtractPageAllFragment().apply {
                _pixHomeExtractListener = listener
            }
    }
}
