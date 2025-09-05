package br.com.mobicare.cielo.home.presentation.postecipado.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.home.presentation.postecipado.domain.usecase.GetPostecipadoSummaryUseCase
import br.com.mobicare.cielo.home.presentation.postecipado.utils.PostecipadoUiState
import br.com.mobicare.cielo.home.presentation.postecipado.utils.PostecipadoUiState.Empty
import br.com.mobicare.cielo.home.presentation.postecipado.utils.PostecipadoUiState.Error
import br.com.mobicare.cielo.home.presentation.postecipado.utils.PostecipadoUiState.HideLoading
import br.com.mobicare.cielo.home.presentation.postecipado.utils.PostecipadoUiState.Success
import br.com.mobicare.cielo.taxaPlanos.TAXA_PLANOS_POSTECIPADO
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PostecipadoRentInformationResponse
import kotlinx.coroutines.launch

class PostecipadoHomeSummaryViewModel(
    private val getPostecipadoSummaryUseCase: GetPostecipadoSummaryUseCase,
    private val getUserObjUseCase: GetUserObjUseCase
) : ViewModel() {

    private val _postecipadoUiState = MutableLiveData<PostecipadoUiState<PostecipadoRentInformationResponse>>()
    val postecipadoUiState: LiveData<PostecipadoUiState<PostecipadoRentInformationResponse>> get() = _postecipadoUiState

    fun getPlanInformation(planName: String = TAXA_PLANOS_POSTECIPADO) {
        viewModelScope.launch {
            getPostecipadoSummaryUseCase(planName)
                .onSuccess {
                    _postecipadoUiState.value = HideLoading

                    it.firstOrNull()?.let { itPostecipadoItem ->
                        _postecipadoUiState.value = Success(itPostecipadoItem)
                    } ?: run {
                        _postecipadoUiState.value = Empty
                    }
                }.onError { apiError ->
                    val error = apiError.apiException.newErrorMessage

                    newErrorHandler(
                        getUserObjUseCase = getUserObjUseCase,
                        newErrorMessage = error,
                        onHideLoading = {
                            _postecipadoUiState.postValue(HideLoading)
                        },
                        onErrorAction = {
                            _postecipadoUiState.value = Error(error)
                        }
                    )
                }.onEmpty {
                    _postecipadoUiState.value = HideLoading
                    _postecipadoUiState.value = Empty
                }
        }
    }
}