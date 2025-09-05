package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.ui.decode.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import br.com.cielo.libflue.util.res.CieloTextStyle
import br.com.mobicare.cielo.R

@Composable
fun ToolbarDecodeQRCodeScreenComponent(onClickBack: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(dimensionResource(id = R.dimen.dimen_56dp))
                .background(
                    color = colorResource(id = R.color.color_1E1E1E_opacity_90),
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onClickBack,
            modifier =
                Modifier
                    .padding(horizontal = dimensionResource(id = R.dimen.dimen_4dp))
                    .size(dimensionResource(id = R.dimen.dimen_48dp)),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left_18_16_brand_400),
                contentDescription = stringResource(id = R.string.back),
                tint = Color.White,
            )
        }

        Text(
            text = stringResource(id = R.string.pix_home_transaction_read_qr_code),
            style = CieloTextStyle.boldMontserrat(fontSize = R.dimen.dimen_20sp, textColor = R.color.white),
        )
    }
}
