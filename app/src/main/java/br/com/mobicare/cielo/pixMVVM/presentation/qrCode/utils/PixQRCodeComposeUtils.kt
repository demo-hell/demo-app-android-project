package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING

object PixQRCodeComposeUtils {
    @Composable
    fun getValueStringOrRes(
        stringValue: String?,
        @StringRes resValue: Int?,
        resArgs: String? = null,
    ) = when {
        resArgs != null && resValue != null -> stringResource(id = resValue, resArgs)
        stringValue != null -> stringValue
        resValue != null -> stringResource(id = resValue)
        else -> EMPTY_STRING
    }
}
