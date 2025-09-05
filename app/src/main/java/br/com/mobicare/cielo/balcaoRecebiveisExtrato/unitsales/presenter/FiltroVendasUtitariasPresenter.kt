package br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.presenter

import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.VendasUnitariasFilterBrands
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.FiltroVendasUnitariasContract
import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.managers.Result
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import java.net.HttpURLConnection

class FiltroVendasUtitariasPresenter(
    val mView: FiltroVendasUnitariasContract.View,
    val mInteractor: FiltroVendasUnitariasContract.Interactor
) {


    fun initView() {
        mView.initView()
    }


    fun callBrands(date: String, identificationNumber:String) {

        loadBrands(date, identificationNumber,
            preExecute = { mView.initProgress() },
            response = { execute(Result.Success(it)) },
            finished = { mView.finishedProgress() },
            failure = {verificationStatusError(it)}
        )
    }

    private fun loadBrands(
        date:String,
        identificationNumber:String,
        preExecute: () -> Unit,
        response: (VendasUnitariasFilterBrands) -> Unit,
        finished: () -> Unit,
        failure: (ErrorMessage) -> Unit
    ) {

        preExecute()
        val callbackReturn = object : APICallbackDefault<VendasUnitariasFilterBrands, String> {
            override fun onSuccess(brands: VendasUnitariasFilterBrands) {
                response.invoke(brands)
                finished()
            }

            override fun onError(error: ErrorMessage) {
                finished()
                failure.invoke(error)
            }
        }

        mInteractor.getBrands(
            date,
            identificationNumber,
            callbackReturn)

    }

    private fun verificationStatusError(
        error: ErrorMessage
    ) {
        when (error.httpStatus) {
            HTTP_ENHANCE -> execute(Result.Error.Enhance(error))
            HttpURLConnection.HTTP_INTERNAL_ERROR ->execute(Result.Error.ServerError(error))
            else -> execute(Result.Error.ServerError(error))
        }
    }

    private fun execute(
        result: Result<Any>
    ) = when (result) {
        is Result.Success ->  mView.showSuccess(result.data as VendasUnitariasFilterBrands)
        is Result.Error.ServerError -> mView.serverError()
        else -> mView.serverError()
    }

}