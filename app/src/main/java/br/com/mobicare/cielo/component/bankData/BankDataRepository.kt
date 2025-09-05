package br.com.mobicare.cielo.component.bankData

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BanksSet
import com.google.gson.internal.`$Gson$Preconditions`.checkNotNull
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class BankDataRepository(remoteDataSource: BankDataAPIDataSource) {
    private val remoteDataSource: BankDataAPIDataSource = checkNotNull(remoteDataSource)

    fun banks(callback: APICallbackDefault<BanksSet, String>) {
        remoteDataSource.allBanksNew()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { callback.onStart() }
                .subscribe(object : Observer<BanksSet> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onComplete() {
                        callback.onFinish()
                    }

                    override fun onError(e: Throwable) {
                        val errorMessage = APIUtils.convertToErro(e)
                        callback.onError(errorMessage)
                    }

                    override fun onNext(bankListResponse: BanksSet) {
                        callback.onSuccess(bankListResponse)
                    }
                })
    }

    companion object {

        private var instance: BankDataRepository? = null

        fun getInstance(remoteDataSource: BankDataAPIDataSource): BankDataRepository {
            if (instance == null) {
                instance = BankDataRepository(remoteDataSource)
            }

            return instance as BankDataRepository
        }
    }

}
