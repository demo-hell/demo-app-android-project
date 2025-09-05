package br.com.mobicare.cielo.turboRegistration.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.domain.model.Address
import br.com.mobicare.cielo.turboRegistration.domain.repository.AddressRepository

class GetAddressByCepUseCase(private val repository: AddressRepository) {
    suspend operator fun invoke(cep: String): CieloDataResult<Address> = repository.getAddressByCep(cep)
}