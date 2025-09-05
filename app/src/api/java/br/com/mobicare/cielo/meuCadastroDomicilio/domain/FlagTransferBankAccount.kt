package br.com.mobicare.cielo.meuCadastroDomicilio.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FlagTransferBankAccount (
      var code : Int,
      var agency : String,
      var agencyDigit : String?,
      var account : String,
      var accountDigit : String?,
      var savingsAccount : Boolean
) : Parcelable