package br.com.mobicare.cielo.centralDeAjuda.data.clients.managers


import br.com.mobicare.cielo.centralDeAjuda.data.clients.api.CentralDeAjudaAPIDataSource

class CentralDeAjudaRepository(private val remoteDataSource: CentralDeAjudaAPIDataSource) {

    fun registrationData() = remoteDataSource.registrationData()
}