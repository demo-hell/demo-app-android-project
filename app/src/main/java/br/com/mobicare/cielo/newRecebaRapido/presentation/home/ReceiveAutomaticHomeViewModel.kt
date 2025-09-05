package br.com.mobicare.cielo.newRecebaRapido.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.newRecebaRapido.domain.model.CreditOfferItem
import br.com.mobicare.cielo.newRecebaRapido.domain.model.GeneralOfferSummary
import br.com.mobicare.cielo.newRecebaRapido.domain.model.InstallmentOfferItem
import br.com.mobicare.cielo.newRecebaRapido.domain.model.Offer
import br.com.mobicare.cielo.newRecebaRapido.domain.model.mapToCreditOffer
import br.com.mobicare.cielo.newRecebaRapido.domain.model.mapToGeneralOfferSummary
import br.com.mobicare.cielo.newRecebaRapido.domain.model.mapToInstallmentOffer
import br.com.mobicare.cielo.newRecebaRapido.domain.usecase.GetReceiveAutomaticOffersUseCase
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.DAILY
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.HIRED_OFFER_EXISTS_ERROR
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.OFFERS_NOT_FOUND
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.OFFER_NOT_FOUND
import br.com.mobicare.cielo.newRecebaRapido.util.OfferState
import br.com.mobicare.cielo.newRecebaRapido.util.UiStateRAOffers
import kotlinx.coroutines.launch

class ReceiveAutomaticHomeViewModel(
    private val getOffersUseCase: GetReceiveAutomaticOffersUseCase,
    private val user: GetUserObjUseCase
) : ViewModel() {

    private val _receiveAutomaticOffersMutableLiveData =
        MutableLiveData<UiStateRAOffers<GeneralOfferSummary>>()
    val receiveAutomaticOffersMutableLiveData: LiveData<UiStateRAOffers<GeneralOfferSummary>> get() = _receiveAutomaticOffersMutableLiveData

    private val _bothOfferMutableLiveData =
        MutableLiveData<OfferState<Pair<CreditOfferItem, InstallmentOfferItem>>>()
    val bothOfferLiveData: LiveData<OfferState<Pair<CreditOfferItem, InstallmentOfferItem>>> get() = _bothOfferMutableLiveData

    private val _creditOfferMutableLiveData =
        MutableLiveData<OfferState<CreditOfferItem>>()
    val creditOfferLiveData: LiveData<OfferState<CreditOfferItem>> get() = _creditOfferMutableLiveData

    private val _installmentOfferMutableLiveData =
        MutableLiveData<OfferState<InstallmentOfferItem>>()
    val installmentOfferLiveData: LiveData<OfferState<InstallmentOfferItem>> get() = _installmentOfferMutableLiveData

    var offers: List<Offer>? = null
    var isOffer = false

    init {
        getReceiveAutomaticOffers()
    }

    fun getReceiveAutomaticOffers() {
        _receiveAutomaticOffersMutableLiveData.value = UiStateRAOffers.Loading
        viewModelScope.launch {
            getOffersUseCase(DAILY)
                .onSuccess { offers ->
                    _receiveAutomaticOffersMutableLiveData.value = UiStateRAOffers.HideLoading
                    _receiveAutomaticOffersMutableLiveData.value = UiStateRAOffers.Success(
                        offers.mapToGeneralOfferSummary().apply {
                            isOffer = this.validityPeriod.isNullOrEmpty().not()
                        }
                    )

                    val creditOffer = offers.mapToCreditOffer()
                    val installmentOffer = offers.mapToInstallmentOffer()
                    val firstInstallmentOffer = installmentOffer?.installments?.firstOrNull()

                    if (creditOffer != null && firstInstallmentOffer != null) {
                        _bothOfferMutableLiveData.value =
                            OfferState.Show(Pair(creditOffer, firstInstallmentOffer))
                    } else {
                        _bothOfferMutableLiveData.value = OfferState.Hide
                    }

                    _creditOfferMutableLiveData.value = creditOffer?.let {
                        OfferState.Show(it)
                    } ?: OfferState.Hide

                    _installmentOfferMutableLiveData.value = firstInstallmentOffer?.let {
                        OfferState.Show(it)
                    } ?: OfferState.Hide

                    this@ReceiveAutomaticHomeViewModel.offers = offers

                }.onEmpty {
                    _receiveAutomaticOffersMutableLiveData.value = UiStateRAOffers.HideLoading
                    _receiveAutomaticOffersMutableLiveData.value =
                        UiStateRAOffers.Error(R.string.anticipation_error)
                    isOffer = false
                }.onError {
                    handleError(it.apiException.newErrorMessage)
                    isOffer = false
                }
        }
    }

    private suspend fun handleError(error: NewErrorMessage) {
        when (error.flagErrorCode) {
            OFFERS_NOT_FOUND, OFFER_NOT_FOUND -> _receiveAutomaticOffersMutableLiveData.value =
                UiStateRAOffers.OffersNotFound

            else -> {
                newErrorHandler(
                    getUserObjUseCase = user,
                    newErrorMessage = error,
                    onErrorAction = {
                        setError(error)
                    }
                )
            }
        }
    }

    private fun setError(error: NewErrorMessage) {
        if (error.flagErrorCode == HIRED_OFFER_EXISTS_ERROR) {
            _receiveAutomaticOffersMutableLiveData.value = UiStateRAOffers.HiredOfferExists
        } else {
            _receiveAutomaticOffersMutableLiveData.value =
                UiStateRAOffers.Error(R.string.receive_auto_error_message, error)
        }
    }
}