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

class PixExtractPageSchedulingFragment private constructor() : PixExtractPageBaseFragment() {
    private lateinit var _pixHomeExtractListener: PixHomeExtractListener

    private val _viewModel: PixExtractPageViewModel by viewModel()

    override val viewModel: PixExtractPageViewModel
        get() = _viewModel

    override val pageType: PixReceiptsTab
        get() = PixReceiptsTab.SCHEDULES

    override val pixHomeExtractListener: PixHomeExtractListener
        get() = _pixHomeExtractListener

    override fun onItemClick(receipt: Any) {
        if (receipt is PixExtract.PixExtractReceipt) {
            findNavController().safeNavigate(
                PixHomeExtractFragmentDirections
                    .actionPixHomeExtractFragmentToPixNewExtractDetailFragment(
                        false,
                        PixExtractHomeArgs(schedulingCode = receipt.schedulingCode),
                    ),
            )
        }
    }

    companion object {
        fun create(listener: PixHomeExtractListener) =
            PixExtractPageSchedulingFragment().apply {
                _pixHomeExtractListener = listener
            }
    }
}
