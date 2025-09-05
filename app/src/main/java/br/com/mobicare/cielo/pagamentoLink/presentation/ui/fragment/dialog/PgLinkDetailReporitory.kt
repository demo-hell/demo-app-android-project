package br.com.mobicare.cielo.pagamentoLink.presentation.ui.fragment.dialog

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.pagamentoLink.domains.DeleteLink
import br.com.mobicare.cielo.pagamentoLink.domains.PgLinkDataDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class PgLinkDetailReporitory(val dataSource: PgLinkDataDataSource){

    private var compositeDisp = CompositeDisposable()

    /*override fun disposable() {
        compositeDisp.clear()
    }*/

    fun deleteLink(token: String?, linkId: DeleteLink, callback: APICallbackDefault<Int, String>) {
        compositeDisp.add(dataSource.deleteLink(token, linkId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe({
                    callback.onSuccess(204)
                }, {
                    val errorMessage = APIUtils.convertToErro(it)
                    callback.onError(errorMessage)
                }))

    }

}