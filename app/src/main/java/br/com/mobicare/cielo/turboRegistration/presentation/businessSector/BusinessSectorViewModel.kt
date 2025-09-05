package br.com.mobicare.cielo.turboRegistration.presentation.businessSector

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.turboRegistration.domain.model.Mcc
import br.com.mobicare.cielo.turboRegistration.domain.usecase.SearchBusinessLinesUseCase
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationResource
import kotlinx.coroutines.launch

class BusinessSectorViewModel(
    private val searchBusinessLinesUseCase: SearchBusinessLinesUseCase
): ViewModel() {

    private var _businessSector = MutableLiveData<RegistrationResource<List<Mcc>>>()
    val businessSector: LiveData<RegistrationResource<List<Mcc>>> = _businessSector
    private var searchQuery: String = ""
    private var selectedBusinessCode: Int? = null

    fun searchBusinessSector() {
        viewModelScope.launch {
            _businessSector.value = RegistrationResource.Loading
            searchBusinessLinesUseCase(searchQuery).onSuccess {
                _businessSector.value = RegistrationResource.Success(it)
            }.onError {
                _businessSector.value = RegistrationResource.Error(it.apiException)
            }.onEmpty {
                _businessSector.value = RegistrationResource.Empty
            }
        }
    }

    fun setQuery(query: String?) {
        if (query != null) {
            this.searchQuery = query
        }
    }

    fun currentQuery(): String {
        return searchQuery
    }

    fun setSelectedBusinessCode(code: Int) {
        this.selectedBusinessCode = code
    }
}