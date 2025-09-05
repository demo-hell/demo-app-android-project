package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.ui.decode.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_FLOAT

@Composable
fun SquareCameraComponent() {
    Row(
        modifier =
            Modifier
                .height(dimensionResource(id = R.dimen.dimen_272dp))
                .fillMaxWidth(),
    ) {
        Box(
            modifier =
                Modifier
                    .background(color = colorResource(id = R.color.color_1E1E1E_opacity_90))
                    .fillMaxHeight()
                    .weight(ONE_FLOAT),
        )
        Box(
            modifier =
                Modifier
                    .border(
                        dimensionResource(id = R.dimen.dimen_4dp),
                        color = colorResource(id = R.color.white),
                    ).fillMaxHeight()
                    .width(dimensionResource(id = R.dimen.dimen_272dp)),
        )
        Box(
            modifier =
                Modifier
                    .background(color = colorResource(id = R.color.color_1E1E1E_opacity_90))
                    .fillMaxHeight()
                    .weight(ONE_FLOAT),
        )
    }
}
