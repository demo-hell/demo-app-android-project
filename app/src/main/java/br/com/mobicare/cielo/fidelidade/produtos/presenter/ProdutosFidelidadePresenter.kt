package br.com.mobicare.cielo.fidelidade.produtos.presenter

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.fidelidade.data.managers.FidelidadeRepository
import br.com.mobicare.cielo.fidelidade.domains.ProdutoFidelidadeObjList
import br.com.mobicare.cielo.fidelidade.produtos.ui.ProdutosFidelidadeContract

/**
 * Created by silvia.miranda on 21/08/2017.
 */
class ProdutosFidelidadePresenter (var mView: ProdutosFidelidadeContract.View, var repository: FidelidadeRepository) : ProdutosFidelidadeContract.Presenter, APICallbackDefault<ProdutoFidelidadeObjList, ErrorMessage> {

    override fun callAPI() {
        repository.getProdutosFidelidade(this)

    }

    override fun onStart() {
        if (mView.isAttached()) {
            mView.showProgress()
        }
    }

    override fun onError(error: ErrorMessage) {
        if (mView.isAttached()) {
            mView.hideProgress()
            //todo mostrar icone de erros
        }
    }

    override fun onFinish() {
        if (mView.isAttached()) {
            mView.hideProgress()
        }
    }

    override fun onSuccess(response: ProdutoFidelidadeObjList) {
        if (mView.isAttached()) mView.loadItensCard(response.produtosFidelidade)
    }

}
