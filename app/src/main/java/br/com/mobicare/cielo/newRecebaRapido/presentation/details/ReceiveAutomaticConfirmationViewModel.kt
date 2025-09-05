package br.com.mobicare.cielo.newRecebaRapido.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.HTTP_ENHANCE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.EMPTY_VALUE
import br.com.mobicare.cielo.commons.utils.getErrorMessage
import br.com.mobicare.cielo.extensions.orZero
import br.com.mobicare.cielo.newRecebaRapido.data.model.ReceiveAutomaticContractRequest
import br.com.mobicare.cielo.newRecebaRapido.domain.model.CreditOfferItem
import br.com.mobicare.cielo.newRecebaRapido.domain.model.GeneralOfferSummary
import br.com.mobicare.cielo.newRecebaRapido.domain.model.InstallmentOfferItem
import br.com.mobicare.cielo.newRecebaRapido.domain.model.Offer
import br.com.mobicare.cielo.newRecebaRapido.domain.model.OfferSummary
import br.com.mobicare.cielo.newRecebaRapido.domain.model.SelectedPlanSummary
import br.com.mobicare.cielo.newRecebaRapido.domain.model.mapToOfferSummary
import br.com.mobicare.cielo.newRecebaRapido.domain.usecase.ContractReceiveAutomaticOfferUseCase
import br.com.mobicare.cielo.newRecebaRapido.domain.usecase.GetReceiveAutomaticOffersUseCase
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.BOTH
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.DAILY
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.HIRED_OFFER_EXISTS_ERROR_RA
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.MONTHLY
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.VALIDITY_TYPE_FIXED_DATE
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.VALIDITY_TYPE_MONTHS
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.WEEKLY
import br.com.mobicare.cielo.newRecebaRapido.util.OfferState
import br.com.mobicare.cielo.newRecebaRapido.util.OfferValidityState
import br.com.mobicare.cielo.newRecebaRapido.util.PlanState
import br.com.mobicare.cielo.newRecebaRapido.util.UiStateRAOContract
import br.com.mobicare.cielo.newRecebaRapido.util.UiStateRAODetailsOffers
import kotlinx.coroutines.launch

class ReceiveAutomaticConfirmationViewModel(
    private val contractOffersUseCase: ContractReceiveAutomaticOfferUseCase,
    private val getOffersUseCase: GetReceiveAutomaticOffersUseCase,
    private val user: GetUserObjUseCase
) : ViewModel() {

    private val receiveAutomaticOffersValidityMutableLiveData =
        MutableLiveData<OfferValidityState>()
    val receiveAutomaticOffersValidityLiveData: LiveData<OfferValidityState>
        get() =
            receiveAutomaticOffersValidityMutableLiveData

    private val receiveAutomaticOffersBrandMutableLiveData =
        MutableLiveData<String>()
    val receiveAutomaticOffersBrandLiveData: LiveData<String>
        get() =
            receiveAutomaticOffersBrandMutableLiveData

    private val receiveAutomaticOffersPostValidityMutableLiveData =
        MutableLiveData<UiStateRAODetailsOffers<List<OfferSummary>>>()
    val receiveAutomaticOffersPostValidityLiveData: LiveData<UiStateRAODetailsOffers<List<OfferSummary>>> get() =
        receiveAutomaticOffersPostValidityMutableLiveData

    private val _receiveAutomaticOffersDetailsContractLiveData =
        MutableLiveData<UiStateRAOContract>()
    val receiveAutomaticOffersDetailsContractLiveData: LiveData<UiStateRAOContract> get() =
        _receiveAutomaticOffersDetailsContractLiveData

    private val creditOfferMutableLiveData = MutableLiveData<OfferState<CreditOfferItem>>()
    val creditOfferLiveData: LiveData<OfferState<CreditOfferItem>> get() = creditOfferMutableLiveData

    private val installmentOfferMutableLiveData =
        MutableLiveData<OfferState<InstallmentOfferItem>>()
    val installmentOfferLiveData: LiveData<OfferState<InstallmentOfferItem>> get() =
        installmentOfferMutableLiveData

    private val planSummaryMutableLiveData =
        MutableLiveData<PlanState>()
    val planSummaryLiveData: LiveData<PlanState> get() = planSummaryMutableLiveData


    var offers: List<Offer>? = null
    var typeTransactionSelected: String = EMPTY_VALUE
    var periodicitySelected: String = EMPTY_VALUE
    var weekDaySelected: String? = null
    var monthDaySelected: Int? = null

    fun setReceiveAutomaticOffers(
        offers: List<Offer>,
        offerSummary: GeneralOfferSummary,
        creditOffer: CreditOfferItem?,
        installmentOffer: InstallmentOfferItem?,
        planSummary: SelectedPlanSummary
    ) {
        this@ReceiveAutomaticConfirmationViewModel.offers = offers
        this@ReceiveAutomaticConfirmationViewModel.typeTransactionSelected = planSummary.typeTransactionSelected
        this@ReceiveAutomaticConfirmationViewModel.periodicitySelected = planSummary.periodicitySelected
        this@ReceiveAutomaticConfirmationViewModel.weekDaySelected = planSummary.weekDaySelected
        this@ReceiveAutomaticConfirmationViewModel.monthDaySelected = planSummary.monthDaySelected

        when (typeTransactionSelected) {
            BOTH -> {
                creditOfferMutableLiveData.value = creditOffer?.let { OfferState.Show(it) } ?: OfferState.Hide
                installmentOfferMutableLiveData.value =
                    installmentOffer?.let { OfferState.Show(it) } ?: OfferState.Hide
            }

            ConstantsReceiveAutomatic.CREDIT -> {
                creditOfferMutableLiveData.value = creditOffer?.let { OfferState.Show(it) } ?: OfferState.Hide
                installmentOfferMutableLiveData.value = OfferState.Hide
            }

            ConstantsReceiveAutomatic.INSTALLMENT -> {
                installmentOfferMutableLiveData.value =
                    installmentOffer?.let { OfferState.Show(it) } ?: OfferState.Hide
                creditOfferMutableLiveData.value = OfferState.Hide
            }
        }

        planSummaryMutableLiveData.value = when (planSummary.periodicitySelected) {
            DAILY -> PlanState.Daily
            WEEKLY -> PlanState.Weekly(planSummary.weekDaySelected.orEmpty())
            MONTHLY -> PlanState.Monthly(planSummary.monthDaySelected.orZero)
            else -> PlanState.Empty
        }

        receiveAutomaticOffersValidityMutableLiveData.value = when(offerSummary.validityPeriodType) {
            VALIDITY_TYPE_MONTHS -> offerSummary.validityPeriod?.toInt()
                ?.let { OfferValidityState.Months(it) }
            VALIDITY_TYPE_FIXED_DATE -> offerSummary.validityPeriod?.let {
                OfferValidityState.FixedDate(
                    it
                )
            }
            else -> OfferValidityState.Empty
        }

        receiveAutomaticOffersBrandMutableLiveData.value = offerSummary.referenceBrand.orEmpty()
    }

    fun contractingReceiveAutomaticOffers() {
        _receiveAutomaticOffersDetailsContractLiveData.value = UiStateRAOContract.Loading
        viewModelScope.launch {
            contractOffersUseCase(
                ReceiveAutomaticContractRequest(
                    settlementTerm = if (periodicitySelected == MONTHLY) monthDaySelected else null,
                    dayOfTheWeek = if (periodicitySelected == WEEKLY) weekDaySelected else null,
                    customFastRepayPeriodicity = periodicitySelected,
                    customFastRepayContractType = typeTransactionSelected
                )
            ).onSuccess {
                    _receiveAutomaticOffersDetailsContractLiveData.value =
                        UiStateRAOContract.HideLoading
                    _receiveAutomaticOffersDetailsContractLiveData.value =
                        UiStateRAOContract.Success
                }.onEmpty {
                    _receiveAutomaticOffersDetailsContractLiveData.value =
                        UiStateRAOContract.HideLoading
                    _receiveAutomaticOffersDetailsContractLiveData.value = UiStateRAOContract.Empty
                }.onError {
                    _receiveAutomaticOffersDetailsContractLiveData.value =
                        UiStateRAOContract.HideLoading
                    val error = it.apiException.newErrorMessage
                    val message =
                        if (error.httpCode == HTTP_ENHANCE && error.flagErrorCode == HIRED_OFFER_EXISTS_ERROR_RA) {
                            error.message
                        } else {
                            genericErrorMessage(error)
                        }
                handleContractError(error, message)
                }
        }
    }

    private suspend fun handleContractError(
        error: NewErrorMessage,
        message: Any
    ) {
        newErrorHandler(getUserObjUseCase = user,
            newErrorMessage = error,
            onErrorAction = {
                _receiveAutomaticOffersDetailsContractLiveData.value =
                    UiStateRAOContract.Error(message, error)
            })
    }

    private fun genericErrorMessage(error: NewErrorMessage) =
        error.getErrorMessage(
            R.string.anticipation_error
        )

    fun getPostValidityOffers() {
        receiveAutomaticOffersPostValidityMutableLiveData.value = UiStateRAODetailsOffers.Loading
        viewModelScope.launch {
            getOffersUseCase.invoke(periodicity = periodicitySelected, nextValidityPeriod = true)
                .onSuccess { offers ->
                    receiveAutomaticOffersPostValidityMutableLiveData.value =
                        UiStateRAODetailsOffers.HideLoading
                    receiveAutomaticOffersPostValidityMutableLiveData.value = UiStateRAODetailsOffers.Success(offers.mapToOfferSummary())
                }.onEmpty {
                    setErrorState()
                }.onError {
                    handleError(it.apiException.newErrorMessage)
                }
        }
    }

    private suspend fun handleError(error: NewErrorMessage) {
        newErrorHandler(
            getUserObjUseCase = user,
            newErrorMessage = error,
            onErrorAction = {
                setErrorState(error)
            }
        )
    }

    private fun setErrorState(error: NewErrorMessage? = null) {
        receiveAutomaticOffersPostValidityMutableLiveData.value =
            UiStateRAODetailsOffers.HideLoading
        receiveAutomaticOffersPostValidityMutableLiveData.value =
            UiStateRAODetailsOffers.Error(R.string.anticipation_error, error)
    }
}