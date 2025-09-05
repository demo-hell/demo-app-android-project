package br.com.mobicare.cielo.mySales.data.model.params

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class  GetSalesDataParams(
    val accessToken: String,
    val authorization: String,
    val pageSize: Int = TWENTY_FIVE,
    val page: Long? = null,
    val quickFilter: QuickFilter
): Parcelable

