package br.com.mobicare.cielo.interactBannersOffersNew.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers

interface InteractBannerNewRepository {
    suspend fun getRemoteInteractBannersOffers(): CieloDataResult<List<HiringOffers>>
    suspend fun getLocalInteractBannersOffers(): CieloDataResult<List<HiringOffers>>
    suspend fun deleteLocalInteractBannersOffers(): CieloDataResult<Boolean>
    suspend fun saveLocalInteractBannersOffers(offers: List<HiringOffers>): CieloDataResult<Boolean>
}