package br.com.mobicare.cielo.solesp.model

import android.os.Parcelable
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.solesp.enums.SolespSelectPeriodEnum
import br.com.mobicare.cielo.solesp.enums.SolespSelectTypeEnum
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SolespModel(
    val typeSelected: SolespSelectTypeEnum? = null,
    val periodSelected: SolespSelectPeriodEnum? = null,
    val startDate: DataCustomNew? = null,
    val endDate: DataCustomNew? = null,
    val sendingEmail: String? = null,
    val sendingPhone: String? = null
) : Parcelable