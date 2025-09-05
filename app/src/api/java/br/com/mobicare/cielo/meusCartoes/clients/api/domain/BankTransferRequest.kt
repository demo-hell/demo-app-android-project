package br.com.mobicare.cielo.meusCartoes.clients.api.domain

import android.os.Parcelable
import br.com.mobicare.cielo.commons.utils.SIMPLE_DT_FORMAT_MASK
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class BankTransferRequest(
        @SerializedName("bank") val bankCode: String,
        @SerializedName("branch") var bankBranch: String,
        @SerializedName("accountType") var accountType: String,
        @SerializedName("beneficiaryName") var accountHolderName: String,
        @SerializedName("beneficiaryDoc") var accountHolderDocument: String,
        @SerializedName("account") var bankAccount: String,
        @SerializedName("accountDigit") var bankAccountDigit: String,
        @SerializedName("beneficiaryDocType") var accountHolderType: String,
        @SerializedName("amount") var amount: Double,
        @SerializedName("description") var description: String,
        @SerializedName("branchDigit") var bankBranchDigit: String = "0",
        @Transient var transferDate: String =
                SimpleDateFormat(SIMPLE_DT_FORMAT_MASK, Locale.getDefault()).format(Date()),
        @Transient var bankName: String,
) : Parcelable