package br.com.mobicare.cielo.migration

import android.content.Context
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.domain.MultichannelUserTokenResponse
import br.com.mobicare.cielo.migration.domain.MigrationRequest
import io.reactivex.Observable
import retrofit2.Response

class MigrationDataSource (context: Context) {

    private var api: CieloAPIServices = CieloAPIServices.getInstance(context, BuildConfig.HOST_API)

    fun migrationUser(migrationRequest: MigrationRequest,
                      accessToken: String,
                      authorization: String):
            Observable<MultichannelUserTokenResponse> {
        return api.migrationUser(migrationRequest, accessToken, authorization)
    }

    fun getMigrationVerification(accessToken: String): Observable<Response<Unit>> {
        return api.getUserMigration(accessToken)
    }

}