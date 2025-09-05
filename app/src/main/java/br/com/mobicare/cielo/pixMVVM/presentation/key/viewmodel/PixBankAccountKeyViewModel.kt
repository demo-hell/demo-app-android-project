package br.com.mobicare.cielo.pixMVVM.presentation.key.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.errorhandler.NewErrorMessage
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.newErrorHandler
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onEmpty
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onError
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.onSuccess
import br.com.mobicare.cielo.commons.domain.useCase.GetUserObjUseCase
import br.com.mobicare.cielo.commons.utils.CNPJ_MASK_COMPLETE_FORMAT
import br.com.mobicare.cielo.commons.utils.CPF_MASK_FORMAT
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.unmask
import br.com.mobicare.cielo.pixMVVM.domain.model.PixTransferBank
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferBanksUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.key.enums.PixBankAccountType
import br.com.mobicare.cielo.pixMVVM.presentation.key.enums.PixBeneficiaryType
import br.com.mobicare.cielo.pixMVVM.presentation.key.models.PixBankAccountStore
import br.com.mobicare.cielo.pixMVVM.presentation.key.utils.PixTransferBanksUiState
import kotlinx.coroutines.launch

class PixBankAccountKeyViewModel(
    private val getUserObjUseCase: GetUserObjUseCase,
    private val getTransferBanksUseCase: GetPixTransferBanksUseCase
) : ViewModel() {

    private val _transferBanksUiState = MutableLiveData<PixTransferBanksUiState>()
    val transferBanksUiState: LiveData<PixTransferBanksUiState> = _transferBanksUiState

    private var _bankAccount = PixBankAccountStore()
    val bankAccount get() = _bankAccount

    private var reloadAttemptCounter = ZERO

    val isTransferBanksLoaded get() = transferBanksUiState.value is PixTransferBanksUiState.Success

    fun setSelectedBank(bank: PixTransferBank?) {
        _bankAccount = _bankAccount.copy(bank = bank)
    }

    fun setSelectedAccountType(accountType: PixBankAccountType?) {
        _bankAccount = _bankAccount.copy(bankAccountType = accountType)
    }

    fun setBankAccountData(branchNumber: String, accountNumber: String, accountDigit: String) {
        _bankAccount = _bankAccount.copy(
            bankBranchNumber = branchNumber.trim(),
            bankAccountNumber = accountNumber.trim(),
            bankAccountDigit = accountDigit.trim()
        )
    }

    fun setDocument(documentNumber: String) {
        val beneficiaryType = getBeneficiaryType(documentNumber)

        _bankAccount = _bankAccount.copy(
            beneficiaryType = beneficiaryType,
            documentNumber = getExtractedDocument(documentNumber, beneficiaryType)
        )
    }

    fun setRecipient(name: String) {
        _bankAccount = _bankAccount.copy(recipientName = name.trim())
    }

    fun getTransferBanks() {
        viewModelScope.launch {
            setState(PixTransferBanksUiState.Loading)

            getTransferBanksUseCase()
                .onSuccess {
                    setState(PixTransferBanksUiState.Success(it))
                }.onError {
                    handleError(it.apiException.newErrorMessage)
                }.onEmpty {
                    setErrorState()
                }
        }
    }

    private suspend fun handleError(error: NewErrorMessage) {
        newErrorHandler(
            getUserObjUseCase = getUserObjUseCase,
            newErrorMessage = error,
            onHideLoading = { setState(PixTransferBanksUiState.HideLoading) },
            onErrorAction = { setErrorState() }
        )
    }

    private fun setErrorState() {
        setState(
            if (++reloadAttemptCounter > THREE) {
                PixTransferBanksUiState.UnavailableServiceError
            } else {
                PixTransferBanksUiState.UnableToFetchBankListError
            }
        )
    }

    private fun setState(state: PixTransferBanksUiState) {
        _transferBanksUiState.postValue(state)
    }

    private fun getBeneficiaryType(document: String) =
        if (ValidationUtils.isCPF(document)) PixBeneficiaryType.CPF else PixBeneficiaryType.CNPJ

    private fun getExtractedDocument(document: String, beneficiaryType: PixBeneficiaryType) =
        document.unmask(
            if (beneficiaryType == PixBeneficiaryType.CPF) {
                CPF_MASK_FORMAT
            } else {
                CNPJ_MASK_COMPLETE_FORMAT
            }
        )

}