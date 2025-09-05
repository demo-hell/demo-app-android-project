package br.com.mobicare.cielo.pix.ui.mylimits.transactions.helpers

import android.widget.TextView
import androidx.fragment.app.FragmentManager
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO_DOUBLE
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.databinding.LayoutPixMyLimitsInformationBinding
import br.com.mobicare.cielo.databinding.LayoutPixMyLimitsTransactionLimitBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.domain.Limits

class LimitTypeViewConfigurator(
    private val limit: Limits,
    private val amountTextView: TextView?,
    private val layoutLimitsInformation: LayoutPixMyLimitsInformationBinding?,
    private val layoutTransactionLimit: LayoutPixMyLimitsTransactionLimitBinding? = null,
    private val fragmentManager: FragmentManager
) {

    operator fun invoke() {
        configureAmountValue()
        configureInformation()
        configureTransactionLimit()
    }

    private fun configureAmountValue() {
        amountTextView?.text = limit.accountLimit?.toPtBrRealString()
    }

    private fun configureInformation() {
        layoutLimitsInformation?.apply {
            limit.requestLimit?.let { limitRequest ->
                if (limitRequest > ZERO_DOUBLE) {
                    amountTextView?.isEnabled = false
                    tvMyLimitsInfo.apply {
                        cardText = resources.getString(R.string.text_pix_my_limit_info, limitRequest.toPtBrRealString())
                        setForegroundColor(R.color.brand_400)
                    }
                    root.visible()
                }
            }
        }
    }

    private fun configureTransactionLimit() {
        layoutTransactionLimit?.apply {
            if (limit.transactionLimit != null) {
                tvMyLimitsTransactionLimitValue.text = limit.transactionLimit.toPtBrRealString()
                tvMyLimitsTransactionLimitLabel.setOnClickListener {
                    CieloDialog.create(
                        title = root.resources.getString(R.string.text_my_limits_available_limit),
                        message = root.resources.getString(R.string.text_my_limits_available_limit_description),
                    )
                        .setTitleTextAppearance(R.style.bold_montserrat_20_cloud_500)
                        .setPrimaryButton(root.resources.getString(R.string.text_close))
                        .show(fragmentManager, EMPTY)
                }
            } else root.gone()
        }
    }

}