package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.failed

import android.view.LayoutInflater
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferDetail

class PixStatusFeeTransferFailedViewBuilder(
    layoutInflater: LayoutInflater,
    data: PixTransferDetail
) : PixStatusTransferFailedViewBuilder(layoutInflater, data) {

    override val content = super.content.copy(
        status = getString(R.string.pix_extract_detail_type_fee)
    )

}