package br.com.mobicare.cielo.suporteTecnico.ui.presenter

import androidx.appcompat.app.AppCompatActivity
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.suporteTecnico.TechnicalSupportContract
import br.com.mobicare.cielo.suporteTecnico.domain.repo.TechnicalSupportRepository
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable


class TechnicalSupportPresenter(
    private val repository: TechnicalSupportRepository,
    private val view: TechnicalSupportContract.View,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : TechnicalSupportContract.Presenter {


    var activity: AppCompatActivity?= null

    override fun loadView(activity: AppCompatActivity) {
        this.activity = activity
    }
    private var composite = CompositeDisposable()

    override fun loadItems() {
        composite.add(repository.fetchTechnicalSupportRepository()
            .subscribeOn(ioScheduler)
            .observeOn(uiScheduler)
            .doOnSubscribe {
                this.view.showLoading()
            }
            .doAfterTerminate {
                this.view.hideLoading()
            }
            .subscribe({ data ->
                val sanitizedData = data.filter {
                    it.categoryName.isNullOrBlank().not() && it.problems.isEmpty().not()
                }
                this.view.loadTechnicalSupportItems(sanitizedData)
            }, { error ->

                if (error is RetrofitException) {
                    this.view.systemError(ErrorMessage().apply {
                        message = error.message.toString()
                        statusText = error.response?.raw()?.message()
                        httpStatus = error.response?.code() ?: -1
                        brokenServiceUrl = error.url.toString()
                        setType(error.kind)
                    })
                } else {
                    this.view.userError(ErrorMessage().apply {
                        message = activity!!.getString(R.string.text_technical_suppport_error)
                    })
                }
            }))
    }

}