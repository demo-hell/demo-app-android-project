package br.com.mobicare.cielo.turboRegistration.domain.usecase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.data.model.request.AddressRequest
import br.com.mobicare.cielo.turboRegistration.domain.repository.AddressRepository

class UpdateAddressUseCase(private val addressRepository: AddressRepository) {
    suspend operator fun invoke(addressId: String, addressRequest: AddressRequest): CieloDataResult<Void> =
        addressRepository.updateAddress(addressId, addressRequest)

}