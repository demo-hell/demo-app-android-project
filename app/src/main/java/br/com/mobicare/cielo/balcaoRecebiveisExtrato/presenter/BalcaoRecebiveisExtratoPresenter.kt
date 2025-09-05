package br.com.mobicare.cielo.balcaoRecebiveisExtrato.presenter

import br.com.mobicare.cielo.balcaoRecebiveisExtrato.BalcaoRecebiveisExtratoContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4Constants.SCREEN_VIEW_RECEIVABLES_NEGOTIATION_CIELO_SEE_MORE
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4Constants.SCREEN_VIEW_RECEIVABLES_NEGOTIATION_MARKET_SEE_MORE
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Negotiations
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.fragment.BalcaoRecebiveisExtratoFragment.Companion.CIELO
import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.managers.Result
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.toNewErrorMessage
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import java.net.HttpURLConnection

/**
 * create by Enzo Teles
 * Jan 19, 2021
 * */
class BalcaoRecebiveisExtratoPresenter(
    val mView: BalcaoRecebiveisExtratoContract.View,
    val mInteractor: BalcaoRecebiveisExtratoContract.Interactor
) {

    private val PAGE_SIZE = 25

    /**
     * method to init views
     * */
    fun initView() {
        mView.initView()
    }

    /**
     * method to call api negotiations
     * */
    fun callApi(initDate: String?, finalDate: String?, type: String? = null) {

        loadNegotiations(initDate, finalDate, type,
            preExecute = { mView.initProgress() },
            response = { execute(Result.Success(it), type) },
            finished = { mView.finishedProgress() },
            failure = { verificationStatusError(it, type) }
        )
    }


    /**
     * method to call api negotiations with filter or withoud filter
     * */
    fun callExtratoDetail(
        page: Int = 1, initDate: String?, finalDate: String?, type: String? = null,
        quickFilter: QuickFilter? = null, showLoadingMore: Boolean = false
    ) {

        loadNegotiationsExtratoDetail(page, PAGE_SIZE, initDate, finalDate, type, quickFilter,
            preExecute = { if (showLoadingMore) mView.showProgressMore() else mView.initProgress() },
            response = { execute(Result.Success(it), type) },
            finished = { if (showLoadingMore) mView.finishedProgressMore() else mView.finishedProgress() },
            failure = { verificationStatusError(it, type) }
        )
    }

    /**
     * method to load negotiations with filter or withoud filter
     * @param initDate
     * @param preExecute
     * @param response
     * @param finished
     * @param failure
     * @param quickFilter
     * */
    private fun loadNegotiationsExtratoDetail(
        page: Int,
        pageSize: Int,
        initDate: String?,
        finalDate: String?,
        type: String? = null,
        quickFilter: QuickFilter? = null,
        preExecute: () -> Unit,
        response: (Negotiations) -> Unit,
        finished: () -> Unit,
        failure: (ErrorMessage) -> Unit
    ) {

        preExecute()
        initDate?.let { inDate ->
            finalDate?.let { fnDate ->

                val callbackReturn = object : APICallbackDefault<Negotiations, String> {
                    override fun onSuccess(negotiation: Negotiations) {
                        response.invoke(negotiation)
                        finished()
                    }

                    override fun onError(error: ErrorMessage) {
                        finished()
                        failure.invoke(error)
                    }
                }

                mInteractor.getNegotiationsType(
                    page,
                    pageSize,
                    inDate,
                    fnDate,
                    type!!,
                    quickFilter,
                    callbackReturn
                )
            }
        }

    }


    /**
     * method to load all negotiations of the api
     * @param initDate
     * @param preExecute
     * @param response
     * @param finished
     * @param failure
     * */
    private fun loadNegotiations(
        initDate: String?,
        finalDate: String?,
        type: String? = null,
        preExecute: () -> Unit,
        response: (Negotiations) -> Unit,
        finished: () -> Unit,
        failure: (ErrorMessage) -> Unit
    ) {

        preExecute()
        initDate?.let { inDate ->
            finalDate?.let { fnDate ->

                val callbackReturn = object : APICallbackDefault<Negotiations, String> {
                    override fun onSuccess(negotiation: Negotiations) {
                        response.invoke(negotiation)
                        finished()
                    }

                    override fun onError(error: ErrorMessage) {
                        finished()
                        failure.invoke(error)
                    }
                }
                mInteractor.getNegotiations(
                    inDate,
                    fnDate,
                    callbackReturn
                )
            }
        }
    }


    /**
     * method that checks the error status coming from the api and redirects
     * @param error
     * */
    private fun verificationStatusError(
        error: ErrorMessage,
        type: String?
    ) {
        when (error.httpStatus) {
            HTTP_ENHANCE -> execute(Result.Error.Enhance(error), type)
            HttpURLConnection.HTTP_INTERNAL_ERROR -> execute(Result.Error.ServerError(error), type)
            else -> execute(Result.Error.ServerError(error), type)
        }
    }

    /**
     * method that shows where the response from the api has to be sent
     * @param result
     * method to show
     * */
    private fun execute(result: Result<Any>, type: String?) = when (result) {
        is Result.Success -> mView.showSuccess(result.data as Negotiations)
        is Result.Error.ServerError -> {
            trackExceptionEvent(result.error, type)
            mView.serverError()
        }

        else -> mView.serverError()
    }

    fun trackScreenViewEvent(type: String?) {
        mView.logScreenView(getScreenName(type))
    }

    private fun trackExceptionEvent(errorMessage: ErrorMessage, type: String?) {
        mView.logException(
            getScreenName(type = type), errorMessage.toNewErrorMessage()
        )
    }

    private fun getScreenName(type: String?): String {
        return when (type) {
            CIELO -> SCREEN_VIEW_RECEIVABLES_NEGOTIATION_CIELO_SEE_MORE
            else -> SCREEN_VIEW_RECEIVABLES_NEGOTIATION_MARKET_SEE_MORE
        }
    }

    fun onResume() {
        mInteractor.resumeDisposable()
    }

    fun onDestroy() {
        mInteractor.cleanDisposable()
    }
}