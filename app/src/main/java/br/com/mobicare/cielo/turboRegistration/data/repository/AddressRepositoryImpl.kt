package br.com.mobicare.cielo.turboRegistration.data.repository

import br.com.mobicare.cielo.turboRegistration.data.dataSource.AddressDataSource
import br.com.mobicare.cielo.turboRegistration.data.model.request.AddressRequest
import br.com.mobicare.cielo.turboRegistration.domain.repository.AddressRepository

class AddressRepositoryImpl(
    private val addressDataSource: AddressDataSource
) : AddressRepository {
    override suspend fun getAddressByCep(cep: String) = addressDataSource.getAddressByCep(cep)
    override suspend fun updateAddress(addressId: String, address: AddressRequest) = addressDataSource.updateAddress(addressId, address)
}