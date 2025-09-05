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
import br.com.cielo.libflue.button.v2.CieloButtonCompose
import br.com.cielo.libflue.button.v2.CieloButtonType
import br.com.cielo.libflue.button.v2.CieloTextButtonCompose
import br.com.cielo.libflue.card.CieloCardInformationCompose
import br.com.cielo.libflue.enum.CieloCardInformationType
import br.com.cielo.libflue.util.res.CieloTextStyle
import br.com.mobicare.cielo.R

@Composable
fun PaymentSummaryFooterComponent(
    optionalMessage: State<String>,
    isChangeOrWithdrawal: Boolean,
    onClickWriteMessage: (String) -> Unit,
    onClickCancelTransactionButton: (String) -> Unit,
    onClickToPayButton: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.dimen_16dp)),
    ) {
        Text(
            text = stringResource(id = R.string.pix_qr_code_payment_summary_payment_label_optional_message),
            style = CieloTextStyle.mediumMontserrat(fontSize = R.dimen.dimen_14sp, textColor = R.color.neutral_main),
        )

        CieloTextButtonCompose(
            label =
                optionalMessage.value.ifBlank {
                    stringResource(id = R.string.pix_qr_code_payment_summary_payment_label_write_message_button)
                },
            iconEnd = R.drawable.ic_actions_edit_cielo_accent_500_24_dp,
            onClick = onClickWriteMessage,
            paddingValues = PaddingValues(top = dimensionResource(id = R.dimen.dimen_3dp)),
        )

        if (isChangeOrWithdrawal) {
            CieloCardInformationCompose(
                message = stringResource(id = R.string.pix_qr_code_payment_summary_payment_alert_message_change_or_withdrawal),
                type = CieloCardInformationType.INFORMATION,
                paddingValues = PaddingValues(top = dimensionResource(id = R.dimen.dimen_4dp)),
            )
        }

        CieloButtonCompose(
            text = stringResource(id = R.string.pix_qr_code_payment_summary_payment_label_cancel_transaction_button),
            paddingValues =
                PaddingValues(
                    top = dimensionResource(id = R.dimen.dimen_33dp),
                    bottom = dimensionResource(id = R.dimen.dimen_12dp),
                ),
            type = CieloButtonType.OUTLINED,
            color = R.color.red_500,
            textColor = R.color.red_500,
            onClick = onClickCancelTransactionButton,
        )

        CieloButtonCompose(
            text = stringResource(id = R.string.pix_qr_code_payment_summary_payment_label_to_pay_button),
            onClick = onClickToPayButton,
        )
    }
}
