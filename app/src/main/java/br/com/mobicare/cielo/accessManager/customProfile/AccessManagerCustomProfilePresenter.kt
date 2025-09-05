package br.com.mobicare.cielo.accessManager.customProfile

import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.pix.constants.ACTIVE
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class AccessManagerCustomProfilePresenter(
    private val repository: AccessManagerRepository,
    private val view: AccessManagerCustomProfileContract.View,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler
) : AccessManagerCustomProfileContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun getCustomActiveProfiles() {
        view.showLoading()
        disposable.add(
            repository.getProfiles(Text.CUSTOM, ACTIVE)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({ response ->
                    view.hideLoading()
                    view.showCustomProfiles(response)
                }, {
                    view.hideLoading()
                    view.showErrorProfile()
                })
        )
    }

    override fun getDetailCustomProfile(profileId: String) {
        view.showLoading()
        disposable.add(
            repository.getProfileDetail(profileId)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    view.getDetailSuccess(it)
                },{
                    view.hideLoading()
                    view.showErrorProfile()
                })
        )
    }

    fun getCustomUsers(profileId: String) {
            view.showLoading()
            disposable.add(
                repository.getCustomUsersWithRole(true, profileId)
                    .observeOn(uiScheduler)
                    .subscribeOn(ioScheduler)
                    .subscribe({ accessManagerUsersResponse ->
                        view.hideLoading()
                        view.showCustomUsers(accessManagerUsersResponse)
                    }, {
                        view.hideLoading()
                        view.showErrorProfile()
                    })
            )
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}