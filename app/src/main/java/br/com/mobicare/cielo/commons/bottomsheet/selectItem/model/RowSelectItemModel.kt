package br.com.mobicare.cielo.commons.bottomsheet.selectItem.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class RowSelectItemModel(
    @StringRes val label: Int,
    val onClick: () -> Unit,
    @DrawableRes val iconStart: Int? = null,
    @DrawableRes val iconEnd: Int? = null
)
