package br.com.mobicare.cielo.meuCadastroDomicilio.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FlagTransferRequest (
        var bankAccount: FlagTransferBankAccount,
        var cardBrands : List<FlagTransferCode>
): Parcelable

