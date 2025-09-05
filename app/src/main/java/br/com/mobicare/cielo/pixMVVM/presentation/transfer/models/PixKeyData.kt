package br.com.mobicare.cielo.pixMVVM.presentation.transfer.models

import androidx.annotation.StringRes

interface PixKeyData<T> {
    val data: T
    val ownerName: String?
    val documentType: String?
    val formattedDocumentNumber: String?
    val bankName: String?
    val bankBranchNumber: String?
    val bankAccountNumber: String?
    @get:StringRes val bankAccountType: Int?
}