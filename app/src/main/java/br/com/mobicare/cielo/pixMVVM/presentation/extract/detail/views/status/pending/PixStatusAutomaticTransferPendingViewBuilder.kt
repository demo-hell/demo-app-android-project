package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.pending

import android.view.LayoutInflater
import android.view.View
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

class PixStatusAutomaticTransferPendingViewBuilder(
    layoutInflater: LayoutInflater,
    private val data: PixTransferDetail,
    private val onAccessOriginalTransactionTap: (PixTransferDetail.Credit?) -> Unit
): PixStatusTransferPendingViewBuilder(layoutInflater, data) {

    override val content = super.content.copy(
        payerAnswer = null
    )

    override fun build(): View {
        binding.includeContent.apply {
            tvAdditionalInformation.text = getString(R.string.pix_extract_detail_automatic_transfer_additional_info)
            btnAccessOriginalTransaction.apply {
                setOnClickListener(::onAccessOriginalTransactionClick)
                visible()
            }
            containerAdditionalInformation.visible()
        }

        return super.build()
    }

    private fun onAccessOriginalTransactionClick(v: View) = onAccessOriginalTransactionTap(data.credit)

}