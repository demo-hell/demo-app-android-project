package br.com.mobicare.cielo.posVirtual.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualProductId
import br.com.mobicare.cielo.posVirtual.domain.enum.PosVirtualStatus
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class PosVirtual(
    val status: PosVirtualStatus? = null,
    val merchantId: String? = null,
    val impersonateRequired: Boolean? = null,
    val products: List<PosVirtualProduct>? = null
) : Parcelable

@Keep
@Parcelize
data class PosVirtualProduct(
    val id: PosVirtualProductId? = null,
    val logicalNumber: String? = null,
    val status: PosVirtualStatus? = null
) : Parcelable


