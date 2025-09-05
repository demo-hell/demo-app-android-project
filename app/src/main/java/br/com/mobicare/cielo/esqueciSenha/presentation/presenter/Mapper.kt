package br.com.mobicare.cielo.esqueciSenha.presentation.presenter

import br.com.mobicare.cielo.esqueciSenha.domains.entities.BankListResponse
import br.com.mobicare.cielo.esqueciSenha.domains.entities.BankMaskVO
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BanksSet

object Mapper {

    fun mapper(banks: BanksSet): BankListResponse {
        val bankListResponse = BankListResponse()
        val list = ArrayList<BankMaskVO>()
        banks.forEachIndexed { index, bank ->
            val bankMaskVo = BankMaskVO()
            bankMaskVo.code = bank.code.toString()
            bankMaskVo.name = bank.name.toString()
            list.add(bankMaskVo)
        }
        bankListResponse.banks = list.toTypedArray()
        return bankListResponse
    }
}