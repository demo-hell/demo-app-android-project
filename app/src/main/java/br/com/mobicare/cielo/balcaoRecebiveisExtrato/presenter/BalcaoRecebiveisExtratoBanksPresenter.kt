package br.com.mobicare.cielo.balcaoRecebiveisExtrato.presenter

import br.com.mobicare.cielo.balcaoRecebiveisExtrato.BalcaoRecebiveisExtratoBanksContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4Constants.SCREEN_VIEW_RECEIVABLES_NEGOTIATION_CIELO
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4Constants.SCREEN_VIEW_RECEIVABLES_NEGOTIATION_MARKET
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.NegotiationsBanks
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment.BalcaoRecebiveisExtratoFragment.Companion.CIELO
import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.managers.Result
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.toNewErrorMessage
import java.net.HttpURLConnection

class BalcaoRecebiveisExtratoBanksPresenter(
    val mView: BalcaoRecebiveisExtratoBanksContract.View,
    val mInteractor: BalcaoRecebiveisExtratoBanksContract.Interactor
) {

    fun callBanks(initDate: String, finalDate: String, type: String) {
        loadBanks(initDate, finalDate, type,
            preExecute = { mView.initProgressBanks() },
            response = { execute(Result.Success(it), type) },
            finished = { mView.finishedProgressBanks() },
            failure = { verificationStatusError(it, type) }
        )
    }

    private fun loadBanks(
        initDate: String,
        finalDate: String,
        type: String,
        preExecute: () -> Unit,
        response: (NegotiationsBanks) -> Unit,
        finished: () -> Unit,
        failure: (ErrorMessage) -> Unit
    ) {
        preExecute()

        val callbackReturn = object : APICallbackDefault<NegotiationsBanks, String> {
            override fun onSuccess(banks: NegotiationsBanks) {
                response.invoke(banks)
                finished()
            }

            override fun onError(error: ErrorMessage) {
                finished()
                failure.invoke(error)
            }
        }

        mInteractor.getBanks(initDate, finalDate, type, callbackReturn)
    }

    private fun verificationStatusError(
        error: ErrorMessage, type: String?
    ) {
        when (error.httpStatus) {
            HTTP_ENHANCE -> execute(Result.Error.Enhance(error), type)
            HttpURLConnection.HTTP_INTERNAL_ERROR -> execute(Result.Error.ServerError(error), type)
            else -> execute(Result.Error.ServerError(error), type)
        }
    }

    private fun execute(result: Result<Any>, type: String?) = when (result) {
        is Result.Success -> mView.showSuccessBanks(result.data as NegotiationsBanks)
        is Result.Error.ServerError -> {
            trackExceptionEvent(result.error, type)
            mView.serverErrorBanks()
        }
        else -> mView.serverErrorBanks()
    }

    fun onResume(tab: Int?) {
        trackScreenViewEvent(tab)
        mInteractor.resumeDisposable()
    }

    private fun trackScreenViewEvent(tab: Int?) {
        mView.logScreenView(getScreenName(tab = tab))
    }

    private fun trackExceptionEvent(errorMessage: ErrorMessage, type: String?) {
        mView.logException(
            getScreenName(type = type), errorMessage.toNewErrorMessage()
        )
    }

    private fun getScreenName(type: String? = null, tab: Int? = null): String {
        return if (type == CIELO || tab == ZERO) {
            SCREEN_VIEW_RECEIVABLES_NEGOTIATION_CIELO
        } else {
            SCREEN_VIEW_RECEIVABLES_NEGOTIATION_MARKET
        }
    }

    fun onDestroy() {
        mInteractor.cleanDisposable()
    }
}