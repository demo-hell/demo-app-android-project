package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.ui.summary.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import br.com.cielo.libflue.button.v2.CieloTextButtonCompose
import br.com.cielo.libflue.util.res.CieloTextStyle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.pixMVVM.domain.model.PixDecodeQRCode
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixQRCodeUtils

@Composable
fun PaymentSummaryBillingDetailsComponent(decodeQRCode: PixDecodeQRCode) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .animateContentSize()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.dimen_16dp),
                ),
    ) {
        CieloTextButtonCompose(
            label = stringResource(id = R.string.pix_qr_code_payment_summary_payment_billing_details_label_button),
            textStyle = CieloTextStyle.mediumMontserrat(fontSize = R.dimen.dimen_14sp),
            contentColor = R.color.neutral_main,
            iconEnd =
                if (expanded) {
                    R.drawable.ic_directions_chevron_up_cloud_200_24_dp
                } else {
                    R.drawable.ic_directions_chevron_down_cloud_200_24_dp
                },
        ) {
            expanded = expanded.not()
        }

        if (expanded) {
            Column {
                PixQRCodeUtils.getPaymentBillingDetail(decodeQRCode).forEach {
                    PaymentSummaryBillingDetailsFieldComponent(it)
                }

                Spacer(modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.dimen_3dp)))
            }
        }
    }
}
