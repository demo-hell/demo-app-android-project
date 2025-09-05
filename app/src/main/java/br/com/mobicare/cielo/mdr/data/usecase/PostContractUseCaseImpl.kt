package br.com.mobicare.cielo.mdr.data.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import br.com.mobicare.cielo.mdr.domain.repository.MdrRepository
import br.com.mobicare.cielo.mdr.domain.usecase.PostContractUseCase

class PostContractUseCaseImpl(
    private val mdrRepository: MdrRepository,
) : PostContractUseCase {
    override suspend fun invoke(
        apiId: String,
        bannerId: Int,
        isAccepted: Boolean,
    ): CieloDataResult<Void> {
        return try {
            mdrRepository.postContractDecision(apiId, bannerId, isAccepted)
        } catch (error: Exception) {
            error.message.logFirebaseCrashlytics()
            CieloDataResult.Empty()
        }
    }
}
