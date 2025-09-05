package br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter

import br.com.mobicare.cielo.commons.constants.HTTP_UNAUTHORIZED
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.GetUserAdditionalInfo
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.MeuCadastroContract
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class DadosUsuarioPresenter(
    val mView: MeuCadastroContract.DadosUsuarioView,
    val mRepository: MeuCadastroContract.MeuCadastroRepository,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) {

    private val compositeDisposable = CompositeDisposable()
    private var _userAdditionalInfo: GetUserAdditionalInfo? = null
    val userAdditionalInfo: GetUserAdditionalInfo?
        get() = _userAdditionalInfo

    fun loadDadosUser(token: String) {
        mRepository.loadMe(token, compositeDisposable, { usr ->
            mView.showUser(usr)
            UserPreferences.getInstance().saveUserActionPermissions(usr.roles.toSet())
        }, {
            val errorMessage = APIUtils.convertToErro(it)
            when (errorMessage.httpStatus) {
                401 -> mView.logout()
                else -> mView.error()
            }
        })
    }

    fun getAdditionalInfo(){
        compositeDisposable.add(
            mRepository.getAdditionalInfo()
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({ response ->
                    _userAdditionalInfo = response
                    mView.showAdditionalInfo(
                        response.typeOfCommunication,
                        response.contactPreference,
                        response.timeOfDay,
                        response.pcdType
                    )
                }, { error ->
                    val errorMessage = APIUtils.convertToErro(error)
                    when (errorMessage.httpStatus) {
                        HTTP_UNAUTHORIZED -> mView.logout()
                        else -> mView.error()
                    }
                })
        )
    }

    fun onCleared() {
        compositeDisposable.clear()
    }

}