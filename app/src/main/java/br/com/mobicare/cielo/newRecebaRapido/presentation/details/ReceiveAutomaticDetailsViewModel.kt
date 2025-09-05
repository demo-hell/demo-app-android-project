package br.com.mobicare.cielo.newRecebaRapido.presentation.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.enums.DaysOfWeek
import br.com.mobicare.cielo.commons.enums.DiasDaSemana
import br.com.mobicare.cielo.commons.utils.EMPTY_VALUE
import br.com.mobicare.cielo.newRecebaRapido.domain.model.CreditOfferItem
import br.com.mobicare.cielo.newRecebaRapido.domain.model.GeneralOfferSummary
import br.com.mobicare.cielo.newRecebaRapido.domain.model.InstallmentOfferItem
import br.com.mobicare.cielo.newRecebaRapido.domain.model.Offer
import br.com.mobicare.cielo.newRecebaRapido.domain.model.mapToCreditOffer
import br.com.mobicare.cielo.newRecebaRapido.domain.model.mapToGeneralOfferSummary
import br.com.mobicare.cielo.newRecebaRapido.domain.model.mapToInstallmentOffer
import br.com.mobicare.cielo.newRecebaRapido.domain.usecase.GetReceiveAutomaticOffersUseCase
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.BOTH
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.DAILY
import br.com.mobicare.cielo.newRecebaRapido.util.ConstantsReceiveAutomatic.ONE_DAY_SELECT
import br.com.mobicare.cielo.newRecebaRapido.util.OfferState
import br.com.mobicare.cielo.newRecebaRapido.util.UiStateRAODetailsOffers
import kotlinx.coroutines.launch

class ReceiveAutomaticDetailsViewModel(
    private val getOffersUseCase: GetReceiveAutomaticOffersUseCase,
    private val user: GetUserObjUseCase
) : ViewModel() {

    private val receiveAutomaticOffersDetailsMutableLiveData =
        MutableLiveData<UiStateRAODetailsOffers<GeneralOfferSummary>>()
    val receiveAutomaticOffersDetailsLiveData: LiveData<UiStateRAODetailsOffers<GeneralOfferSummary>> get() = receiveAutomaticOffersDetailsMutableLiveData

    private val creditOfferMutableLiveData =
        MutableLiveData<OfferState<CreditOfferItem>>()
    val creditOfferLiveData: LiveData<OfferState<CreditOfferItem>> get() = creditOfferMutableLiveData

    private val installmentOfferMutableLiveData =
        MutableLiveData<OfferState<InstallmentOfferItem>>()
    val installmentOfferLiveData: LiveData<OfferState<InstallmentOfferItem>> get() = installmentOfferMutableLiveData

    var title: String = EMPTY_VALUE
    var titleDescription: String = EMPTY_VALUE
    var typeTransactionSelected: String = BOTH
    var periodicitySelected: String = DAILY
    var weekDaySelected: String = DaysOfWeek.MONDAY.day
    var monthDaySelected: Int = ONE
    var periodicityBottomSelected: String = ONE_DAY_SELECT
    var weekDayBottomSelected: String = DiasDaSemana.SEGUNDA.dia

    var offers: List<Offer>? = null
        private set

    var offerSummary: GeneralOfferSummary? = null
    var installmentOffer: InstallmentOfferItem? = null
    var creditOffer: CreditOfferItem? = null

    fun setInitOffer(offers: List<Offer>) {
        if(this.offers == null) {
            updateOffers(offers)
        }
    }

    fun getReceiveAutomaticOffers() {
        receiveAutomaticOffersDetailsMutableLiveData.value = UiStateRAODetailsOffers.Loading
        viewModelScope.launch {
            getOffersUseCase(periodicitySelected)
                .onSuccess { offers ->
                    receiveAutomaticOffersDetailsMutableLiveData.value =
                        UiStateRAODetailsOffers.HideLoading

                    updateOffers(offers)

                }.onEmpty {
                    receiveAutomaticOffersDetailsMutableLiveData.value =
                        UiStateRAODetailsOffers.HideLoading
                    receiveAutomaticOffersDetailsMutableLiveData.value =
                        UiStateRAODetailsOffers.Error(R.string.anticipation_error)
                    this@ReceiveAutomaticDetailsViewModel.offers = null
                }.onError {
                    handleError(it.apiException.newErrorMessage)
                    this@ReceiveAutomaticDetailsViewModel.offers = null
                }
        }
    }

    private fun updateOffers(offers: List<Offer>) {
        val creditOffer = offers.mapToCreditOffer()
        val installmentOffer = offers.mapToInstallmentOffer()
        val firstInstallmentOffer = installmentOffer?.installments?.firstOrNull()
        when (typeTransactionSelected) {
            BOTH -> {
                creditOfferMutableLiveData.value =
                    creditOffer?.let { OfferState.Show(it) }
                installmentOfferMutableLiveData.value =
                    firstInstallmentOffer?.let { OfferState.Show(it) }
            }

            ConstantsReceiveAutomatic.CREDIT -> {
                creditOfferMutableLiveData.value =
                    creditOffer?.let { OfferState.Show(it) } ?: OfferState.Hide
                installmentOfferMutableLiveData.value = OfferState.Hide
            }

            ConstantsReceiveAutomatic.INSTALLMENT -> {
                installmentOfferMutableLiveData.value =
                    firstInstallmentOffer?.let { OfferState.Show(it) } ?: OfferState.Hide
                creditOfferMutableLiveData.value = OfferState.Hide
            }
        }

        val offerSummary = offers.mapToGeneralOfferSummary()
        receiveAutomaticOffersDetailsMutableLiveData.value =
            UiStateRAODetailsOffers.Success(offerSummary)

        this@ReceiveAutomaticDetailsViewModel.offers = offers
        this@ReceiveAutomaticDetailsViewModel.offerSummary = offerSummary
        this@ReceiveAutomaticDetailsViewModel.creditOffer = creditOffer
        this@ReceiveAutomaticDetailsViewModel.installmentOffer = firstInstallmentOffer
    }

    private suspend fun handleError(error: NewErrorMessage) {
        newErrorHandler(
            getUserObjUseCase = user,
            newErrorMessage = error,
            onErrorAction = {
                receiveAutomaticOffersDetailsMutableLiveData.value =
                    UiStateRAODetailsOffers.Error(R.string.receive_auto_error_message)
            }
        )
    }
}