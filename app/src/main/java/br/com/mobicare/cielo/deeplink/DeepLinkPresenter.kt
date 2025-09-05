package br.com.mobicare.cielo.deeplink

import br.com.mobicare.cielo.CieloApplication
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.domain.MultichannelUserTokenResponse
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

/**
 * create by Enzo Teles
 * */
class DeepLinkPresenter(val mView: DeepLinkContract.View, private val mInteractor: DeepLinkInteractor){

    /**
     * método para verificar a confirmação de email
     * @param token
     * */
    fun verificationEmailConfirmation(token: String?) {
        mInteractor.verificationEmailConfirmation(token, object : APICallbackDefault<Unit, String> {
            override fun onSuccess(response: Unit) {
                mView.sendEmailSucess()
            }
            override fun onError(error: ErrorMessage) {
               mView.getResponseSendEmail(error)
            }

        })
    }

    /**
     * método para reenviar o email
     * @param token
     * */
    fun resendEmail(token: String?){

        mInteractor.resendEmail(token, object :
                APICallbackDefault<MultichannelUserTokenResponse, String> {

            override fun onSuccess(response: MultichannelUserTokenResponse) {
                mView.modalResendEmail(CieloApplication.context?.getString(R.string.dp_modal_sucess_title)!!,
                        CieloApplication.context?.getString(R.string.dp_subtitle_sucess_01)!!, response)
            }

            override fun onError(error: ErrorMessage) {
                mView.getResponseResendEmail(error.httpStatus)
            }
        })

    }

    /**
     * método para limpar o disposable
     * */
    fun onCleared() {
        mInteractor.disposable()
    }

}