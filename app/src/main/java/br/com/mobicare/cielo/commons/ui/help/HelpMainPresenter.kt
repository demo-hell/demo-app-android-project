package br.com.mobicare.cielo.commons.ui.help

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.recebaMais.managers.RecebaMaisHelpRepository
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class HelpMainPresenter(private val repository: RecebaMaisHelpRepository) : HelpMainContract.Presenter {

    private lateinit var mView: HelpMainContract.View
    private var composite = CompositeDisposable()
    private var uiScheduler: Scheduler? = AndroidSchedulers.mainThread()
    private var ioScheduler: Scheduler? = Schedulers.io()

    override fun setView(view: HelpMainContract.View) {
        mView = view
    }

    override fun loadHelps(idHelp : String) {
        composite.add(repository.getHelpCenter()
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    mView.helpsSuccess(it.helpCenter.first {
                        it.id == idHelp
                    })
                }, {
                    onErrorDefalt(it)
                }))
    }


    private fun onErrorDefalt(error: Throwable) {
        mView.let {
            val errorMessage = APIUtils.convertToErro(error)
            if (errorMessage.logout) {
                it.logout(errorMessage)
            } else {
                it.showError(errorMessage)
            }
        }
    }


}