package br.com.mobicare.cielo.mySales.data.model.responses

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.mySales.data.model.CanceledSale
import br.com.mobicare.cielo.mySales.data.model.Pagination
import br.com.mobicare.cielo.mySales.data.model.Summary
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ResultSummaryCanceledSales(
    val summary: Summary? = null,
    val pagination: Pagination? = null,
    var items: MutableList<CanceledSale>? = null
): Parcelable