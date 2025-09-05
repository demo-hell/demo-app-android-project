package br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.presenter

import android.content.Context
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.data.clients.managers.EsqueciUsuarioAndEstabelecimentoRepository
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciEstabelecimentoObj
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciUsuarioObj
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.UserNameObj
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.ui.EsqueciUsuarioAndEstabelecimentoCallback
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.ui.EsqueciUsuarioAndEstabelecimentoContract

class EsqueciUsuarioAndEstabelecimentoPresenter(private var context: Context,
                                                private var mView: EsqueciUsuarioAndEstabelecimentoContract.View,
                                                private var repository: EsqueciUsuarioAndEstabelecimentoRepository,
                                                var isEstablishment: Boolean,
                                                var showCPF: Boolean) :
        EsqueciUsuarioAndEstabelecimentoContract.Presenter, EsqueciUsuarioAndEstabelecimentoCallback {

    private var userList: Array<UserNameObj>? = null
    var list: Array<String>? = null
    var listUsers: Array<UserNameObj>? = null
    var ecParam: String? = null
    //    var context = mView as Context
    var screenName = ""
    var screenNameId = 0


    override fun checkLabels() {

        if (!mView.isAttached()) {
            return
        }

        if (isEstablishment) {

            if (showCPF) {
                mView.changeLabels(R.string.esqueci_ec_cpf_description)
                mView.managerField(R.string.esqueci_ec_cpf_hint)
                mView.addMask(R.string.esqueci_cpf_mask)
                screenName = context.getString(R.string.ajuda_title) + " > " + context.getString(R.string.ga_esqueci_ec_pf)
                screenNameId = R.string.ga_esqueci_ec_pf
            } else {
                mView.changeLabels(R.string.esqueci_ec_cnpj_description)
                mView.managerField(R.string.esqueci_ec_cnpj_hint)
                mView.addMask(R.string.esqueci_cnpj_mask)
                screenName = context.getString(R.string.ajuda_title) + " > " + context.getString(R.string.ga_esqueci_ec_pj)
                screenNameId = R.string.ga_esqueci_ec_pj
            }

        } else {
            mView.changeLabels(R.string.esqueci_usuario_ec_description)
            mView.managerField(R.string.esqueci_usuario_ec_hint)
            screenName = context.getString(R.string.ajuda_title) + " > " + context.getString(R.string.ga_esqueci_usuario)
            screenNameId = R.string.ga_esqueci_usuario
        }

    }


    override fun callAPI(params: String) {
        ecParam = params
        if (isEstablishment) {
            callRecoveryEC(params)
        } else {
            callRecoveryUser(params)
        }
    }

    fun isValid(param: String): Boolean {
        if (isEstablishment) {
            if (showCPF) {
                return validCPF(param)
            }

            return validCNPJ(param)
        }

        return validEC(param)
    }

    fun validEC(ec: String): Boolean {
        if (Utils.isEmpty(ec)) {
            if (mView.isAttached()) {
                mView.showLocalError(R.string.dialog_error_ec)
            }
            return false
        }

        return true
    }

    fun validCPF(cpf: String): Boolean {
        if (!ValidationUtils.isCPF(cpf)) {
            if (mView.isAttached()) {
                mView.showLocalError(R.string.dialog_error_cpf)
            }
            return false
        }

        return true
    }

    fun validCNPJ(cnpj: String): Boolean {
        if (!ValidationUtils.isCNPJ(cnpj)) {
            if (mView.isAttached()) {
                mView.showLocalError(R.string.dialog_error_cnpj)
            }
            return false
        }

        return true
    }

    private fun callRecoveryUser(ec: String) {

        if (!isValid(ec)) {
            return
        }
        repository.recoveryUser(ec, this)
    }

    private fun callRecoveryEC(params: String) {
        if (!isValid(params)) {
            return
        }
        repository.recoveryEC(params, this)
    }

    override fun chooseItem(id: Int, ecUser: String?) {
        if (isEstablishment) {
            if (mView.isAttached()) {
                //mView.saveEC(list?.get(id))
                mView.changeActivity(false)
            }
        } else {
            callSendEmail(id.toString(), ecUser!!)
        }
    }

    fun callSendEmail(userID: String, ec: String) {
        repository.sendEmail(userID, ec, this)
    }


    override fun onStart() {
        if (mView.isAttached()) {
            mView.showProgress()
        }
    }

    override fun onError(error: ErrorMessage) {
        if (mView.isAttached()) {
            mView.hideProgress()
            mView.showMessageError(error.message)
        }
    }

    override fun onFinish() {
        if (mView.isAttached()) {
            mView.hideProgress()
        }
    }

    override fun onSuccess(response: EsqueciUsuarioObj?) {
        userList = response?.loginList
        if (mView.isAttached()) {
            if (userList != null) {
                onUserSuccess(userList)
            } else {
                if (response?.emailResponse != null) {
                    mView.showMessageUser(response.emailResponse!!)
                }
            }
        }
    }

    override fun onSuccess(response: EsqueciEstabelecimentoObj?) {
        list = response?.ecs
        if (mView.isAttached()) {
            if (list != null) {
                mView.onSuccess(screenName, R.string.dialog_esqueci_ec_title, list, R.string.dialog_esqueci_usar_numero)
            } else {
                mView.showMessageSucess(msgId = R.string.dialog_esqueci_ec_error)
            }
        }
    }

    override fun onUserSuccess(response: Array<UserNameObj>?) {
        userList = response
        if (mView.isAttached()) {
            mView.onUserSuccess(screenName, R.string.dialog_esqueci_usuario_title, userList, R.string.dialog_btn_continuar)
        }
    }
}
