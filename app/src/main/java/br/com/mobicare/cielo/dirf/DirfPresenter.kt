package br.com.mobicare.cielo.dirf

import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.managers.Result
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.dirf.DirfActivity.Companion.PDF
import br.com.mobicare.cielo.meuCadastroNovo.domain.MCMerchantResponse
import java.net.HttpURLConnection


class DirfPresenter(val mView: DirfContract.View, val mInteractor: DirfContract.Interactor) {

    companion object {
        const val ME = "me"
        const val DIRF = "dirf"
    }

    fun sendDate(value: String) {
        mView.returnDate(value)
    }

    fun initView() {
        mView.initView()
    }

    fun callDirf(
        year: Int,
        cnpj: String,
        companyName: String,
        owner: String,
        cpf: String,
        type: String,
        extension: String = PDF
    ) {
        mInteractor.callDirf(year, cnpj, companyName, owner, cpf, type, object :
            APICallbackDefault<DirfResponse, String> {
            override fun onSuccess(response: DirfResponse) {
                execute(Result.Success(response), extension = extension)
            }

            override fun onError(error: ErrorMessage) {
                verificationStatusError(error)
            }
        })
    }

    fun callDirfPDFOrExcel(year: Int, type: String?, extension: String? = PDF) {
        mInteractor.callDirfPDFOrExcel(year, type, object :
            APICallbackDefault<DirfResponse, String> {
            override fun onSuccess(response: DirfResponse) {
                execute(Result.Success(response), extension = extension)
            }

            override fun onError(error: ErrorMessage) {
                verificationStatusError(error)
            }
        })
    }

    /**
     * method that checks the error status coming from the api and redirects
     * @param error
     * */
    private fun verificationStatusError(
        error: ErrorMessage
    ) {
        when (error.httpStatus) {
            HttpURLConnection.HTTP_BAD_REQUEST -> execute(Result.Error.NotFound(error))
            HttpURLConnection.HTTP_UNAUTHORIZED -> execute(Result.Error.ExpiredSession(error))
            HTTP_ENHANCE -> execute(Result.Error.Enhance(error))
            HttpURLConnection.HTTP_INTERNAL_ERROR -> execute(Result.Error.ServerError(error))
            else -> execute(Result.Error.ServerError(error))
        }
    }

    /**
     * method that shows where the response from the api has to be sent
     * @param result
     * method to show
     * */
    private fun execute(result: Result<Any>, type: String = DIRF, extension: String? = PDF) =
        when (result) {
            is Result.Success -> if (type.equals(DIRF)) mView.showSucesso(
                result.data as DirfResponse,
                extension
            ) else mView.responseME(result.data as MCMerchantResponse)

            is Result.Error.Enhance -> {
                val error = result.error
                mView.erroEnhance(error)
            }
            is Result.Error.BadRequest -> {
                val error = result.error
                mView.erroBadRequest(error)
            }
            else -> mView.serverError()
        }

    fun callApi() {
        mInteractor.callApi(object :
            APICallbackDefault<MCMerchantResponse, String> {
            override fun onSuccess(response: MCMerchantResponse) {
                execute(Result.Success(response), ME)
            }

            override fun onError(error: ErrorMessage) {
                verificationStatusError(error)
            }
        })
    }

    fun onDestroy() {
        mInteractor.cleanDisposable()
    }


}