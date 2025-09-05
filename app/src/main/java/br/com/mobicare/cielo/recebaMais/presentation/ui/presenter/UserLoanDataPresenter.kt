package br.com.mobicare.cielo.recebaMais.presentation.ui.presenter

import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.ValidationUtils
import br.com.mobicare.cielo.commons.utils.phone
import br.com.mobicare.cielo.commons.utils.phoneNumber
import br.com.mobicare.cielo.recebaMais.domain.*
import br.com.mobicare.cielo.recebaMais.domains.entities.ContratarEmprestimoRecebaMaisRequest
import br.com.mobicare.cielo.recebaMais.managers.RecebaMaisRepository
import br.com.mobicare.cielo.recebaMais.presentation.ui.MyDataContract
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UserLoanDataPresenter(private val repository: RecebaMaisRepository) : MyDataContract.Presenter {


    private var validateEmail = false
    private var validateBank = false
    private var validatePhone = false
    private var composite = CompositeDisposable()
    private var uiScheduler: Scheduler? = AndroidSchedulers.mainThread()
    private var ioScheduler: Scheduler? = Schedulers.io()
    private lateinit var view: MyDataContract.View

    private var flagMerchant = false
    private var flagBanks = false

    private lateinit var installment: Installment

    override fun setView(view: MyDataContract.View) {
        this.view = view
    }

    override fun setInstallment(installment: Installment) {
        this.installment = installment
    }

    override fun loadMerchant(authorization: String, token: String) {
        flagMerchant = false


        if (!installment.email.isNullOrEmpty() && installment.phone != null) {
            view.merchantSuccess(installment.email!!, getPhoneNumber(OwnerPhone
                    .convertPhoneIntoOwnerPhone(installment.phone)))
            flagMerchant = true
            loadsComplete()
            return
        }

        composite.add(repository.getMerchant(authorization, token)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnComplete {
                    flagMerchant = true
                    loadsComplete()
                }
                .subscribe({

                    var allNumber: String = ""
                    var allEmail: String = ""

                    it?.userOwners?.first()?.run {
                        allNumber = getPhoneNumber(this.phones.first())
                        allEmail = this.email
                    }

                    view.merchantSuccess(allEmail, allNumber)

                }, {
                    flagMerchant = true
                    // onErrorDefalt(it)
                }))
    }

    fun borrow(token: String, contratarEmprestimo: ContratarEmprestimoRecebaMaisRequest) {

        val accessToken = userToken().toString()

        composite.add(repository.setBorrow(token, contratarEmprestimo, accessToken)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    view.sucessBorrow()

                }, {
                     onErrorBorrow(it)
                }))

    }

    fun keepInterestOffer(offerId: String) {

        val accessToken = userToken().toString()

        composite.add(repository.keepInterestOffer(offerId, accessToken, Utils.authorization())
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({
                    view.sucessBorrow()

                }, {
                    onErrorDefault(it)
                }))

    }

    override fun resumoContract() {


        val accessToken = userToken().toString()


        composite.add(repository.summary(accessToken)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe({data->
                    view.sucessSummary(data.contracts)

                }, {
                }))



    }

    override fun loadBanks() {
        flagBanks = false
        composite.add(repository.getBanks()
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .doOnComplete {
                    flagBanks = true
                    loadsComplete()
                }
                .subscribe({
                    view.banksSuccess(it)
                }, {
                    onErrorDefault(it)
                }))
    }


    private fun loadsComplete() {
        if (flagBanks && flagMerchant) {
            view.hideLoading()
        }
    }


    private fun onErrorDefault(error: Throwable) {
        view.let {
            val errorMessage = APIUtils.convertToErro(error)
            if (errorMessage.logout) {
                it.logout(errorMessage)
            }
        }
    }

    private fun onErrorBorrow(error: Throwable) {
        view.let {
            val errorMessage = APIUtils.convertToErro(error)

            if (errorMessage.logout) {
                it.logout(errorMessage)
            } else {
                it.showErrorResponse(errorMessage.message)
            }
        }
    }


    override fun validatePhone(phone: String) {
        if (phone.isNotEmpty()) {
            val p = phone.phoneNumber()
            installment.phone = Phone(p.substring(0, 2), p.substring(2), "")
            validatePhone = true
        } else {
            validatePhone = false
            installment.phone = null
        }
    }

    override fun validadeEmail(email: String) : String {
        if (!email.isNullOrEmpty() && ValidationUtils.isEmail(email)) {
            installment.email = email
            validateEmail = true
        } else {
            installment.email = ""
            validateEmail = false
        }
        return  installment.email!!
    }

    override fun validadeBank(bank: Bank) {
        installment.bank = bank
        validateBank = true
    }

    override fun validate() : Boolean  {
        if (!validateBank) {
           view.showMessageDomicilioObrigatorio()
        }
        return validateBank && validateEmail && validatePhone
    }


    private fun getPhoneNumber(stablishment: StablishmentResponse): String {
        var allNumber = ""
        if (stablishment.owners.isNotEmpty() && stablishment.owners[0].phones.isNotEmpty()) {
            val firstTempPhone = stablishment.owners[0].phones[0]
            allNumber = getPhoneNumber(OwnerPhone(firstTempPhone.areaCode,
                    firstTempPhone.number,
                    firstTempPhone.type))
        }
        return allNumber
    }

    private fun getPhoneNumber(phone: OwnerPhone): String {
        val areaCode = phone.areaCode
        val number = phone.number
        return "$areaCode$number".phone()
    }

    private fun userToken(): String? {
        return UserPreferences.getInstance().token
    }

}