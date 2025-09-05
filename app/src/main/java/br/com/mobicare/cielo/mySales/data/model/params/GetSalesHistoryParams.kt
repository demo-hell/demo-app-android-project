package br.com.mobicare.cielo.mySales.data.model.params

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class GetSalesHistoryParams(
    val accessToken: String,
    val authorization: String,
    val quickFilter: QuickFilter,
    val type: String = "DATE"
): Parcelable


