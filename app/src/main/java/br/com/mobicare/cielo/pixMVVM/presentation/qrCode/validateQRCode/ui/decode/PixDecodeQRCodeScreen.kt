package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.ui.decode

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.ui.decode.components.OpenDecodeQRCodePermissionComponent
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.ui.decode.components.QRCodeFrameComponent

@Composable
fun PixDecodeQRCodeScreen(
    onClickBack: () -> Unit,
    onSuccessReadQRCode: (String) -> Unit,
    onErrorReadQRCode: () -> Unit,
    onClickEnterCode: (String) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        OpenDecodeQRCodePermissionComponent(
            onSuccess = onSuccessReadQRCode,
            onError = onErrorReadQRCode,
        )

        QRCodeFrameComponent(
            onClickBack = onClickBack,
            onClickEnterCode = onClickEnterCode,
        )
    }
}
