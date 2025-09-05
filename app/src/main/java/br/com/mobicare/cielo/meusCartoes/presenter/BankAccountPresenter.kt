package br.com.mobicare.cielo.meusCartoes.presenter

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.commons.utils.ResourcesLoader
import br.com.mobicare.cielo.meusCartoes.BankTransactionRepository
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.Bank
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BankTransferRequest
import br.com.mobicare.cielo.meusCartoes.domains.entities.BankAccountType
import br.com.mobicare.cielo.meusCartoes.presentation.ui.CreditCardsContract
import br.com.mobicare.cielo.mfa.EnrollmentResponse
import br.com.mobicare.cielo.mfa.MfaRepository
import br.com.mobicare.cielo.mfa.commons.EnrollmentStatus
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import retrofit2.HttpException
import timber.log.Timber
import java.net.HttpURLConnection

class BankAccountPresenter(var bankAccView: CreditCardsContract.BankAccountView,
                           val bankTransactionRepository: BankTransactionRepository,
                           val mfaRepository: MfaRepository,
                           val uiScheduler: Scheduler, val ioScheduler: Scheduler) :
        CommonPresenter {


    private var compositeDisp = CompositeDisposable()

    fun allAgencies() {

        compositeDisp.add(bankTransactionRepository.allBanks()
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    bankAccView.fillSpinnerBanks(it)
                }, {
                    errorHandler(it)
                }))

    }


    fun toggleAccountType(checkingAccoutType: Boolean) {
        bankAccView.toggleAccountType(checkingAccoutType)
    }

    fun validateEmptyFields(bank: Bank?, agencyNumber: String?,
                            account: String?,
                            accountDigit: String?) {

        if (bank != null) {
            bankAccView.updateEnabledNext(!agencyNumber.isNullOrEmpty() &&
            !account.isNullOrEmpty() && !accountDigit.isNullOrEmpty())
        }
    }


    fun nextTransferStep(selectedBank: Bank,
                         agencyNumber: String,
                         account: String,
                         accountDigit: String,
                         buttonChekingAccountSelected: Boolean) {



        val bankToTransferAccountType = when (buttonChekingAccountSelected) {
            true -> BankAccountType.CHECKING
            else -> BankAccountType.SAVING
        }

        val bankTransferRequest = BankTransferRequest(
                bankCode = selectedBank.code ?: "",
                bankBranch = agencyNumber,
                accountType = bankToTransferAccountType.typeName,
                accountHolderName = "",
                accountHolderDocument = "",
                bankAccount = account,
                bankAccountDigit = accountDigit,
                accountHolderType = "",
                amount = 0.0,
                description = "",
                bankBranchDigit = "0",
                bankName = selectedBank.name ?: ""
        )

        bankAccView.finishStep(bankTransferRequest)
    }

    override fun onResume() {
        if (compositeDisp.isDisposed) {
            compositeDisp = CompositeDisposable()
        }
    }

    override fun onDestroy() {
        compositeDisp.dispose()
    }

    private fun errorHandler(transferError: Throwable?) {

        if (transferError is HttpException) {
            when (transferError.code()) {
                HttpURLConnection.HTTP_UNAUTHORIZED -> {
                    bankAccView.showUnauthorizedAndClose()
                }
                else -> {
                    bankAccView.showUnavailableServer()
                }
            }
        } else {

            bankAccView.showError(ErrorMessage().apply {
                this.message = ResourcesLoader.instance
                        .getString(R.string.text_server_error_message)
                this.title = ResourcesLoader.instance
                        .getString(R.string.text_transfer_error_title)
            })
        }
    }


    fun checkEnrollment() {
        mfaRepository.checkEnrollment(object :
            APICallbackDefault<EnrollmentResponse, String> {

            override fun onError(error: ErrorMessage) {
                Timber.e(error.errorMessage)
                bankAccView.enrollmentError()
            }

            override fun onSuccess(response: EnrollmentResponse) {

                when (response.status) {
                    EnrollmentStatus.NOT_ELIGIBLE.status -> {
                        bankAccView.enrollmentError()
                    }
                    else -> {
                        bankAccView.userEnrollmentEligible()
                    }
                }

            }

        })
    }

}