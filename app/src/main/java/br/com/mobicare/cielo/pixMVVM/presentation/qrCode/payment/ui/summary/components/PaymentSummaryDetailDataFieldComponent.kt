package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.ui.summary.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import br.com.cielo.libflue.util.res.CieloTextStyle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.model.DetailDataFieldModel
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixQRCodeComposeUtils

@Composable
fun PaymentSummaryDetailDataFieldComponent(data: DetailDataFieldModel) {
    if (fieldIsVisible(data)) {
        Column(modifier = Modifier.padding(top = dimensionResource(id = R.dimen.dimen_11dp))) {
            Text(
                text =
                    PixQRCodeComposeUtils.getValueStringOrRes(
                        stringValue = data.title,
                        resValue = data.titleRes,
                        resArgs = data.titleArgs,
                    ),
                style =
                    CieloTextStyle
                        .mediumMontserrat(
                            fontSize = R.dimen.dimen_14sp,
                            textColor = R.color.neutral_main,
                        ),
            )
            Text(
                text = PixQRCodeComposeUtils.getValueStringOrRes(stringValue = data.value, resValue = data.valueRes),
                style = CieloTextStyle.semiBoldMontserrat(fontSize = R.dimen.dimen_14sp, textColor = R.color.neutral_dark),
                modifier = Modifier.padding(top = dimensionResource(id = R.dimen.dimen_7dp)),
            )
        }
    }
}

private fun fieldIsVisible(data: DetailDataFieldModel) = data.value.isNullOrEmpty().not() || data.valueRes != null
