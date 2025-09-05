package br.com.mobicare.cielo.posVirtual.data.model.request

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal

@Keep
@Parcelize
data class PosVirtualCreateQRCodeRequest(
    val logicalNumber: String? = null,
    val city: String? = null,
    val amount: BigDecimal? = null,
    val phone: String? = null,
    val withdrawChangeData: PosVirtualCreateQRCodeWithDrawChangeData? = null
) : Parcelable
