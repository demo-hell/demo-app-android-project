package br.com.mobicare.cielo.pixMVVM.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.pixMVVM.domain.model.OnBoardingFulfillment

interface PixOnBoardingRepository {
    suspend fun getOnBoardingFulfillment(): CieloDataResult<OnBoardingFulfillment>
}