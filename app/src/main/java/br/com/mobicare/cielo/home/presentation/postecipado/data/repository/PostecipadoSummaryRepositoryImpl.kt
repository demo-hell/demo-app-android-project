package br.com.mobicare.cielo.home.presentation.postecipado.data.repository

import br.com.mobicare.cielo.home.presentation.postecipado.data.datasource.PostecipadoRemoteDataSource
import br.com.mobicare.cielo.home.presentation.postecipado.domain.repository.PostecipadoSummaryRepository

class PostecipadoSummaryRepositoryImpl(
    private val postecipadoRemoteDataSource: PostecipadoRemoteDataSource
): PostecipadoSummaryRepository {

    override suspend fun getPlanInformation(planName: String) = postecipadoRemoteDataSource.getPlanInformation(planName)
}