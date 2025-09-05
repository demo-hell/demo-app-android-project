package br.com.mobicare.cielo.posVirtual.presentation.accreditation.hire

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.Text
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetFeatureTogglePreferenceUseCase
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.OrdersRequest
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Product
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Required
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.posVirtual.domain.model.BankUI
import br.com.mobicare.cielo.posVirtual.domain.model.TermUI
import br.com.mobicare.cielo.posVirtual.domain.useCase.GetPosVirtualAccreditationBanksUseCase
import br.com.mobicare.cielo.posVirtual.domain.useCase.PostPosVirtualCreateOrderUseCase
import br.com.mobicare.cielo.posVirtual.presentation.accreditation.hire.utils.PosVirtualAccreditationOrdersRequestGenerate
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_INVALID_BANK
import br.com.mobicare.cielo.posVirtual.utils.UIPosVirtualAccreditationCreateOrderState
import br.com.mobicare.cielo.posVirtual.utils.UIPosVirtualAccreditationState
import kotlinx.coroutines.launch
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Agreement as AgreementResponse

class PosVirtualAccreditationHireViewModel(
    private val getPosVirtualAccreditationBanksUseCase: GetPosVirtualAccreditationBanksUseCase,
    private val postPosVirtualCreateOrderUseCase: PostPosVirtualCreateOrderUseCase,
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getFeatureTogglePreferenceUseCase: GetFeatureTogglePreferenceUseCase,
) : ViewModel() {

    private val _loadingBanksState = MutableLiveData<UIPosVirtualAccreditationState>()
    val loadingBanksState: LiveData<UIPosVirtualAccreditationState> get() = _loadingBanksState

    private val _loadingCreateOrderState =
        MutableLiveData<UIPosVirtualAccreditationCreateOrderState>()
    val loadingCreateOrderState: LiveData<UIPosVirtualAccreditationCreateOrderState> get() = _loadingCreateOrderState

    private var _offerID = EMPTY
    private var _agreements: List<AgreementResponse> = emptyList()
    private var _products: List<Product> = emptyList()
    private var _itemsConfigurations: List<String> = emptyList()
    val contractedProducts: List<String>
        get() = _products.map { it.reference.orEmpty() }.filter { it.isNotEmpty() }

    private val _bankSelected = MutableLiveData<BankUI>()
    val bankSelected: LiveData<BankUI> get() = _bankSelected
    val bankNameSelected: String get() = _bankSelected.value?.name.orEmpty()

    private var _banks: List<BankUI> = emptyList()
    val banks: List<BankUI> get() = _banks

    private var _terms: List<TermUI> = emptyList()
    val terms: List<TermUI> get() = _terms

    private var _required: Required? = null
    val required get() = _required

    private var _showBSConfirmTerms: Boolean? = null
    val showBSConfirmTerms get() = _showBSConfirmTerms ?: false
    private var _showBSConfirmTermsTap: Boolean? = null
    val showBSConfirmTermsTap get() = _showBSConfirmTermsTap ?: false
    private var _showBSConfirmTermsSuperLink: Boolean? = null
    val showBSConfirmTermsSuperLink get() = _showBSConfirmTermsSuperLink ?: false
    private var _showBSConfirmTermsPix: Boolean? = null
    val showBSConfirmTermsPix get() = _showBSConfirmTermsPix ?: false

    private var _isEnabledRequiredDataField = false

    fun start(
        offerID: String,
        agreements: List<AgreementResponse>,
        products: List<Product>,
        itemsConfigurations: List<String>,
        required: Required?
    ) {
        _offerID = offerID
        _agreements = agreements
        _products = products
        _itemsConfigurations = itemsConfigurations
        _required = required

        loadStart()
    }

    private fun loadStart() {
        _loadingBanksState.value = UIPosVirtualAccreditationState.ShowLoading

        if (filterTerms()) {
            viewModelScope.launch {
                if ((_loadingBanksState.value is UIPosVirtualAccreditationState.Success).not()) {
                    getFeaturesToggle()
                    getBanks()
                }
            }
        }
    }

    fun setBankSelected(bankUI: BankUI) {
        _bankSelected.value = bankUI
    }

    fun reloadGetBanks() {
        _loadingBanksState.value = UIPosVirtualAccreditationState.ShowLoading
        viewModelScope.launch {
            getBanks()
        }
    }

    private suspend fun getBanks() {
        getPosVirtualAccreditationBanksUseCase.invoke()
            .onSuccess { banksAux ->
                _banks = banksAux.mapIndexed { index, bank ->
                    BankUI(
                        id = index,
                        code = bank.code,
                        name = bank.name,
                        account = bank.accountExt,
                        agency = bank.agencyExt,
                        onlyAgency = bank.agencyNumber
                    )
                }
                _bankSelected.value = _banks.first()
                _loadingBanksState.value = UIPosVirtualAccreditationState.HideLoading
                _loadingBanksState.value = UIPosVirtualAccreditationState.Success
            }
            .onEmpty {
                _loadingBanksState.value = UIPosVirtualAccreditationState.HideLoading
                _loadingBanksState.value = UIPosVirtualAccreditationState.GenericError()
            }
            .onError {
                handleErrorLoadingBanks(it.apiException.newErrorMessage)
            }
    }

    private suspend fun handleErrorLoadingBanks(error: NewErrorMessage) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onHideLoading = {
                _loadingBanksState.value = UIPosVirtualAccreditationState.HideLoading
            },
            onErrorAction = {
                _loadingBanksState.value = UIPosVirtualAccreditationState.HideLoading
                _loadingBanksState.value = UIPosVirtualAccreditationState.GenericError(error)
            })
    }

    private suspend fun getFeaturesToggle() {
        val keys = listOf(
            FeatureTogglePreference.POS_VIRTUAL_BS_CONFIRM_TERM,
            FeatureTogglePreference.POS_VIRTUAL_BS_CONFIRM_TERM_TAP,
            FeatureTogglePreference.POS_VIRTUAL_BS_CONFIRM_TERM_SUPER_LINK,
            FeatureTogglePreference.POS_VIRTUAL_BS_CONFIRM_TERM_PIX,
            FeatureTogglePreference.POS_VIRTUAL_REQUIRED_DATA_FIELD
        )

        keys.forEach { key ->
            getFeatureTogglePreferenceUseCase.invoke(key)
                .onSuccess { result ->
                    when (key) {
                        FeatureTogglePreference.POS_VIRTUAL_BS_CONFIRM_TERM ->
                            _showBSConfirmTerms = result
                        FeatureTogglePreference.POS_VIRTUAL_BS_CONFIRM_TERM_TAP ->
                            _showBSConfirmTermsTap = result
                        FeatureTogglePreference.POS_VIRTUAL_BS_CONFIRM_TERM_SUPER_LINK ->
                            _showBSConfirmTermsSuperLink = result
                        FeatureTogglePreference.POS_VIRTUAL_BS_CONFIRM_TERM_PIX ->
                            _showBSConfirmTermsPix = result
                        FeatureTogglePreference.POS_VIRTUAL_REQUIRED_DATA_FIELD ->
                            _isEnabledRequiredDataField = result
                    }
                }
        }
    }

    private fun filterTerms(): Boolean {
        if (_agreements.isNotEmpty()) {
            _terms = _agreements.flatMap {
                it.terms.orEmpty()
            }.mapIndexed { index, term ->
                TermUI(
                    id = index,
                    label = term.description.orEmpty(),
                    url = term.url.orEmpty()
                )
            }

            return if (_terms.isEmpty()) {
                _loadingBanksState.value = UIPosVirtualAccreditationState.HideLoading
                _loadingBanksState.value = UIPosVirtualAccreditationState.GenericError()
                false
            } else {
                true
            }
        } else {
            _loadingBanksState.value = UIPosVirtualAccreditationState.HideLoading
            _loadingBanksState.value = UIPosVirtualAccreditationState.GenericError()

            return false
        }
    }

    fun toHire() {
        if ((_required != null) && (_isEnabledRequiredDataField)) {
            _loadingCreateOrderState.value =
                UIPosVirtualAccreditationCreateOrderState.OpenRequiredDataField
        } else {
            _loadingCreateOrderState.value =
                UIPosVirtualAccreditationCreateOrderState.GenerateOTPCode
        }
    }

    fun createOrder(otpCode: String) {
        viewModelScope.launch {
            postPosVirtualCreateOrderUseCase.invoke(
                otpCode,
                generateOrderRequest()
            ).onSuccess {
                _loadingCreateOrderState.value =
                    UIPosVirtualAccreditationCreateOrderState.Success(it)
            }.onEmpty {
                _loadingCreateOrderState.value =
                    UIPosVirtualAccreditationCreateOrderState.GenericError()
            }.onError {
                handleErrorCreateOrder(it.apiException.newErrorMessage)
            }
        }
    }

    fun generateOrderRequest(): OrdersRequest {
        return PosVirtualAccreditationOrdersRequestGenerate.generate(
            _offerID,
            null,
            _bankSelected.value,
            _agreements,
            _itemsConfigurations
        )
    }

    private suspend fun handleErrorCreateOrder(error: NewErrorMessage) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onHideLoading = {
                _loadingCreateOrderState.value =
                    UIPosVirtualAccreditationCreateOrderState.HideLoading
            },
            onErrorAction = {
                showError(error)
            }
        )
    }

    private fun showError(error: NewErrorMessage? = null) {
        with(error?.flagErrorCode) {
            when {
                this?.contains(Text.OTP) == true -> _loadingCreateOrderState.value =
                    UIPosVirtualAccreditationCreateOrderState.TokenError(error)
                this == POS_VIRTUAL_ERROR_CODE_INVALID_BANK -> _loadingCreateOrderState.value =
                    UIPosVirtualAccreditationCreateOrderState.InvalidBankError(error)
                else -> _loadingCreateOrderState.value =
                    UIPosVirtualAccreditationCreateOrderState.GenericError(error)
            }
        }
    }

}