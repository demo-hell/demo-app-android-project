package br.com.mobicare.cielo.suporteTecnico.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.chargeback.utils.UiState
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.suporteTecnico.data.EquipmentEligibilityResponse
import br.com.mobicare.cielo.suporteTecnico.data.ProblemEquipments
import br.com.mobicare.cielo.suporteTecnico.domain.useCase.GetProblemEquipmentsUseCase
import kotlinx.coroutines.launch

class ProblemListViewModel(
    private val getProblemEquipmentsUseCase: GetProblemEquipmentsUseCase
) : ViewModel() {

    private val _problemsEquipments = MutableLiveData<UiState<List<ProblemEquipments>>>()
    val problemsEquipments: LiveData<UiState<List<ProblemEquipments>>> = _problemsEquipments

    private val _equipmentsIsEligibility = MutableLiveData<UiState<EquipmentEligibilityResponse>>()
    val equipmentsIsEligibility: LiveData<UiState<EquipmentEligibilityResponse>> =
        _equipmentsIsEligibility

    fun getProblemEquipments() {
        viewModelScope.launch {

            _problemsEquipments.value = UiState.Loading

            getProblemEquipmentsUseCase.getProblemEquipments()
                .onSuccess { problemEquipments ->
                    _problemsEquipments.value = UiState.Success(problemEquipments)
                }
                .onError {
                    _problemsEquipments.value = UiState.Error(null)
                }
                .onEmpty {
                    _problemsEquipments.value = UiState.Empty
                }
        }
    }

    fun getEligibility(technology: String, code: String) {
        viewModelScope.launch {

            _equipmentsIsEligibility.value = UiState.Loading

            getProblemEquipmentsUseCase.getEligibility(technology, code)
                .onSuccess { equipmentsIsEligibility ->
                    _equipmentsIsEligibility.value = UiState.Success(equipmentsIsEligibility)
                }
                .onError {
                    _equipmentsIsEligibility.value = UiState.Error(null)
                }
                .onEmpty {
                    _equipmentsIsEligibility.value = UiState.Empty
                }
        }
    }
}