package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.model

import androidx.annotation.StringRes

data class PaymentDetailDataModel(
    @StringRes val titleRes: Int? = null,
    val fields: List<DetailDataFieldModel>,
)
