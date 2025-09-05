package br.com.mobicare.cielo.notification.domain

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.pix.constants.EMPTY
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class NotificationResponse(
    val notifications: List<NotificationItem>? = null,
    val pages: Int? = ZERO,
    val totalElements: Int? = ZERO
): Parcelable

@Parcelize
@Keep
data class Merchant(
    val merchantId: String? = EMPTY,
    val notificationId: String? = EMPTY,
    val read: Boolean,
    val readDate: String? = EMPTY
): Parcelable

@Parcelize
@Keep
data class NotificationItem(
    val campaignId: String? = EMPTY,
    val createdDate: String? = EMPTY,
    val description: String? = EMPTY,
    val enabled: Boolean,
    val id: String? = EMPTY,
    val merchants: List<Merchant>? = null,
    val title: String? = EMPTY
): Parcelable