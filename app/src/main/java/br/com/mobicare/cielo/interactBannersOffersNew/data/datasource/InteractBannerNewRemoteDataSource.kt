package br.com.mobicare.cielo.interactBannersOffersNew.data.datasource

import br.com.mobicare.cielo.commons.constants.DASH_CHAR
import br.com.mobicare.cielo.commons.constants.UNDERLINE_CHAR
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.SafeApiCaller
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.interactBannersOffersNew.data.datasource.remote.InteractBannerServerAPI
import br.com.mobicare.cielo.interactBannersOffersNew.utils.enums.InteractBannerEnum
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers

class InteractBannerNewRemoteDataSource(
    private val serverAPI: InteractBannerServerAPI,
    private val safeApiCaller: SafeApiCaller,
    private val userPreferences: UserPreferences
) {
    suspend fun getRemoteInteractBannersOffers(): CieloDataResult<List<HiringOffers>> {
        var result: CieloDataResult<List<HiringOffers>> =
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))

        safeApiCaller.safeApiCall {
            serverAPI.getHiringOffers()
        }.onSuccess { response ->
            result = response.body()?.let {
                val filteredOffers = filterHiringOffers(it).filter { hiringOffers ->
                    InteractBannerEnum.values().any { validator ->
                        validator.name.lowercase().replace(DASH_CHAR, UNDERLINE_CHAR) == hiringOffers.name?.lowercase()
                    }
                }
                if (filteredOffers.isEmpty()) {
                    CieloDataResult.Empty()
                } else {
                    CieloDataResult.Success(filteredOffers)
                }
            } ?: CieloDataResult.Empty()
        }.onError {
            result = CieloDataResult.APIError(it.apiException)
        }.onEmpty {
            result = CieloDataResult.Empty()
        }
        return result
    }

    private fun filterHiringOffers(offers: List<HiringOffers>?): List<HiringOffers> {
        val processingOffers = userPreferences.getProcessingOffers()
        return offers?.filterNot { offer -> processingOffers.contains(offer.id) } ?: emptyList()
    }
}
