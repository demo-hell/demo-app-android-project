package br.com.mobicare.cielo.pixMVVM.presentation.transfer.factory

import br.com.mobicare.cielo.pixMVVM.domain.model.PixValidateKey
import br.com.mobicare.cielo.pixMVVM.presentation.key.models.PixBankAccountStore
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.models.PixBankAccountData
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.models.PixValidateKeyData

object PixKeyDataFactory {

    private const val EXCEPTION_MESSAGE = "PixKeyData is not allowed to be instantiated without parameters"

    fun create(validateKey: PixValidateKey?, pixBankAccountStore: PixBankAccountStore?) =
        when {
            validateKey != null -> PixValidateKeyData(validateKey)
            pixBankAccountStore != null -> PixBankAccountData(pixBankAccountStore)
            else -> throw IllegalArgumentException(EXCEPTION_MESSAGE)
        }

}