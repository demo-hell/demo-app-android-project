package br.com.mobicare.cielo.interactBannersOffersNew.data.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.interactBannersOffersNew.data.datasource.InteractBannerNewRemoteDataSource
import br.com.mobicare.cielo.interactBannersOffersNew.data.datasource.local.InteractBannerNewLocalDataSource
import br.com.mobicare.cielo.interactBannersOffersNew.domain.repository.InteractBannerNewRepository
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers

class InteractBannerNewRepositoryImpl(
    private val remoteDataSource: InteractBannerNewRemoteDataSource,
    private val localDataSource: InteractBannerNewLocalDataSource,
) : InteractBannerNewRepository {

    override suspend fun getRemoteInteractBannersOffers(): CieloDataResult<List<HiringOffers>> =
        remoteDataSource.getRemoteInteractBannersOffers()

    override suspend fun getLocalInteractBannersOffers(): CieloDataResult<List<HiringOffers>> =
        localDataSource.getLocalInteractBannersOffers()

    override suspend fun deleteLocalInteractBannersOffers(): CieloDataResult<Boolean> =
        localDataSource.deleteLocalInteractBannersOffers()

    override suspend fun saveLocalInteractBannersOffers(offers: List<HiringOffers>): CieloDataResult<Boolean> =
        localDataSource.putHiringOffersLocal(offers)
}