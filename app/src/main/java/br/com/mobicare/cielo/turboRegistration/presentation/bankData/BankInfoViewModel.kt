package br.com.mobicare.cielo.turboRegistration.presentation.bankData

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.turboRegistration.domain.model.Bank
import br.com.mobicare.cielo.turboRegistration.domain.model.Operation
import br.com.mobicare.cielo.turboRegistration.domain.usecase.GetOperationsUseCase
import br.com.mobicare.cielo.turboRegistration.domain.usecase.SearchBanksUseCase
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationResource
import kotlinx.coroutines.launch

class BankInfoViewModel(
    private val searchBanksUseCase: SearchBanksUseCase,
    private val operationUseCase: GetOperationsUseCase
): ViewModel() {

    private val _banks = MutableLiveData<RegistrationResource<List<Bank>>>()
    val banks: LiveData<RegistrationResource<List<Bank>>> get() = _banks

    private val _operations = MutableLiveData<RegistrationResource<List<Operation>>>()
    val operations: LiveData<RegistrationResource<List<Operation>>> get() = _operations
    private var bankName: String? = null

    fun searchBanks() {
        viewModelScope.launch {
            _banks.value = RegistrationResource.Loading
            searchBanksUseCase(bankName).onSuccess {
                _banks.value = RegistrationResource.Success(it)
            }.onError {
                _banks.value = RegistrationResource.Error(it.apiException)
            }.onEmpty {
                _banks.value = RegistrationResource.Success(emptyList())
            }
        }
    }

    fun getCaixaOperations() {
        viewModelScope.launch {
            _operations.value = RegistrationResource.Loading
            operationUseCase().onSuccess {
                _operations.value = RegistrationResource.Success(it)
            }.onError {
                _operations.value = RegistrationResource.Error(it.apiException)
            }.onEmpty {
                _operations.value = RegistrationResource.Success(emptyList())
            }
        }
    }

    fun setName(name: String?) {
        bankName = name
    }
}