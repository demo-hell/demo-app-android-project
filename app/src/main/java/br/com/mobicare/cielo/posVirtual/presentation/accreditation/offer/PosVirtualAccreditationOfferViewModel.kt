package br.com.mobicare.cielo.posVirtual.presentation.accreditation.offer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Agreement
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Brand
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.OfferResponse
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Product
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Required
import br.com.mobicare.cielo.extensions.formatRate
import br.com.mobicare.cielo.posVirtual.domain.useCase.GetPosVirtualAccreditationOffersUseCase
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.AUTOMATIC_RECEIPT
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_ACTIVITY_BRANCH
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_NOT_ELIGIBLE
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_REQUIRED_DATA_FIELD
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.POS_VIRTUAL_ERROR_CODE_SUSPECT_FOR_FRAUD
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.REFERENCE_CODE_CIELO_TAP
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.REFERENCE_CODE_QR_CODE_PIX
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.REFERENCE_CODE_SUPER_LINK
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualConstants.VISA_BRAND_CODE
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualProductTypeEnum
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualTransactionTypeEnum
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualTransactionTypeEnum.CREDIT_IN_CASH
import br.com.mobicare.cielo.posVirtual.utils.PosVirtualTransactionTypeEnum.DEBIT
import br.com.mobicare.cielo.posVirtual.utils.UIPosVirtualAccreditationState
import kotlinx.coroutines.launch

class PosVirtualAccreditationOfferViewModel(
    private val getPosVirtualAccreditationOffersUseCase: GetPosVirtualAccreditationOffersUseCase,
    private val getUserObjUseCase: GetUserObjUseCase
) : ViewModel() {

    private var isNecessaryUpdateData = false

    private val _offerState = MutableLiveData<UIPosVirtualAccreditationState>()
    val offerState: LiveData<UIPosVirtualAccreditationState> get() = _offerState

    private var _offerID = EMPTY
    val offerID get() = _offerID

    private var _agreements: List<Agreement> = emptyList()
    val agreements get() = _agreements

    private var _products: List<Product> = emptyList()
    val products get() = _products

    private var _itemsConfigurations: List<String> = emptyList()
    val itemsConfigurations get() = _itemsConfigurations

    private var _required: Required? = null
    val required get() = _required

    private var _brandsSuperLink: List<Brand> = emptyList()
    private var _brandsTap: List<Brand> = emptyList()
    private var _brandsPix: List<Brand> = emptyList()

    private val _acceptAutomaticReceiverIsChecked = MutableLiveData(true)
    val acceptAutomaticReceiverIsChecked: LiveData<Boolean> get() = _acceptAutomaticReceiverIsChecked

    val isShowCardCieloTap get() = _brandsTap.isNotEmpty()
    val isShowCardSuperLink get() = _brandsSuperLink.isNotEmpty()
    val isShowCardQRCodePix get() = _brandsPix.isNotEmpty()

    private val offerIsNotEmpty get() = isShowCardCieloTap || isShowCardSuperLink || isShowCardQRCodePix

    val creditRateCieloTap
        get() = formatRateUI(
            getPrimaryBrand(
                _brandsTap,
                CREDIT_IN_CASH
            )?.flexibleTermPaymentMDR?.formatRate()
        )

    val debitRateCieloTap
        get() = formatRateUI(
            getPrimaryBrand(
                _brandsTap,
                DEBIT
            )?.flexibleTermPaymentMDR?.formatRate()
        )

    val creditRateSuperLink
        get() = formatRateUI(
            getPrimaryBrand(
                _brandsSuperLink,
                CREDIT_IN_CASH
            )?.flexibleTermPaymentMDR?.formatRate()
        )

    val debitRateSuperLink
        get() = formatRateUI(
            getPrimaryBrand(
                _brandsSuperLink,
                DEBIT
            )?.flexibleTermPaymentMDR?.formatRate()
        )

    val debitRateQRCodePix
        get() = formatRateUI(_brandsPix.firstOrNull()?.conditions?.firstOrNull()?.flexibleTermPaymentMDR?.formatRate())

    private fun formatRateUI(rate: String?) = rate ?: SIMPLE_LINE

    fun getBrands(typeProduct: PosVirtualProductTypeEnum): Array<Brand> {
        return (if (typeProduct == PosVirtualProductTypeEnum.CIELO_TAP) _brandsTap else _brandsSuperLink)
            .toTypedArray()
    }

    val contractedProducts: List<String>
        get() = _products.map { it.reference.orEmpty() }.filter { it.isNotEmpty() }

    fun resume() {
        reloadAccreditationOffer()
    }

    private fun getPrimaryBrand(
        brands: List<Brand>,
        transactionType: PosVirtualTransactionTypeEnum
    ) =
        brands.firstOrNull {
            it.code == VISA_BRAND_CODE
        }?.conditions?.firstOrNull {
            it.type == transactionType.name
        }

    fun reloadAccreditationOffer() {
        _offerState.value = UIPosVirtualAccreditationState.ShowLoading
        viewModelScope.launch {
            getAccreditationOffer()
        }
    }

    private suspend fun getAccreditationOffer() {
        val additionalProduct =
            if (_acceptAutomaticReceiverIsChecked.value == true) AUTOMATIC_RECEIPT else null
        getPosVirtualAccreditationOffersUseCase.invoke(additionalProduct)
            .onSuccess { offerResponse ->
                processOffer(offerResponse)
            }
            .onEmpty {
                setGenericErrorLoadingOffer()
            }
            .onError {
                handleError(it.apiException.newErrorMessage)
            }
    }

    private fun processOffer(offerResponse: OfferResponse) {
        _required = offerResponse.required
        checkIsNecessaryUpdateData()

        offerResponse.offer.let { offer ->
            _offerID = offer?.id.orEmpty()
            _agreements = offer?.agreements.orEmpty()
            _products = offer?.products.orEmpty()
            _itemsConfigurations = offer?.itemsConfigurations.orEmpty()
            filterRates()

            if (offerIsNotEmpty) {
                _offerState.value = UIPosVirtualAccreditationState.HideLoading
                _offerState.value = UIPosVirtualAccreditationState.Success
            } else if (isNecessaryUpdateData) {
                _offerState.value = UIPosVirtualAccreditationState.RequiredDataFieldError()
            } else {
                setGenericErrorLoadingOffer()
            }
        }
    }

    private suspend fun handleError(error: NewErrorMessage) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onHideLoading = {
                _offerState.value = UIPosVirtualAccreditationState.HideLoading
            },
            onErrorAction = {
                errorAction(error)
            }
        )
    }

    private fun errorAction(error: NewErrorMessage) {
        when (error.flagErrorCode) {
            POS_VIRTUAL_ERROR_CODE_NOT_ELIGIBLE, POS_VIRTUAL_ERROR_CODE_ACTIVITY_BRANCH -> {
                _offerState.value = UIPosVirtualAccreditationState.UnavailableError(error)
            }

            POS_VIRTUAL_ERROR_CODE_SUSPECT_FOR_FRAUD -> {
                _offerState.value = UIPosVirtualAccreditationState.SuspectError(error)
            }

            POS_VIRTUAL_ERROR_CODE_REQUIRED_DATA_FIELD -> {
                _offerState.value = UIPosVirtualAccreditationState.RequiredDataFieldError(error)
            }

            else -> setGenericErrorLoadingOffer()
        }
    }

    private fun filterRates() {
        products.forEach { product ->
            if (product.brands.isNullOrEmpty()) {
                setGenericErrorLoadingOffer()
                return
            }
            when (product.reference) {
                REFERENCE_CODE_SUPER_LINK -> _brandsSuperLink = product.brands
                REFERENCE_CODE_CIELO_TAP -> _brandsTap = product.brands
                REFERENCE_CODE_QR_CODE_PIX -> _brandsPix = product.brands
            }
        }
    }

    private fun setGenericErrorLoadingOffer(error: NewErrorMessage? = null) {
        _offerState.value = UIPosVirtualAccreditationState.HideLoading
        _offerState.value = UIPosVirtualAccreditationState.GenericError(error)
    }

    fun checkAutomaticReceiver() {
        _acceptAutomaticReceiverIsChecked.value = _acceptAutomaticReceiverIsChecked.value?.not()
    }

    private fun checkIsNecessaryUpdateData() {
        isNecessaryUpdateData =
            (_required != null) && (_required?.addressFields.isNullOrEmpty().not()
                    || _required?.companyFields.isNullOrEmpty().not()
                    || _required?.individualFields.isNullOrEmpty().not()
                    || _required?.phoneFields.isNullOrEmpty().not())
    }

}