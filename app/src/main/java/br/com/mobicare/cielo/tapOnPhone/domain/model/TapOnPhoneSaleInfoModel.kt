package br.com.mobicare.cielo.tapOnPhone.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.tapOnPhone.constants.BRAZIL_MONEY_CURRENCY
import com.symbiotic.taponphone.Enums.AidType
import com.symbiotic.taponphone.Enums.TransactionType
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class TapOnPhoneSaleInfoModel(
    val aidType: AidType?,
    val transactionValue: Long,
    val currency: Short = BRAZIL_MONEY_CURRENCY,
    val transactionType: TransactionType?,
    val extendedTransactionData: HashMap<String, String>? = null,
    val additionalData: String? = EMPTY,
    val transactionApprovedAction: (TransactionReceiptData?) -> Unit,
    val transactionCancelled: () -> Unit,
    val transactionTimeExpired: () -> Unit
) : Parcelable