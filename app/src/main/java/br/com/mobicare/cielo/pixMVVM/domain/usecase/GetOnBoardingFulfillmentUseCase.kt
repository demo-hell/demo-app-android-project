package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCaseWithoutParams
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixOnBoardingRepository

class GetOnBoardingFulfillmentUseCase(
    private val repository: PixOnBoardingRepository
) : UseCaseWithoutParams<OnBoardingFulfillment> {

    override suspend fun invoke() = repository.getOnBoardingFulfillment()

}