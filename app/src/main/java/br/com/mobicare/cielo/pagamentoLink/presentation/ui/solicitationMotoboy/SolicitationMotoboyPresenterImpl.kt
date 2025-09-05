package br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy

import android.app.Dialog
import android.os.Handler
import br.com.mobicare.cielo.commons.constants.*
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.managers.Result
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.pagamentoLink.orders.repository.LinkOrdersInteractor
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import java.net.HttpURLConnection

class SolicitationMotoboyPresenterImpl(
    private val view: SolicitationMotoboyView,
    private val interactor: LinkOrdersInteractor
) : SolicitationMotoboyPresenter {

    val time: Long
        get() = ConfigurationPreference.instance
            .getConfigurationValue(
                ConfigurationDef
                    .SUPER_LINK_SECONDS_FOR_NEXT_QUERY_TRACKING, ""
            ).toLong()

    /**
     * método que envia um Post para api motoboy
     * @param orderId
     * */
    override fun callMotoboy(orderId: String) {
        interactor.callMotoboy(orderId, object : APICallbackDefault<ResponseMotoboy, String> {
            override fun onSuccess(response: ResponseMotoboy) {
                execute(Result.Success(response))
            }

            override fun onError(error: ErrorMessage) {
                verificationStatusError(error)
            }
        })
    }

    /**
     * método que envia um Get para api motoboy
     * @param orderId
     * */
    override fun resendCallMotoboy(orderId: String) {

        val second = this.time * 1000
        Handler().postDelayed({
            interactor.resendCallMotoboy(
                orderId,
                object : APICallbackDefault<ResponseMotoboy, String> {
                    override fun onSuccess(response: ResponseMotoboy) {
                        execute(Result.Success(response), true)
                    }

                    override fun onError(error: ErrorMessage) {
                        verificationStatusError(error, true)
                    }
                })
        }, second)

    }

    /**
     * método que verifica qual é o status de error vinda da api e redireciona
     * @param error
     * */
    private fun verificationStatusError(
        error: ErrorMessage,
        isChronometer: Boolean = false
    ) {
        when (error.httpStatus) {
            HttpURLConnection.HTTP_UNAUTHORIZED -> execute(Result.Error.ExpiredSession(error))
            HTTP_ENHANCE -> execute(Result.Error.Enhance(error))
            HttpURLConnection.HTTP_NOT_FOUND -> execute(Result.Error.NotFound(error))
            HttpURLConnection.HTTP_INTERNAL_ERROR -> if (!isChronometer) execute(
                Result.Error.ServerError(
                    error
                )
            )
            else -> if (!isChronometer) execute(Result.Error.ServerError(error))
        }
    }

    /**
     * método que faz a lógica para onde tem que ser enviada a resposta vinda da api
     * @param result
     * */
    private fun execute(result: Result<Any>, isResendMotoboy: Boolean = false) = when (result) {
        is Result.Success -> view.responseMotoboy(result.data as ResponseMotoboy, isResendMotoboy)
        is Result.Error.ServerError -> view.serverError()
        is Result.Error.Enhance -> view.enhance()
        is Result.Error.NotFound -> view.notFound()
        is Result.Error.ExpiredSession -> view.expiredSession()
        else -> view.serverError()
    }

    /**
     * método que mostra o load da chamada do motoboy
     * */
    override fun callLoadMotoboy() {
        view.callLoadMotoboy(false)
    }

    /**
     * método que pega os argumentos do fragmento
     * */
    override fun loadParams() {
        view.loadParams()
    }

    /**
     * método que inicia os componentes na tela
     * */
    override fun initView() {
        view.initView()
    }

    /**
     * método para chamar o modal na tela
     * */
    override fun callBottonSheetGeneric(isShowModalMotoboy: Boolean) {
        view.callBottonSheetGeneric(isShowModalMotoboy)
    }

    /**
     * método para fechar o modal na tela
     * */
    override fun closeDialog() {
        Handler().postDelayed({
            view.closeDialog()
        }, 1000)

    }

    /**
     * método que verifica o status que vem da api quando o http code é 200
     * */
    override fun statusCodeMotoboy(motoboy: ResponseMotoboy, isResendMotoboy: Boolean) = when {
        motoboy.status.equals(ALLOCATED_DELIVERER) || motoboy.status.equals(STARTED) || motoboy.status.equals(
            FINESHED
        ) || motoboy.status.equals(FINESHED_WITH_ERROR) -> view.screenLocated(motoboy)
        motoboy.status.equals(ERROR_CANCELLATION_PENDING) || motoboy.status.equals(CANCELLED) -> view.collectionCanceled()
        motoboy.status.equals(ERROR) -> view.navigateUp()
        else -> view.callLoadMotoboy(isResendMotoboy)
    }

    /**
     * método que abre o link no browser via device
     * @param trackingUrl
     * */
    override fun openBrowser(trackingUrl: String) {
        view.openTrackingUrl(trackingUrl)
    }

    /**
     * método que abre o link no browser via device
     * @param trackingUrl
     * */
    override fun delayCloseLocatesScreen(dialog: Dialog) {
        view.delayCloseLocatesScreen(dialog)
    }
}