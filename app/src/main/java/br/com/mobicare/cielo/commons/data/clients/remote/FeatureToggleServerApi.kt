package br.com.mobicare.cielo.commons.data.clients.remote

import br.com.mobicare.cielo.commons.constants.FOUR_HUNDRED
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FeatureToggleServerApi {
    @GET("/site-cielo/v1/configurations/featuretoggle")
    suspend fun getFeatureToggle(
        @Query("system") system: String?,
        @Query("version") version: String?,
        @Query("platform") platform: String?,
        @Query("page") page: Int? = ZERO,
        @Query("size") pageSize: Int? = FOUR_HUNDRED,
        @Query("status") status: String? = Text.ACTIVATED_STATUS
    ): Response<FeatureToggleResponse>
}