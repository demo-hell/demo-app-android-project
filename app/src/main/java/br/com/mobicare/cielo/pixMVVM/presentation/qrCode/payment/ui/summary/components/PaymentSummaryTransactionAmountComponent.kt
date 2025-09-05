package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.ui.summary.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import br.com.cielo.libflue.button.v2.CieloTextButtonCompose
import br.com.cielo.libflue.util.res.CieloTextStyle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.toPtBrRealString

@Composable
fun PaymentSummaryTransactionAmountComponent(
    enabledChangePaymentAmount: Boolean,
    enabledChangeChangeAmount: Boolean,
    paymentAmount: State<Double?>,
    changeAmount: State<Double?>,
    onClickChangePaymentAmount: (String) -> Unit,
    onClickChangeChangeAmount: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.dimen_16dp)),
    ) {
        paymentAmount.value?.let { amount ->
            Text(
                text = stringResource(id = R.string.pix_transfer_review_label_transfer_amount),
                style = CieloTextStyle.mediumMontserrat(fontSize = R.dimen.dimen_14sp, textColor = R.color.neutral_main),
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.dimen_7dp)),
            )

            if (enabledChangePaymentAmount) {
                CieloTextButtonCompose(
                    label = amount.toPtBrRealString(),
                    textStyle = CieloTextStyle.boldMontserrat(fontSize = R.dimen.dimen_32sp),
                    iconEnd = R.drawable.ic_actions_edit_cielo_accent_500_24_dp,
                    onClick = onClickChangePaymentAmount,
                )
            } else {
                Text(
                    text = amount.toPtBrRealString(),
                    style = CieloTextStyle.boldMontserrat(fontSize = R.dimen.dimen_32sp, textColor = R.color.neutral_dark),
                )
            }
        }

        changeAmount.value?.let { amount ->
            Text(
                text = stringResource(id = R.string.pix_transfer_review_label_transfer_change_amount),
                style = CieloTextStyle.mediumMontserrat(fontSize = R.dimen.dimen_14sp, textColor = R.color.neutral_main),
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.dimen_7dp)),
            )

            if (enabledChangeChangeAmount) {
                CieloTextButtonCompose(
                    label = amount.toPtBrRealString(),
                    textStyle = CieloTextStyle.semiBoldMontserrat(fontSize = R.dimen.dimen_16sp),
                    iconEnd = R.drawable.ic_actions_edit_cielo_accent_500_24_dp,
                    onClick = onClickChangeChangeAmount,
                    paddingValues = PaddingValues(top = dimensionResource(id = R.dimen.dimen_1dp)),
                )
            } else {
                Text(
                    text = amount.toPtBrRealString(),
                    style = CieloTextStyle.semiBoldMontserrat(fontSize = R.dimen.dimen_16sp, textColor = R.color.neutral_dark),
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.dimen_7dp)),
                )
            }
        }
    }
}
