package br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.adapter

import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.helpers.BrandCardHelper
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.databinding.LayoutPixMyLimitsTrustedDestinationsItemBinding
import br.com.mobicare.cielo.pix.domain.PixTrustedDestinationResponse
import br.com.mobicare.cielo.pix.ui.extract.account.management.PATH_DEFAULT
import br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.PixMyLimitsTrustedDestinationsContract

class PixMyLimitsTrustedDestinationsGroupHolder(val view: LayoutPixMyLimitsTrustedDestinationsItemBinding) :
    RecyclerView.ViewHolder(view.root) {

    fun bind(
        trustedDestination: PixTrustedDestinationResponse,
        listener: PixMyLimitsTrustedDestinationsContract.View
    ) {
        view.container.setOnClickListener {
            listener.onShowDetails(trustedDestination)
        }

        view.tvTitleContactItem.text = trustedDestination.name
        view.tvDocumentContactItem.text = trustedDestination.nationalRegistration

        BrandCardHelper.getLoadBrandImageGeneric(
            trustedDestination.bankCode ?: PATH_DEFAULT
        )
            .let { itUrl ->
                ImageUtils.loadImage(
                    view.ivBrandItem,
                    itUrl,
                    R.drawable.bank_000
                )
            }
    }
}