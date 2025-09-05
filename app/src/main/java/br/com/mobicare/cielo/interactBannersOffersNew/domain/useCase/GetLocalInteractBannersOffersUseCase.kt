package br.com.mobicare.cielo.interactBannersOffersNew.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.interactBannersOffersNew.domain.repository.InteractBannerNewRepository
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers

class GetLocalInteractBannersOffersUseCase(private val repository: InteractBannerNewRepository) {

    suspend operator fun invoke(): CieloDataResult<List<HiringOffers>> {
        return repository.getLocalInteractBannersOffers()
    }
}