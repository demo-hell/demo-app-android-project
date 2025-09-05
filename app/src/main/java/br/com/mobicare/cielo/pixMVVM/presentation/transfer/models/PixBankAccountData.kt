package br.com.mobicare.cielo.pixMVVM.presentation.transfer.models

import br.com.mobicare.cielo.pixMVVM.presentation.key.models.PixBankAccountStore

class PixBankAccountData(
    private val bankAccountStore: PixBankAccountStore
) : PixKeyData<PixBankAccountStore> {

    override val data
        get() = bankAccountStore

    override val ownerName
        get() = bankAccountStore.recipientName

    override val documentType
        get() = bankAccountStore.beneficiaryType?.name

    override val formattedDocumentNumber
        get() = bankAccountStore.formattedDocumentNumber

    override val bankName
        get() = bankAccountStore.bank?.shortName

    override val bankBranchNumber
        get() = bankAccountStore.bankBranchNumber

    override val bankAccountNumber
        get() = bankAccountStore.bankAccountNumberWithDigit

    override val bankAccountType
        get() = bankAccountStore.bankAccountType?.nameRes

}