package br.com.mobicare.cielo.arv.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWENTY_FIVE
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ArvHistoricRequest(
    var negotiationType: String? = null,
    var modalityType: String? = null,
    var status: String? = null,
    var operationNumber: String? = null,
    var initialDate: String? = null,
    var finalDate: String? = null,
    var page: Int = ONE,
    var pageSize: Int = TWENTY_FIVE,
) : Parcelable
