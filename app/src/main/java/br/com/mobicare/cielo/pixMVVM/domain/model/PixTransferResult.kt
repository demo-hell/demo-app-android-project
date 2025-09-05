package br.com.mobicare.cielo.pixMVVM.domain.model

import android.os.Parcelable
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixTransactionStatus
import kotlinx.android.parcel.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class PixTransferResult(
    val endToEndId: String? = null,
    val transactionCode: String? = null,
    val transactionDate: ZonedDateTime? = null,
    val transactionStatus: PixTransactionStatus? = null,
    val schedulingDate: ZonedDateTime? = null,
    val schedulingCode: String? = null,
) : Parcelable
