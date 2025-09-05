package br.com.mobicare.cielo.pix.ui.transfer.agency.selectBank

import androidx.annotation.VisibleForTesting
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.pix.api.transfer.PixTransferRepositoryContract
import br.com.mobicare.cielo.pix.model.PixBank
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class PixSelectBankPresenter(
        private val view: PixSelectBankContract.View,
        private val repository: PixTransferRepositoryContract,
        private val uiScheduler: Scheduler,
        private val ioScheduler: Scheduler
) : PixSelectBankContract.Presenter {

    private var disposable = CompositeDisposable()

    @VisibleForTesting
    val filteredBanks = mutableListOf<PixBank>()

    @VisibleForTesting
    val allBanks = mutableListOf<PixBank>()

    override fun fetchAllBanks(): List<PixBank> = allBanks

    override fun getAllBanks() {
        disposable.add(
                repository.getAllBanks()
                        .observeOn(uiScheduler)
                        .subscribeOn(ioScheduler)
                        .doOnSubscribe {
                            view.showLoading()
                        }
                        .subscribe({
                            view.hideLoading()

                            allBanks.clear()
                            allBanks.addAll(it)

                            view.setupBankListView(it)
                        }, {
                            view.hideLoading()
                            view.showError()
                        })
        )
    }

    override fun searchBank(bankCodeOrName: String) {
        filteredBanks.apply {
            clear()

            addAll(allBanks.filter { itBank ->
                itBank.name.toLowerCasePTBR().contains(bankCodeOrName.toLowerCasePTBR()) or itBank.code.toString().contains(bankCodeOrName)
            })

            view.showFilteredBanks(this)
        }
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onPause() {
        disposable.dispose()
    }
}