package br.com.mobicare.cielo.interactBannersOffersNew.data.datasource.local

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.interactBannersOffersNew.utils.InteractBannersConstants.LIST_INTERACT_OFFER
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class InteractBannerNewLocalDataSource(val userPreferences: UserPreferences) {
    fun getLocalInteractBannersOffers(): CieloDataResult<List<HiringOffers>> {
        return try {
            val type = object : TypeToken<List<HiringOffers>>() {}.type
            val offers = Gson().fromJson<List<HiringOffers>>(userPreferences.getInteractOffers(), type)

            offers?.let {
                CieloDataResult.Success(filterHiringOffers(offers))
            } ?: CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))
        } catch (ex: Exception) {
            CieloDataResult.APIError(CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR))
        }
    }

    fun deleteLocalInteractBannersOffers(): CieloDataResult<Boolean> {
        userPreferences.delete(LIST_INTERACT_OFFER)
        return CieloDataResult.Success(value = true)
    }

    fun putHiringOffersLocal(offers: List<HiringOffers>): CieloDataResult<Boolean> {
        userPreferences.putInteractOffers(offers)
        return CieloDataResult.Success(value = true)
    }

    private fun filterHiringOffers(offers: List<HiringOffers>?): List<HiringOffers> {
        val processingOffers = userPreferences.getProcessingOffers()
        return offers?.filterNot { offer -> processingOffers.contains(offer.id) } ?: emptyList()
    }
}