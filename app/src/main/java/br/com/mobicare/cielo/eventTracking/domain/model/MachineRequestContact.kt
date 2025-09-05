package br.com.mobicare.cielo.eventTracking.domain.model

import android.os.Parcelable
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MachineRequestContact(
    val id: String = SIMPLE_LINE,
    val name: String = SIMPLE_LINE,
    val telephone: String = SIMPLE_LINE
) : Parcelable
