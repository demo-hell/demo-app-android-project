package br.com.mobicare.cielo.commons.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.domain.repository.local.UserInformationLocalRepository
import br.com.mobicare.cielo.login.domains.entities.UserObj

class GetUserObjUseCase(private val repository: UserInformationLocalRepository) {
    suspend operator fun invoke(): CieloDataResult<UserObj> = repository.getUserObj()
}