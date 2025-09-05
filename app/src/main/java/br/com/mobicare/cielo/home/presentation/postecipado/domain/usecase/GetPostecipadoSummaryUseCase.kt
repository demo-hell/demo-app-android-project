package br.com.mobicare.cielo.home.presentation.postecipado.domain.usecase

import br.com.mobicare.cielo.home.presentation.postecipado.data.repository.PostecipadoSummaryRepositoryImpl

class GetPostecipadoSummaryUseCase(private val repository: PostecipadoSummaryRepositoryImpl) {
    suspend operator fun invoke(
        planName: String
    ) = repository.getPlanInformation(planName)
}