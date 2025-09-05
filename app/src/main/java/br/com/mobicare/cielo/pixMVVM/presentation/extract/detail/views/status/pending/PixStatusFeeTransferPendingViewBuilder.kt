package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.pending

import android.view.LayoutInflater
import android.view.View
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

class PixStatusFeeTransferPendingViewBuilder(
    layoutInflater: LayoutInflater,
    data: PixTransferDetail
): PixStatusTransferPendingViewBuilder(layoutInflater, data) {

    override val content = super.content.copy(
        status = getString(R.string.pix_extract_detail_type_fee),
        label = getString(R.string.pix_extract_detail_label_fee_value),
        payerAnswer = null
    )

    override fun build(): View {
        binding.includeContent.apply {
            tvAdditionalInformation.text = getString(R.string.pix_extract_detail_fee_additional_info)
            containerAdditionalInformation.visible()
        }

        return super.build()
    }

}