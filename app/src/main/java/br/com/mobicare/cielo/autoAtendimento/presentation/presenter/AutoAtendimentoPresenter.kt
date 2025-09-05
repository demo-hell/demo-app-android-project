package br.com.mobicare.cielo.autoAtendimento.presentation.presenter

import br.com.mobicare.cielo.autoAtendimento.domain.api.AutoAtendimentoRepository
import br.com.mobicare.cielo.autoAtendimento.domain.model.Supply
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.Utils

class AutoAtendimentoPresenter(private val repository: AutoAtendimentoRepository): AutoAtendimentoContract.Presenter{

    private var mView: AutoAtendimentoContract.View? = null

    override fun setView(mView: AutoAtendimentoContract.View) {
        this.mView = mView
    }

    override fun loadSuplies() {
        val accessToken = UserPreferences.getInstance().token ?: ""

        val authoziration = Utils.authorization()
        repository.callBack(this)
        repository.loadSuplies(accessToken, authoziration)
    }

    override fun responseListSuplies(supplies: List<Supply>) {
        mView?.responseListSuplies(supplies)
    }

    override fun errorResponse(e: Throwable) {
        mView?.errorResponse(e)
    }

}