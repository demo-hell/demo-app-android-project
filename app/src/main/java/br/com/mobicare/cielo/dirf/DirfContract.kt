package br.com.mobicare.cielo.dirf

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.meuCadastroNovo.domain.MCMerchantResponse

interface DirfContract {

    interface View {
        fun returnDate(value: String)
        fun initView()
        fun showSucesso(
            dirfResponse: DirfResponse,
            extension: String?
        )
        fun serverError()
        fun erroEnhance(
            error: ErrorMessage?
        )
        fun erroBadRequest(
            error: ErrorMessage?
        )
        fun verificationPJ()
        fun responseME(meResponse: MCMerchantResponse)
    }

    interface Interactor {
        fun callDirf(year: Int, cnpj: String, companyName: String, owner: String, cpf: String, type: String, callBack: APICallbackDefault<DirfResponse, String>)
        fun callDirfPDFOrExcel(year: Int, type: String?, callBack: APICallbackDefault<DirfResponse, String>)
        fun cleanDisposable()
        fun callApi(apiCallbackDefault: APICallbackDefault<MCMerchantResponse, String>)
    }
}