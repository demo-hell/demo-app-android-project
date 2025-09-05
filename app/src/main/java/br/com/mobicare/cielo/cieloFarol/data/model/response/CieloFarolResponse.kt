package br.com.mobicare.cielo.cieloFarol.data.model.response

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.pix.constants.EMPTY
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class CieloFarolResponse(
        val bestDayOfWeek: String? = EMPTY,
        val bestTime: String? = EMPTY,
        val averageTicketAmount: String? = EMPTY,
        val insightText: String? = EMPTY
): Parcelable

@Keep
@Parcelize
data class CieloFarolDetail(
        val title: String? = EMPTY,
        val description: String? = EMPTY
): Parcelable