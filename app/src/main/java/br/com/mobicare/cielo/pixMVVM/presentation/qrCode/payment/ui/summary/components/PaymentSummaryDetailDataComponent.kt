package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.ui.summary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import br.com.cielo.libflue.util.res.CieloTextStyle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.pixMVVM.domain.model.PixDecodeQRCode
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixQRCodeUtils

@Composable
fun PaymentSummaryDetailDataComponent(pixDecodeQRCode: PixDecodeQRCode) {
    val paymentDetailsData = PixQRCodeUtils.getPaymentDetailsData(pixDecodeQRCode)

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
                    bottom = dimensionResource(id = R.dimen.dimen_13dp),
                ),
        ) {
            paymentDetailsData.forEachIndexed { index, data ->
                Text(
                    text = data.titleRes?.let { stringResource(id = it) }.orEmpty(),
                    style = CieloTextStyle.boldMontserrat(fontSize = R.dimen.dimen_14sp, textColor = R.color.neutral_dark),
                    modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.dimen_6dp)),
                )

                data.fields.forEach {
                    PaymentSummaryDetailDataFieldComponent(it)
                }

                if (index < paymentDetailsData.size - ONE) {
                    Spacer(
                        modifier =
                            Modifier
                                .padding(
                                    top = dimensionResource(id = R.dimen.dimen_15dp),
                                    bottom = dimensionResource(id = R.dimen.dimen_18dp),
                                ).height(dimensionResource(id = R.dimen.dimen_1dp))
                                .fillMaxWidth()
                                .background(colorResource(id = R.color.border_neutral)),
                    )
                }
            }
        }
    }
}
