package br.com.mobicare.cielo.turboRegistration.domain.repository

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.turboRegistration.data.model.request.AddressRequest
import br.com.mobicare.cielo.turboRegistration.domain.model.Address

interface AddressRepository {
    suspend fun getAddressByCep(cep: String): CieloDataResult<Address>
    suspend fun updateAddress(addressId: String, address: AddressRequest): CieloDataResult<Void>
}