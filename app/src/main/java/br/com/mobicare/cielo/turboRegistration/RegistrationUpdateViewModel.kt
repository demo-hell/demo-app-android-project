package br.com.mobicare.cielo.turboRegistration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ONE
import br.com.mobicare.cielo.commons.constants.DASH
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.ActionErrorTypeEnum
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.CieloAPIException
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.turboRegistration.data.model.request.AddressRequest
import br.com.mobicare.cielo.turboRegistration.data.model.request.BillingRequest
import br.com.mobicare.cielo.turboRegistration.data.model.request.BusinessUpdateRequest
import br.com.mobicare.cielo.turboRegistration.data.model.request.DestinationRequest
import br.com.mobicare.cielo.turboRegistration.data.model.request.PaymentAccountRequest
import br.com.mobicare.cielo.turboRegistration.data.model.request.PurposeAddressRequest
import br.com.mobicare.cielo.turboRegistration.domain.model.Address
import br.com.mobicare.cielo.turboRegistration.domain.model.Operation
import br.com.mobicare.cielo.turboRegistration.domain.usecase.GetAddressByCepUseCase
import br.com.mobicare.cielo.turboRegistration.domain.usecase.RegisterNewAccountUseCase
import br.com.mobicare.cielo.turboRegistration.domain.usecase.UpdateAddressUseCase
import br.com.mobicare.cielo.turboRegistration.domain.usecase.UpdateBusinessSectorUseCase
import br.com.mobicare.cielo.turboRegistration.domain.usecase.UpdateMonthlyIncomeUseCase
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationResource
import br.com.mobicare.cielo.turboRegistration.utils.RegistrationStepError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class RegistrationUpdateViewModel(
    private val registerNewAccountUseCase: RegisterNewAccountUseCase,
    private val updateAddressUseCase: UpdateAddressUseCase,
    private val updateBusinessSectorUseCase: UpdateBusinessSectorUseCase,
    private val updateIncomeUseCase: UpdateMonthlyIncomeUseCase,
    private val getAddressByCepUseCase: GetAddressByCepUseCase
) : ViewModel() {

    var addressRequest: AddressRequest? = null
    private var billingRequest: BillingRequest? = null
    private var businessRequest: BusinessUpdateRequest? = null
    private var paymentAccountRequest: PaymentAccountRequest? = null
    private var addressId: String = ZERO.toString()
    private var operation: Operation? = null

    private var _updateResult = MutableLiveData<RegistrationResource<Unit>>()
    val updateResult: LiveData<RegistrationResource<Unit>> get() = _updateResult

    private val _addressLiveData = MutableLiveData<RegistrationResource<Address>>()
    val addressLiveData: LiveData<RegistrationResource<Address>> get() = _addressLiveData
    private var lastZipCode: String? = null

    private var _updateStep: MutableStateFlow<Int> = MutableStateFlow(-1)
    val updateStep get() = _updateStep.asStateFlow()

    fun getAddressByCep(cep: String) {
        viewModelScope.launch {
            if (cep != lastZipCode) {
                lastZipCode = cep
                addressRequest = null
                _addressLiveData.value = RegistrationResource.Loading
                getAddressByCepUseCase(cep).onSuccess {
                    _addressLiveData.value = RegistrationResource.Success(it)
                }.onError {
                    _addressLiveData.value = RegistrationResource.Error(it.apiException)
                }.onEmpty {
                    _addressLiveData.value = RegistrationResource.Empty
                }
            }
        }
    }

    fun updateFromStep(step: Int) {
        viewModelScope.launch {
            _updateResult.value = RegistrationResource.Loading
            _updateStep.value = step
            updateStep.collect {
                when (it) {
                    RegistrationStepError.UNDEFINED.ordinal -> _updateStep.value = ONE
                    RegistrationStepError.ADDRESS.ordinal -> doAddressCall {
                        _updateStep.value = RegistrationStepError.MONTHLY_INCOME.ordinal
                    }

                    RegistrationStepError.MONTHLY_INCOME.ordinal -> doUpdateIncomeCall {
                        _updateStep.value =
                            if (UserPreferences.getInstance().isLegalEntity) {
                                RegistrationStepError.BANK.ordinal
                            } else {
                                RegistrationStepError.BUSINESS_SECTOR.ordinal
                            }
                    }

                    RegistrationStepError.BUSINESS_SECTOR.ordinal -> doBusinessSectorCall {
                        _updateStep.value = RegistrationStepError.BANK.ordinal
                    }

                    RegistrationStepError.BANK.ordinal -> doRegisterAccountCall {
                        _updateResult.value = RegistrationResource.Success(Unit)
                    }
                }
            }
        }
    }

    private fun setupError(
        stepError: RegistrationStepError,
        apiException: CieloAPIException = CieloAPIException(actionErrorType = ActionErrorTypeEnum.HTTP_ERROR)
    ) {
        UserPreferences.getInstance().setTurboRegistrationErrorStep(stepError.ordinal)
        _updateResult.value = RegistrationResource.Error(apiException)
    }

    private suspend fun doAddressCall(cb: (CieloDataResult.Empty) -> Unit) {
        addressRequest?.let {
            updateAddressUseCase(
                if (UserPreferences.getInstance().isLegalEntity) CREDENCIAMENTO_PJ else addressId,
                it.copy(number = it.number?.ifBlank { YES_NO } ?: YES_NO))
                .onEmpty { addressResult ->
                    if (addressResult.code != ZERO) {
                        cb(addressResult)
                    } else {
                        setupError(RegistrationStepError.ADDRESS)
                    }
                }.onError {
                    setupError(RegistrationStepError.ADDRESS, it.apiException)
                }
        } ?: run {
            setupError(RegistrationStepError.ADDRESS)
        }
    }


    private suspend fun doUpdateIncomeCall(cb: (CieloDataResult.Empty) -> Unit) {
        billingRequest?.let {
            updateIncomeUseCase(it)
                .onEmpty { billingResult ->
                    if (billingResult.code != ZERO) {
                        cb(billingResult)
                    } else {
                        setupError(RegistrationStepError.MONTHLY_INCOME)
                    }
                }.onError {
                    setupError(RegistrationStepError.MONTHLY_INCOME, it.apiException)
                }
        } ?: run {
            setupError(RegistrationStepError.MONTHLY_INCOME)
        }
    }

    private suspend fun doBusinessSectorCall(cb: (CieloDataResult.Empty) -> Unit) {
        businessRequest?.let {
            updateBusinessSectorUseCase(it)
                .onEmpty { bussinessResult ->
                    if (bussinessResult.code != ZERO) {
                        cb(bussinessResult)
                    } else {
                        setupError(RegistrationStepError.BUSINESS_SECTOR)
                    }
                }.onError {
                    setupError(RegistrationStepError.BUSINESS_SECTOR, it.apiException)
                }
        } ?: run {
            setupError(RegistrationStepError.BUSINESS_SECTOR)
        }
    }

    private suspend fun doRegisterAccountCall(cb: (CieloDataResult.Empty) -> Unit) {
        paymentAccountRequest?.let {
            registerNewAccountUseCase(it)
                .onEmpty { paymentResult ->
                    if (paymentResult.code != ZERO) {
                        UserPreferences.getInstance().deleteTurboRegistrationErrorStep()
                        cb(paymentResult)
                    } else {
                        setupError(RegistrationStepError.BANK)
                    }
                }.onError {
                    setupError(RegistrationStepError.BANK, it.apiException)
                }
        } ?: run {
            setupError(RegistrationStepError.BANK)
        }
    }

    fun setAddress(
        cep: String,
        address: String,
        complement: String,
        number: String,
        neighborhood: String,
        city: String,
        state: String
    ) {
        addressRequest = AddressRequest(
            streetAddress = address,
            streetAddress2 = complement.ifBlank { EMPTY },
            neighborhood = neighborhood,
            city = city,
            state = state,
            zipCode = cep.replace(DASH, EMPTY),
            number = number,
            country = BR,
            purposeAddress = listOf(PurposeAddressRequest(type = FOUR))
        )
    }

    fun setBilling(value: BigDecimal) {
        billingRequest = BillingRequest(value)
    }

    fun setBusinessSector(code: Int?) {
        businessRequest = BusinessUpdateRequest(
            code
        )
    }

    fun getBusinessSector(): BusinessUpdateRequest? {
        return businessRequest
    }

    fun setAddressId(id: String) {
        addressId = id
    }

    fun getPaymentAccount(): PaymentAccountRequest? {
        return paymentAccountRequest
    }

    fun setPaymentAccount(
        account: String,
        agency: String,
        accountDigit: String,
        bankCode: String,
        selectedOperation: Operation? = null,
        isSavingsAccount: Boolean = false
    ) {
        paymentAccountRequest = PaymentAccountRequest(
            destination = DestinationRequest(
                code = bankCode,
                agency = agency,
                account = account,
                accountDigit = accountDigit,
                operationBank = selectedOperation?.value,
                savingsAccount = isSavingsAccount
            )
        )
    }

    fun saveOperation(operationSelected: Operation?) {
        operation = operationSelected
    }

    fun getOperation(): Operation? {
        return operation
    }

    companion object {
        const val CREDENCIAMENTO_PJ = "credenciamento_pj"
        const val YES_NO = "S/N"
        const val BR = "BR"
    }
}