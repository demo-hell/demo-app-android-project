package br.com.mobicare.cielo.main

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.main.data.clients.api.MenuDataSource
import br.com.mobicare.cielo.main.domain.AppMenuResponse
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class AppMenuLocalDataSource : MenuDataSource {

    override fun getOthersMenu(accessToken: String): Observable<AppMenuResponse?>? {
        return Observable.create<AppMenuResponse> { itObservable ->
            try {
                UserPreferences.getInstance().appMenu?.let {
                    if (System.currentTimeMillis() - it.createdAt <= TimeUnit.MINUTES.toMillis(1)) {
                        itObservable.onNext(it)
                        itObservable.onComplete()
                    }
                } ?: itObservable.onComplete()
            } catch (ex: Exception) {
                itObservable.onError(ex)
                itObservable.onComplete()
            }
        }
    }

}