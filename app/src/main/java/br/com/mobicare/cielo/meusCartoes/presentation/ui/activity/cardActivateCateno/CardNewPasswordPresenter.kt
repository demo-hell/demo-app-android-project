package br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.cardActivateCateno

import android.util.Base64
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.meusCartoes.CreditCardsNewRespository
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.CardActivationCatenoRequest
import br.com.mobicare.cielo.meusCartoes.domains.entities.CardActivationStatusResponse

class CardNewPasswordPresenter(
        private val _View: CardNewPasswordContract.View,
        private val _Repository: CreditCardsNewRespository)
    : CardNewPasswordContract.Presenter {

    private var isCallActivateCard = true

    override fun onCleared() {
        _Repository.disposable()
    }


    override fun isValidPassword(cardActivation: CardActivationCatenoRequest): Boolean {
        var flag = false
        if (cardActivation.password.isEmpty()) {
            _View.passwordEmpty()
            flag = true
        }

        if (cardActivation.passwordConfirmation.isEmpty()) {
            _View.passwordConfirmEmpty()
            flag = true
        }

        if (!flag && cardActivation.password != cardActivation.passwordConfirmation) {
            _View.passwordNotMatch()
            flag = true
        }

        return !flag
    }


    override fun activateCard(proxy: String, cvv: String, dt: String, changePasswordCard: CardActivationCatenoRequest) {
        val ec = MenuPreference.instance.getEC() ?: ""
        val token = UserPreferences.getInstance()?.token

        if (isCallActivateCard) {
            _Repository.activateCard(proxy, ec, token!!,
                    object : APICallbackDefault<CardActivationStatusResponse, String> {
                        override fun onStart() {
                            _View.showLoading()
                            isCallActivateCard = true
                        }

                        override fun onError(error: ErrorMessage) {
                            isCallActivateCard = true
                            when {
                                error.logout -> _View.logout(error)
                                error.httpStatus >= 500 -> _View.showSubmit(error)
                                else -> _View.showError(error)
                            }
                        }

                        override fun onSuccess(response: CardActivationStatusResponse) {
                            if (response.active) {
                                isCallActivateCard = false
                                changePasswordCard(proxy, cvv, dt, changePasswordCard)
                            } else {
                                _View.hideLoading()
                                _View.showInvalidCardNumber()
                            }
                        }

                    })
        } else {
            changePasswordCard(proxy, cvv, dt, changePasswordCard)
        }
    }

    private fun changePasswordCard(proxy: String, cvv: String, dt: String, cardActivation: CardActivationCatenoRequest) {

        val proxyNew = "$proxy:$cvv:${dt.replace("/", ":")}"
        val encodedParam = Base64.encodeToString(proxyNew.toByteArray(), Base64.NO_WRAP)

        val token = UserPreferences.getInstance()?.token

        _Repository.changePasswordCard(proxy, cardActivation, token!!, encodedParam,
                object : APICallbackDefault<Int, String> {
                    override fun onStart() {
                        _View.showLoading()
                    }

                    override fun onError(error: ErrorMessage) {

                        when {
                            error.logout -> _View.logout(error)
                            error.httpStatus >= 500 -> _View.showSubmit(error)
                            else -> _View.showError(error)
                        }

                    }

                    override fun onSuccess(response: Int) {
                        _View.showSuccessActivation()
                    }
                })

    }

}