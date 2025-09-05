package br.com.mobicare.cielo.main.data.managers

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.main.MenuRepository
import br.com.mobicare.cielo.main.data.clients.api.MenuDataSource
import br.com.mobicare.cielo.main.domain.AppMenuResponse
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.reactivex.Observable
import java.util.*

class AppMenuRepository(
    private val appMenuDataSourceNetwork: MenuDataSource,
    private val appMenuDataSourceLocal: MenuDataSource
) :
    MenuRepository {

    override fun getMenu(accessToken: String): Observable<AppMenuResponse?>? {
        val remote = appMenuDataSourceNetwork.getOthersMenu(accessToken)
            ?.doOnNext { menu ->
                menu?.let {
                    if (it.menu.isNotEmpty()) {
                        it.createdAt = Calendar.getInstance().timeInMillis
                        UserPreferences.getInstance().saveMenuApp(it)
                    }
                }
            }?.doOnError {
                FirebaseCrashlytics.getInstance().recordException(it)
            }

        return if (remote != null)
            Observable
                .merge(appMenuDataSourceLocal.getOthersMenu(accessToken), remote)
                .take(1)
        else appMenuDataSourceLocal.getOthersMenu(accessToken)
    }

}