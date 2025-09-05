package br.com.mobicare.cielo.chargeback.data.model.response


import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.constants.TEN
import br.com.mobicare.cielo.commons.constants.ZERO
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ChargebacksResponse(
    val content: List<ChargebackResponse>,
    val totalElements: Int = ZERO,
    val firstPage: Boolean = true,
    val lastPage: Boolean = false,
    val totalPages: Int = ZERO,
    val pageSize: Int = TEN,
    val pageNumber: Int = ZERO,
    val numberOfElements: Int = ZERO,
    val empty: Boolean = true,
): Parcelable