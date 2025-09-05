package br.com.mobicare.cielo.pix.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListKeyType(val list: List<PixKeyType>) : Parcelable


@Parcelize
data class PixKeyType(
    val type: PixKeyTypeEnum,
    val title: String,
    @DrawableRes val image: Int
) : Parcelable
