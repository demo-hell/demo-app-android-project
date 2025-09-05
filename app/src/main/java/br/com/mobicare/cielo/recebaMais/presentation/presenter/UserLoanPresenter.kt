package br.com.mobicare.cielo.recebaMais.presentation.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.data.utils.RetrofitException
import br.com.mobicare.cielo.recebaMais.managers.UserLoanRepository
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import java.net.HttpURLConnection

class UserLoanPresenter(private val userLoanRepository: UserLoanRepository,
                        private val userLoanView: UserLoanContract.View) : UserLoanContract.Presenter {

    private var compositeDisposable = CompositeDisposable()
    var uiScheduler: Scheduler? = AndroidSchedulers.mainThread()
    var ioScheduler: Scheduler? = Schedulers.io()

    companion object {
        const val PENDING_STATUS = "PENDING"
        const val CONTRACTED_STATUS = "CONTRACTED"
        const val ERROR_STATUS = "ERROR"
        const val INACTIVE_STATUS = "INACTIVE"
        const val UNKNOWN_STATUS = "UNKNOWN"
        const val ERROR_STATUS_LOAN_REFUSED = "ECO_CADASTRO_INTERNAL_ERROR"
        const val CREDIT_NOT_ALLOWED = "CREDIT_NOT_ALLOWED"
        const val CONTRACT_NOT_EFFECTED = "CONTRACT_NOT_EFFECTED"
        const val CONTRACT_IN_PROGRESS = "CONTRACT_IN_PROGRESS"
        const val EMPTY = ""
        const val BUSINESS_ERROR_STATUS_CODE = 420
    }

    fun listOffers(userToken: String) {
        compositeDisposable.add(userLoanRepository.offers(userToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ offerSetResponse ->
                    if (userLoanView is UserLoanContract.View.Banner) {
                        userLoanView.availableOffersToShow(offerSetResponse.offers.first())
                    }

                    if (userLoanView is UserLoanContract.View.UserLoanScreen) {
                        if (offerSetResponse.offers.isNotEmpty()) {
                            userLoanView.showSimulationWithResponseOffer(offerSetResponse
                                    ?.offers?.first())
                        } else {
                            userLoanView.showNotApproved()
                        }
                    }
                }, {
                    if (userLoanView is UserLoanContract.View.Banner)
                        userLoanView.availableOffersToShow(null)
                }))
    }

    fun fetchContracts(isUserFromBanner: Boolean) {
        if (userLoanView is UserLoanContract.View.UserLoanScreen) {
            if (!isUserFromBanner) {
                compositeDisposable.add(userLoanRepository
                        .fetchContracts(UserPreferences.getInstance().token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ contractsResponse ->
                            when {
                                (contractsResponse.contracts.isNullOrEmpty()) -> {
                                    userLoanView.showSimulation()
                                }
                                else -> {
                                    userLoanView.showPendingContract(contractsResponse.contracts)
                                }
                            }

                        }, {
                            if (it is RetrofitException) {
                                when (it.httpStatus) {
                                    HttpURLConnection.HTTP_NOT_FOUND -> {
                                        callOffers()
                                    }
                                    BUSINESS_ERROR_STATUS_CODE -> {
                                        val errorMessage = APIUtils.convertToErro(it)
                                        when (errorMessage.errorCode) {
                                            ERROR_STATUS_LOAN_REFUSED -> {
                                                //erro cadastral na contratacao
                                                userLoanView.hideLoading()
                                                userLoanView.showContractInternalError()
                                            }
                                            CREDIT_NOT_ALLOWED -> {
                                                //contratacao barrada devido politica de credito
                                                userLoanView.hideLoading()
                                                userLoanView.showErrorHandler(errorMessage)
                                            }
                                            CONTRACT_IN_PROGRESS -> {
                                                //ja existe um contrato em andamento
                                                userLoanView.hideLoading()
                                                userLoanView.showErrorHandler(errorMessage)
                                            }
                                            CONTRACT_NOT_EFFECTED -> {
                                                //erro na contratacao anterior
                                                userLoanView.hideLoading()
                                                userLoanView.showContractErrorHandler(errorMessage)
                                            }
                                            else -> {
                                                userLoanView.showSimulation()
                                            }
                                        }
                                    }
                                    else -> {
                                        //erro generico
                                        userLoanView.hideLoading()
                                        userLoanView.showNetworkError()
                                    }
                                }
                            }
                        }))
            } else {
                userLoanView.showSimulation()
            }
        }
    }

    private fun callOffers() {

        if (userLoanView is UserLoanContract.View.UserLoanScreen) {
            compositeDisposable.add(userLoanRepository.offers(UserPreferences.getInstance().token)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ offerSetResponse ->

                        if (offerSetResponse.offers.isNotEmpty()) {
                            val offer = offerSetResponse.offers.first()
                            userLoanView
                                    .showSimulationWithResponseOffer(offer)
                        } else {
                            userLoanView.hideLoading()
                            userLoanView.showNotApproved()
                        }

                    }, { error ->
                        val retrofitError = APIUtils.convertToErro(error)

                        when (retrofitError.httpStatus) {
                            HttpURLConnection.HTTP_NOT_FOUND -> {
                                userLoanView.hideLoading()
                                userLoanView.showNotApproved()
                            }
                            else -> {
                                userLoanView.hideLoading()
                                userLoanView.showNetworkError()
                            }
                        }

                    }))
        }
    }

    fun simulate(id: String, firstInstallmentDt: String, userToken: String, loanValue: BigDecimal,
                 loanLimit: BigDecimal) {


        if (userLoanView is UserLoanContract.View.Simulation) {
            if (loanValue <= loanLimit) {

                userLoanView.showLoading()
                compositeDisposable.add(userLoanRepository
                        .simulate(id,
                                loanValue,
                                firstInstallmentDt,
                                userToken)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ loanSimulationResponse ->
                            userLoanView.hideLoading()
                            userLoanView.simulationResult(loanSimulationResponse)

                        }, {
                            userLoanView.hideLoading()
                            userLoanView.showNetworkError()

                        }))

            } else {
                userLoanView.showLimitExceededError()
            }

        }
    }

    override fun onStart() {
        if (compositeDisposable.isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
    }

    override fun onPause() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}