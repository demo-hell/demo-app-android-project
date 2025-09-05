package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.ui.summary.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import br.com.cielo.libflue.button.v2.CieloTextButtonCompose
import br.com.cielo.libflue.util.res.CieloTextStyle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import br.com.mobicare.cielo.commons.utils.toStringWithTodayCondition
import java.util.Calendar

@Composable
fun PaymentSummaryChangePaymentDateComponent(
    selectedDate: State<Calendar?>,
    isSchedulable: Boolean,
    onClickChangeDatePayment: (String) -> Unit,
) {
    val selectedDateFormatted = selectedDate.value?.toStringWithTodayCondition(SIMPLE_DT_FORMAT_MASK).orEmpty()

    Box(
        modifier =
            Modifier
                .padding(horizontal = dimensionResource(id = R.dimen.dimen_16dp))
                .fillMaxWidth()
                .border(
                    width = dimensionResource(id = R.dimen.dimen_1dp),
                    color = colorResource(id = R.color.border_neutral),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.dimen_12dp)),
                ),
    ) {
        Column(
            modifier =
                Modifier.padding(
                    start = dimensionResource(id = R.dimen.dimen_16dp),
                    end = dimensionResource(id = R.dimen.dimen_16dp),
                    top = dimensionResource(id = R.dimen.dimen_10dp),
                    bottom = dimensionResource(id = R.dimen.dimen_4dp),
                ),
        ) {
            Text(
                text = stringResource(id = R.string.pix_qr_code_payment_summary_payment_payment_date),
                style = CieloTextStyle.mediumMontserrat(fontSize = R.dimen.dimen_14sp, textColor = R.color.neutral_main),
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.dimen_4dp)),
            )

            if (isSchedulable) {
                CieloTextButtonCompose(
                    label = selectedDateFormatted,
                    iconEnd = R.drawable.ic_actions_edit_cielo_accent_500_24_dp,
                    onClick = onClickChangeDatePayment,
                )
            } else {
                Text(
                    text = selectedDateFormatted,
                    style = CieloTextStyle.mediumMontserrat(textColor = R.color.neutral_dark),
                    modifier =
                        Modifier.padding(
                            top = dimensionResource(id = R.dimen.dimen_3dp),
                            bottom = dimensionResource(id = R.dimen.dimen_9dp),
                        ),
                )
            }
        }
    }
}
