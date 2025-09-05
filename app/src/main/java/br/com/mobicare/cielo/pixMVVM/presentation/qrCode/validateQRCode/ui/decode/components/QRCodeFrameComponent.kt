package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.ui.decode.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import br.com.cielo.libflue.button.v2.CieloButtonCompose
import br.com.cielo.libflue.util.res.CieloTextStyle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_FLOAT

@Composable
fun QRCodeFrameComponent(
    onClickBack: () -> Unit,
    onClickEnterCode: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        ToolbarDecodeQRCodeScreenComponent(onClickBack)

        Spacer(
            modifier =
                Modifier
                    .height(
                        dimensionResource(id = R.dimen.dimen_60dp),
                    ).fillMaxWidth()
                    .background(color = colorResource(id = R.color.color_1E1E1E_opacity_90)),
        )

        SquareCameraComponent()

        Column(
            modifier =
                Modifier
                    .weight(ONE_FLOAT)
                    .background(color = colorResource(id = R.color.color_1E1E1E_opacity_90)),
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(modifier = Modifier.weight(ONE_FLOAT))
            Text(
                text = stringResource(id = R.string.pix_qr_code_decode_help_message),
                style = CieloTextStyle.mediumMontserrat(fontSize = R.dimen.dimen_16sp, textColor = R.color.white),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.dimen_70dp)),
            )
            Spacer(modifier = Modifier.weight(ONE_FLOAT))
            CieloButtonCompose(
                text = stringResource(id = R.string.pix_qr_code_decode_label_button_enter_code),
                paddingValues =
                    PaddingValues(
                        start = dimensionResource(id = R.dimen.dimen_16dp),
                        end = dimensionResource(id = R.dimen.dimen_16dp),
                        bottom = dimensionResource(id = R.dimen.dimen_50dp),
                    ),
                onClick = onClickEnterCode,
            )
        }
    }
}
