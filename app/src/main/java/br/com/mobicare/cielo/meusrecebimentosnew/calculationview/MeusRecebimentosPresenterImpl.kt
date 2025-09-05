package br.com.mobicare.cielo.meusrecebimentosnew.calculationview

import android.text.format.DateUtils
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.justDate
import br.com.mobicare.cielo.extensions.configureIoAndMainThread
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.meusRecebimentos.presentation.presenter.MeusRecebimentosInteractor
import br.com.mobicare.cielo.meusRecebimentos.presentation.presenter.MeusRecebimentosInteractorImpl
import br.com.mobicare.cielo.meusrecebimentosnew.models.Summary
import br.com.mobicare.cielo.minhasVendas.fragments.common.QuickFilter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.text.SimpleDateFormat
import java.util.*

class MeusRecebimentosPresenterImpl(private val view: MeusRecebimentosView) :
    MeusRecebimentosPresenterNew {

    private var disposible = CompositeDisposable()
    private val interactor: MeusRecebimentosInteractor = MeusRecebimentosInteractorImpl()
    private lateinit var quickFilter: QuickFilter
    private lateinit var summary: Summary
    private val featureTogglePreference: FeatureTogglePreference = FeatureTogglePreference()

    override fun onCreate(initialDate: String, finalDate: String) {
        quickFilter = QuickFilter.Builder()
            .initialDate(initialDate)
            .finalDate(finalDate)
            .build()

        onCalculationVision()
        onLoadReceivablesBankAccounts()
    }

    override fun onCalculationVision() {
        this.view.onShowLoadingCalculationVision(true)
        interactor.onCalculationVision(quickFilter.initialDate!!, quickFilter.finalDate!!)
            .configureIoAndMainThread()
            .subscribe({
                view.onCalculationVisionSuccess(it, quickFilter)
                summary = it.summary
            }, {
                this.view.onShowLoadingCalculationVision(false)
                view.onCalculationVisionError(APIUtils.convertToErro(it))
            }).addTo(disposible)
    }

    override fun onLoadAlerts() {
            interactor.onLoadAlerts()
                .configureIoAndMainThread()
                .subscribe({ view.onLoadAlertsSuccess(it) },
                    { error -> view.onLoadAlertsError(APIUtils.convertToErro(error)) })
                .addTo(disposible)
    }

    override fun onGeneratePdfAlerts() {
        interactor.onGeneratePdfAlerts()
            .configureIoAndMainThread()
            .subscribe({ view.onLoadAlertsPdfSuccess(it) },
                { error -> view.onLoadAlertsPdfError(APIUtils.convertToErro(error)) })
            .addTo(disposible)
    }

    override fun onLoadReceivablesBankAccounts() {
        quickFilter.initialDate?.let { itInitialDate ->
            quickFilter.finalDate?.let { itFinalDate ->
                this.view.onShowLoadingReceivablesBankAccounts(true)

                var isPrevisto = true
                val today = Calendar.getInstance().time.justDate()
                val dateStart =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(itInitialDate)
                val dateFinal =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(itFinalDate)

                if (dateFinal.before(today) || DateUtils.isToday(dateFinal.time) && dateStart.before(
                        today
                    )
                ) {
                    isPrevisto = false
                }

                interactor.getReceivablesBankAccounts(itInitialDate, itFinalDate)
                    .configureIoAndMainThread()
                    .subscribe({
                        if (it.items.isEmpty()) {
                            view.onHideReceivablesBankAccounts()
                        } else {
                            view.onShowReceivablesBankAccounts(it.items, isPrevisto)
                        }
                    }, {
                        view.onHideReceivablesBankAccounts()
                    })
                    .addTo(disposible)
            } ?: view.onHideReceivablesBankAccounts()
        } ?: view.onHideReceivablesBankAccounts()
    }

    override fun onClickPendingAmount() {
        view.onClickPendingAmount(summary, quickFilter)
    }

    override fun onDestroy() = disposible.dispose()

    override fun onResume() {
        if (disposible.isDisposed) disposible = CompositeDisposable()
    }

    override fun initializeAlerts() {
        featureTogglePreference
            .getFeatureTogle(FeatureTogglePreference.ALERT_RECEIVABLE)
            .let { alertReceivable ->
                if (alertReceivable) {
                    featureTogglePreference
                        .getFeatureToggleString(FeatureTogglePreference.DATE_ALERT_RECEIVABLE)
                        .let { retorno ->
                            view.showAlerts(retorno ?: "")
                        }
                } else {
                    view.hideAlerts()
                }

            }
    }


}