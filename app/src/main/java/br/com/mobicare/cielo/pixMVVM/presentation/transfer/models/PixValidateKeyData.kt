package br.com.mobicare.cielo.pixMVVM.presentation.transfer.models

import br.com.mobicare.cielo.pixMVVM.domain.model.PixValidateKey
import br.com.mobicare.cielo.pixMVVM.presentation.key.enums.PixBankAccountType
import br.com.mobicare.cielo.pixMVVM.domain.enums.PixOwnerType

class PixValidateKeyData(private val validateKey: PixValidateKey) : PixKeyData<PixValidateKey> {

    override val data
        get() = validateKey

    override val ownerName
        get() =  validateKey.ownerName

    override val documentType
        get() = PixOwnerType.find(validateKey.ownerType)?.documentType

    override val formattedDocumentNumber
        get() = validateKey.ownerDocument

    override val bankName
        get() = validateKey.participantName

    override val bankBranchNumber
        get() = validateKey.branch

    override val bankAccountNumber
        get() = validateKey.accountNumber

    override val bankAccountType
        get() = PixBankAccountType.findByKey(validateKey.accountType)?.nameRes

}