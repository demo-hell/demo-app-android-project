package br.com.mobicare.cielo.pixMVVM.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import java.time.ZonedDateTime

@Keep
@Parcelize
data class PixAccountBalance(
    val currentBalance: Double? = null,
    val timeOfRequest: ZonedDateTime? = null
): Parcelable