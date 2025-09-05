package br.com.mobicare.cielo.research

import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.utils.notNull
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.research.domains.entities.ResearchRating
import br.com.mobicare.cielo.research.domains.entities.ResearchResponse
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ResearchPresenter(var apiServices: CieloAPIServices,
                        var view: ResearchContract.ResearchView?,
                        var featureTogglePreference: FeatureTogglePreference) : ResearchContract.ResearchPresenter {

    private var composite = CompositeDisposable()
    var uiScheduler: Scheduler? = AndroidSchedulers.mainThread()
    var ioScheduler: Scheduler? = Schedulers.io()

    var addedRequestToQueue: Boolean = false

    override fun getResearch(ecNumber: String) {

        if (!addedRequestToQueue) {
            val sentResearch = UserPreferences.getInstance().sentResearch()

            if (sentResearch) {
                composite.add(createResearchInstance(UserPreferences.getInstance().userName,
                    ecNumber)
                        .subscribeOn(ioScheduler)
                        .observeOn(uiScheduler)
                        .subscribe({ data ->
                            view?.apply {
                                this.saveDataResearch(data)
                            }
                            addedRequestToQueue = false
                        }, {
                            view?.apply {
                                this.saveDataResearch(null)
                            }
                            addedRequestToQueue = false
                        }))
            } else {
                val noteResearch = UserPreferences.getInstance().noteResearch()
                val descriptionResearch = UserPreferences.getInstance().descriptionResearch()
                val researchRating = ResearchRating(noteResearch, descriptionResearch)
                saveResearch(researchRating, ecNumber)
            }
            addedRequestToQueue = true
        }

    }

    override fun saveResearch(
        researchRating: ResearchRating?,
        ecNumber: String
    ) {
        researchRating?.let {
            UserPreferences.getInstance().saveNoteResearch(researchRating.note)
            UserPreferences.getInstance().saveDescriptionResearch(researchRating.description)
            UserPreferences.getInstance().saveSentResearch(false)
            UserPreferences.getInstance().saveResearchData(null)
        }

        researchRating?.notNull {
            composite.add(createSaveResearchInstance(UserPreferences.getInstance().userName,
                ecNumber, researchRating)
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .subscribe({
                        UserPreferences.getInstance().saveResearchData(null)
                        UserPreferences.getInstance().saveNoteResearch(0)
                        UserPreferences.getInstance().saveDescriptionResearch("")
                        UserPreferences.getInstance().saveSentResearch(true)
                    }, {
                    }))
        }
    }


    fun createResearchInstance(username: String, ecNumber: String): Observable<ResearchResponse?> {
        return apiServices.getResearch(shouldUseNewResearchEnvironment(), username, ecNumber)
    }

    fun createSaveResearchInstance(username: String, ecNumber: String, researchRating: ResearchRating): Completable {
        return apiServices.saveResearch(researchRating, username, ecNumber, shouldUseNewResearchEnvironment())
    }

    private fun shouldUseNewResearchEnvironment(): Boolean {
        return featureTogglePreference.getFeatureTogle(FeatureTogglePreference.NEW_RESEARCH_ENVIRONMENT)
    }
}