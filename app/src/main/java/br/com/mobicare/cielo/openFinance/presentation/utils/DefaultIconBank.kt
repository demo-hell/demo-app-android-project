package br.com.mobicare.cielo.openFinance.presentation.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.presentation.utils.ImageUtils
import br.com.mobicare.cielo.openFinance.utils.OpenFinanceConstants

object DefaultIconBank {
    fun checkTypeImage(logoUrl: String?, ivIconBank: ImageView, context: Context) {
        when {
            logoUrl.isNullOrEmpty() -> ivIconBank.setImageResource(R.drawable.ic_bank_default)

            logoUrl.contains(OpenFinanceConstants.SVG_EXT) -> ImageUtils.loadSvg(
                ivIconBank,
                Uri.parse(logoUrl),
                R.drawable.ic_bank_default,
                R.drawable.ic_bank_default,
                context
            )

            else -> br.com.cielo.libflue.util.imageUtils.ImageUtils.loadImage(
                ivIconBank,
                logoUrl,
                R.drawable.ic_bank_default
            )
        }
    }
}