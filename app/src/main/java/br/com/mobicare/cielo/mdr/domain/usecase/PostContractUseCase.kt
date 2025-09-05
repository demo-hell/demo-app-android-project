package br.com.mobicare.cielo.mdr.domain.usecase
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface PostContractUseCase {
    suspend operator fun invoke(
        apiId: String,
        bannerId: Int,
        isAccepted: Boolean,
    ): CieloDataResult<Void>
}
