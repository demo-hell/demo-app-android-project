package br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.meuCadastroNovo.MeuCadastroRepository
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.TrocaDomicilioSolicitacoesView
import io.reactivex.rxkotlin.addTo

class TrocaDomicilioSolicitacoesPresenterImpl(private val mView: TrocaDomicilioSolicitacoesView,
                                              private val repository: MeuCadastroRepository) : TrocaDomicilioSolicitacoesPresenter {

    private val compositeDisposableHandler = CompositeDisposableHandler()
    private var isLastPage = false

    override fun getDomicile(isLoading: Boolean, protocol: String?, status: String?, page: Int?, pageSize: Int?) {
        if (isLastPage) return
        repository
                .getDomiciles(protocol, status, page, pageSize)
                .configureIoAndMainThread()
                .doOnSubscribe {
                    if (isLoading) mView.showLoading() else mView.showLoadingMore()
                }
                .doFinally {
                    if (isLoading) mView.hideLoading() else mView.hideLoadingMore()
                }
                .subscribe(
                        {
                            isLastPage = it.pagination?.lastPage ?: false

                            if (it.items.isNullOrEmpty())
                                mView.showEmptyList()
                            else
                                mView.onSuccess(it.items)
                        },
                        {
                            val error = APIUtils.convertToErro(it)

                            when (error.httpStatus) {
                                400, 420 -> mView.showEmptyList()
                                else -> mView.showError(error)
                            }

                        })
                .addTo(compositeDisposableHandler.compositeDisposable)
    }

    override fun onResume() {
        compositeDisposableHandler.start()
    }

    override fun onPause() {
        compositeDisposableHandler.destroy()
    }

    override fun isToShow(): Boolean = FeatureTogglePreference.instance
            .getFeatureTogle(FeatureTogglePreference.ACOMPANHA_TROCA_DOMICILIO)

    override fun resetPagination() {
        isLastPage = false
    }
}