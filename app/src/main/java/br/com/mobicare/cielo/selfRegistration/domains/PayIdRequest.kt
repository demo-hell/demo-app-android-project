package br.com.mobicare.cielo.selfRegistration.domains

import android.os.Parcelable
import br.com.mobicare.cielo.component.bankData.domanins.BankDataVo
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PayIdRequest ( var merchantId: String,
                          var cardProxy: String?,
                          @SerializedName("bankAccount") var bank: BankDataVo?
) : Parcelable