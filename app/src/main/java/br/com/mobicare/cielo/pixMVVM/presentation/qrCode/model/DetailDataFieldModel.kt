package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.model

import androidx.annotation.StringRes

data class DetailDataFieldModel(
    @StringRes val titleRes: Int? = null,
    val title: String? = null,
    val titleArgs: String? = null,
    @StringRes val valueRes: Int? = null,
    val value: String? = null,
)
