package br.com.mobicare.cielo.notification.domain

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.constants.ZERO
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class NotificationCountResponse(val totalElements: Int? = ZERO): Parcelable