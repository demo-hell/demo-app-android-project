package br.com.mobicare.cielo.simulator.simulation.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.simulator.simulation.domain.model.PaymentType
import br.com.mobicare.cielo.simulator.simulation.domain.model.Product
import br.com.mobicare.cielo.simulator.simulation.domain.model.SimulatorProducts
import br.com.mobicare.cielo.simulator.simulation.domain.usecase.GetSimulationUseCase
import br.com.mobicare.cielo.simulator.simulation.domain.usecase.GetSimulatorProductsUseCase
import br.com.mobicare.cielo.simulator.simulation.presentation.state.UiSimulatorProductState
import br.com.mobicare.cielo.simulator.simulation.presentation.state.UiSimulatorResultState
import kotlinx.coroutines.launch
import java.math.BigDecimal

class SimulatorViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getSimulatorProductsUseCase: GetSimulatorProductsUseCase,
    private val getSimulationUseCase: GetSimulationUseCase
) : ViewModel() {
    private var _simulatorProducts: SimulatorProducts? = null
    val simulatorProducts: SimulatorProducts?
        get() = _simulatorProducts
    private var _simulationValue: BigDecimal? = BigDecimal.ZERO
    val simulationValue: BigDecimal?
        get() = _simulationValue
    private var _simulatorSelectedProduct = MutableLiveData<Product>()
    val simulatorSelectedProduct: LiveData<Product>
        get() = _simulatorSelectedProduct

    val availablePaymentTypes: List<PaymentType>?
        get() = simulatorSelectedProduct.value?.paymentTypes?.distinct()

    private var _simulatorSelectedPaymentType = MutableLiveData<PaymentType?>()
    val simulatorSelectedPaymentType: LiveData<PaymentType?>
        get() = _simulatorSelectedPaymentType
    private val _simulatorProductsState = MutableLiveData<UiSimulatorProductState>()
    val simulatorProductState: LiveData<UiSimulatorProductState> get() = _simulatorProductsState
    private val _simulatorResultState = MutableLiveData<UiSimulatorResultState>()
    val simulatorResultState: LiveData<UiSimulatorResultState> get() = _simulatorResultState

    init {
        getSimulatorProducts()
    }

    fun getSimulatorProducts() {

        _simulatorProductsState.value = UiSimulatorProductState.ShowLoading
        viewModelScope.launch {
            getSimulatorProductsUseCase.invoke().onSuccess {
                _simulatorProducts = it
                _simulatorProductsState.value = UiSimulatorProductState.HideLoading
                _simulatorProductsState.value = UiSimulatorProductState.Success(it)
            }.onEmpty {
                _simulatorProductsState.value = UiSimulatorProductState.Error()
            }.onError {
                val error = it.apiException.newErrorMessage
                newErrorHandler(
                    getUserObjUseCase = getUserObjUseCase,
                    newErrorMessage = error,
                    onErrorAction = {
                        _simulatorProductsState.value = UiSimulatorProductState.Error(
                            error
                        )
                    },
                )

            }
        }
    }

    fun updateValue(simulationValue: BigDecimal?) {
        _simulationValue = simulationValue
    }

    fun updateSelectedBrand(brandCode: String?) {
        _simulatorSelectedProduct.value =
            simulatorProducts?.products?.firstOrNull { it.cardBrandCode == brandCode }
        _simulatorSelectedPaymentType.value = null
    }

    fun updateSelectedPaymentType(productCode: Int?) {
        _simulatorSelectedPaymentType.value =
            availablePaymentTypes?.firstOrNull { it.productCode == productCode }
    }

    fun getSimulation() {
        _simulatorResultState.value = UiSimulatorResultState.ShowLoading
        viewModelScope.launch {
            getSimulationUseCase.invoke(
                productTypeCode = simulatorSelectedPaymentType.value?.productCode,
                fastReceiveIndicator = simulatorSelectedPaymentType.value?.fastRepay == true,
                installmentAmount = null,
                salesValue = simulationValue?.toDouble()
            ).onSuccess {
                _simulatorResultState.value = UiSimulatorResultState.HideLoading
                _simulatorResultState.value = UiSimulatorResultState.Success(it.first())
            }.onEmpty {
                _simulatorResultState.value = UiSimulatorResultState.Error()
            }.onError {
                val error = it.apiException.newErrorMessage
                newErrorHandler(
                    getUserObjUseCase = getUserObjUseCase,
                    newErrorMessage = error,
                    onErrorAction = {
                        _simulatorResultState.value = UiSimulatorResultState.Error(
                            error
                        )
                    },
                )
            }
        }
    }
}
