package br.com.mobicare.cielo.eventTracking.domain.model

import android.os.Parcelable
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.eventTracking.utils.CallRequestItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CallRequest(
    val code: String = SIMPLE_LINE,
    val description: String = SIMPLE_LINE,
    val createdDate: String = SIMPLE_LINE,
    val solutionDeadline: Int? = null,
    val referCode: String = SIMPLE_LINE,
    val eventRequestStatus: EventRequestStatus? = null,
    val dependencyCode: String? = null
) : Parcelable, CallRequestItem()