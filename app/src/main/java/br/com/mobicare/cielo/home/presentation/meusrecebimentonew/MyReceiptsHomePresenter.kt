package br.com.mobicare.cielo.home.presentation.meusrecebimentonew

import android.annotation.SuppressLint
import android.os.Handler
import androidx.annotation.VisibleForTesting
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.helpers.DateTimeHelper
import br.com.mobicare.cielo.commons.utils.CompositeDisposableHandler
import br.com.mobicare.cielo.commons.utils.DataCustom
import br.com.mobicare.cielo.commons.utils.format
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.repository.MeusRecebimentosGraficoRepository
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.repository.PostingsResponse
import br.com.mobicare.cielo.meusrecebimentosnew.models.Summary
import io.reactivex.Scheduler
import java.util.*

private const val DELAY_TIME = 1500L

class MyReceiptsHomePresenter(
    private val view: MeusRecebimentosHomeContract.View,
    private val repository: MeusRecebimentosGraficoRepository,
    private val featureTogglePreference: FeatureTogglePreference,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler,
    private val handler: Handler
): MeusRecebimentosHomeContract.Presenter {

    private val compositeDisposable = CompositeDisposableHandler()
    private val yesterdayDate = DataCustom(DateTimeHelper.decreaseDateByNumberDays(Calendar.getInstance().time, ONE)).toDate().format()
    private val todayDate = Date().format()

    @SuppressLint("CheckResult")
    override fun getAllReceivables(initialDate: String?, finalDate: String?, isByRefreshing: Boolean) {
        val isToShowMyReceivables = featureTogglePreference.getFeatureToggleObject(FeatureTogglePreference.MEUS_RECEBIMENTOS)?.show ?: false

        if (isToShowMyReceivables.not()) {
            simulateDelay(isByRefreshing)
            return
        }

        repository.getPostingsGraph(initialDate ?: yesterdayDate, finalDate ?: todayDate)
            .observeOn(uiScheduler)
            .subscribeOn(ioScheduler)
            .doOnSubscribe {
                view.showLoading()
            }
            .subscribe({ response ->
                val yesterdayReceivables = getInfoAccordingToDate(response, yesterdayDate)
                val todayReceivables = getInfoAccordingToDate(response, todayDate)

                view.showReceivablesInfo(yesterdayReceivables, todayReceivables, isByRefreshing)
                view.hideLoading()
            }, {
                view.hideLoading()
                view.showError(APIUtils.convertToErro(it), isByRefreshing)
            })
    }

    private fun getInfoAccordingToDate(response: PostingsResponse, date: String): Summary? {
        return response.summary.firstOrNull { summary ->
            summary.date == date
        }
    }

    @VisibleForTesting
    private fun simulateDelay(isByRefreshing: Boolean = false) {
        handler.postDelayed({
            view.hideLoading()
            view.unavailableReceivables(isByRefreshing)
        }, DELAY_TIME)
    }

    override fun onResume() {
        compositeDisposable.start()
    }

    override fun onPause() {
        compositeDisposable.destroy()
    }

    fun checkShowNewReceivables() : Boolean {
        return featureTogglePreference.getFeatureToggleObject(FeatureTogglePreference.SHOW_RECEIVABLES_WEB)?.show ?: false
    }
}

