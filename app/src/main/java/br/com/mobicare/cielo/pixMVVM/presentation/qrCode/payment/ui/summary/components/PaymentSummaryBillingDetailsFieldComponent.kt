package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.ui.summary.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import br.com.cielo.libflue.util.res.CieloTextStyle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.model.DetailDataFieldModel

@Composable
fun PaymentSummaryBillingDetailsFieldComponent(data: DetailDataFieldModel) {
    Column {
        data.titleRes?.let {
            Text(
                text = stringResource(id = it),
                style = CieloTextStyle.mediumMontserrat(fontSize = R.dimen.dimen_12sp, textColor = R.color.neutral_main),
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.dimen_9dp)),
            )
        }

        data.value?.let {
            Text(
                text = it,
                style = CieloTextStyle.boldMontserrat(textColor = R.color.neutral_main),
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.dimen_12dp))
            )
        }
    }
}
