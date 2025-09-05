package br.com.mobicare.cielo.chargeback.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Keep
@Parcelize
data class ChargebackDocument(
    val code: Int?,
    val message: String?,
    val fileName: String?,
    val inclusionDate: LocalDate?,
    val file: String?
) : Parcelable
