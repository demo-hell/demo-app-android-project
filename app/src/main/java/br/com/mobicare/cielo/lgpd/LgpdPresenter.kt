package br.com.mobicare.cielo.lgpd

import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.lgpd.domain.entity.LgpdElegibilityEntity
import br.com.mobicare.cielo.lgpd.domain.repository.LgpdRepository
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class LgpdPresenter(
        private val view: LgpdContract.View,
        private val repository: LgpdRepository,
        private val uiScheduler: Scheduler,
        private val ioScheduler: Scheduler
) : LgpdContract.Presenter {

    private val disposable = CompositeDisposable()
    private var isOwnerChecked: Boolean = false
    private var isAgreeChecked: Boolean = false
    private var password: ByteArray? = null
    private var isStartByLogin: Boolean = false

    private lateinit var entity: LgpdElegibilityEntity

    override fun loadElegibility(entity: LgpdElegibilityEntity, password: ByteArray?, isStartByLogin: Boolean) {
        this.entity = entity
        this.password = password
        this.isStartByLogin = isStartByLogin
        if (entity.digitalAccount == true && entity.owner == true) {
            this.view.render(State.OwnerLoggedWithDigitalAccount)
        } else if (entity.owner == true) {
            this.view.render(State.OwnerLoggedWithBankingDomicile)
        } else {
            this.view.render(State.SimpleUser)
        }
    }

    override fun onOnwerClicked(isChecked: Boolean) {
        this.isOwnerChecked = isChecked
        checkAgreeButton()
    }

    override fun onAgreeClicked(isChecked: Boolean) {
        this.isAgreeChecked = isChecked
        checkAgreeButton()
    }

    override fun onAgreeButtonClicked() {
        disposable.add(
                repository.postLgpdAgreement()
                        .observeOn(uiScheduler)
                        .subscribeOn(ioScheduler)
                        .doOnSubscribe {
                            this.view.render(State.Loading)
                        }
                        .subscribe({
                            if (it.code() in 200..204)
                                this@LgpdPresenter.view.showMainWindow(this.password, isStartByLogin = this.isStartByLogin)
                            else
                                viewError(APIUtils.convertToErro(it))
                        }, {
                            viewError(APIUtils.convertToErro(it))
                        }))
    }

    private fun viewError(error: ErrorMessage) {
        this@LgpdPresenter.view.render(State.Error?.apply {
            this.errorMessage = error
        })
    }

    override fun onBackButtonClicked() {
        this.view.showMainWindow(this.password, isStartByLogin = this.isStartByLogin)
    }

    private fun checkAgreeButton() {
        var ownerChecked = true
        if (this.entity.owner == true) {
            ownerChecked = this.isOwnerChecked
        }
        this.view.render(
                if (ownerChecked && this.isAgreeChecked)
                    State.EnableButton
                else
                    State.DisableButton
        )
    }

    sealed class State {
        object Loading : State()
        object Error : State() {
            var errorMessage: ErrorMessage? = null
        }

        object EnableButton : State()
        object DisableButton : State()
        object SimpleUser : State()
        object OwnerLoggedWithDigitalAccount : State()
        object OwnerLoggedWithBankingDomicile : State()
    }

}