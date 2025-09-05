package br.com.mobicare.cielo.pixMVVM.data.repository

import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixOnBoardingRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixOnBoardingRepository

class PixOnBoardingRepositoryImpl(
    private val remoteDataSource: PixOnBoardingRemoteDataSource
) : PixOnBoardingRepository {

    override suspend fun getOnBoardingFulfillment() =
        remoteDataSource.getOnBoardingFulfillment()

}