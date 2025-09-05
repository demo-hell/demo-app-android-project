package br.com.mobicare.cielo.arv.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.arv.data.model.response.ArvBankResponse
import br.com.mobicare.cielo.arv.domain.model.ArvBank
import br.com.mobicare.cielo.arv.domain.model.RateSchedules
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ArvConfirmScheduledAnticipationRequest(
    val token: String? = null,
    val rateSchedules: List<RateSchedules?>? = null,
    val domicile: ArvBank? = null
) : Parcelable