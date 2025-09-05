package br.com.mobicare.cielo.pixMVVM.presentation.home.models

import androidx.annotation.DrawableRes

enum class PixTransactionButtonId {
    TRANSFER, READ_QR_CODE, GENERATE_CHARGE, COPY_AND_PASTE
}

data class PixTransactionButton(
    val id: PixTransactionButtonId,
    val title: String,
    @DrawableRes val image: Int,
    val contentDescription: String
)
