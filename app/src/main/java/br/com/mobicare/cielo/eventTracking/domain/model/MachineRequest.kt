package br.com.mobicare.cielo.eventTracking.domain.model

import android.os.Parcelable
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.eventTracking.utils.MachineRequestItem
import kotlinx.android.parcel.Parcelize


@Parcelize
data class MachineRequest(
    val id: String = SIMPLE_LINE,
    val requestMachine: List<Machine?>? = null,
    val requestType: String = SIMPLE_LINE,
    val requestDate: String = SIMPLE_LINE,
    val requestAttendedDate: String = SIMPLE_LINE,
    val requestStatus: EventRequestStatus? = null,
    val requestEstablishment: String = SIMPLE_LINE,
    val requestContact: MachineRequestContact? = null,
    val requestReason: String? = null
) : Parcelable, MachineRequestItem()
