package br.com.mobicare.cielo.mySales.data.model.bo

import android.os.Parcelable
import br.com.mobicare.cielo.mySales.data.model.Pagination
import br.com.mobicare.cielo.mySales.data.model.Sale
import br.com.mobicare.cielo.mySales.data.model.Summary
import com.google.errorprone.annotations.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class SummarySalesBO(
    val summary: Summary,
    val pagination: Pagination?,
    val items: List<Sale>
): Parcelable
