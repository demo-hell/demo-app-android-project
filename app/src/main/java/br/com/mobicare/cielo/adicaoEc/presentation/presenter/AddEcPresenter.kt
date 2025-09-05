package br.com.mobicare.cielo.adicaoEc.presentation.presenter

import br.com.mobicare.cielo.adicaoEc.domain.api.AddEcRepository
import br.com.mobicare.cielo.adicaoEc.domain.model.BankAccountObj
import br.com.mobicare.cielo.adicaoEc.domain.model.ParamsEc
import br.com.mobicare.cielo.commons.data.utils.APIUtils
import br.com.mobicare.cielo.component.selectBottomSheet.SelectItem
import br.com.mobicare.cielo.esqueciSenha.domains.entities.*
import br.com.mobicare.cielo.esqueciSenha.presentation.presenter.Mapper
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

class AddEcPresenter(
    private val mView: AddEcContract.View,
    private val addEcRepository: AddEcRepository,
    private val uiScheduler: Scheduler,
    private val ioScheduler: Scheduler) : AddEcContract.Presenter {

    private var disposable = CompositeDisposable()

    override fun getBankList() {
        disposable.add(
            addEcRepository.getAllBanks()
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    val bankResponse = Mapper.mapper(it)

                    bankResponse.banks?.let { itBanks ->
                        val banks = ArrayList<SelectItem<BankMaskVO>>()
                        val banksMapped = itBanks.map { SelectItem("${it.code} - ${it.name}", it) }
                        banks.addAll(banksMapped)

                        mView.prepareBottomSheetBankList(banks)
                    }
                },{

                })
        )
    }

    override fun fetchAccountTypes(bankCode: String?, profileType: String, params: ParamsEc) {
        val accountTypesAux = ArrayList<BankMaskVO>()

        when {
            bankCode == CODE_BANCO_CAIXA_ECONOMICA && profileType == params.profileTypePF -> {
                accountTypesAux.addAll(
                    listOf(
                        BankMaskVO(params.currentAccount),
                        BankMaskVO(params.savingsAccount),
                        BankMaskVO(params.simpleAccount)
                    )
                )
            }

            bankCode == CODE_BANCO_CAIXA_ECONOMICA && profileType == params.profileTypePJ -> {
                accountTypesAux.addAll(
                    listOf(
                        BankMaskVO(params.currentAccount),
                        BankMaskVO(params.publicEntityAccount)
                    )
                )
            }

            else -> {
                accountTypesAux.addAll(
                    listOf(
                        BankMaskVO(params.currentAccount),
                        BankMaskVO(params.savingsAccount)
                    )
                )
            }
        }

        val accountTypes = accountTypesAux.map {
            SelectItem(it.name, it.name)
        } as ArrayList<SelectItem<BankMaskVO>>

        mView.prepareBottomSheetAccountTypeList(accountTypes)
    }

    override fun addNewEc(
        objEc: BankAccountObj,
        otpCode: String
    ) {
        disposable.add(
            addEcRepository.addNewEc(objEc, otpCode)
                .observeOn(uiScheduler)
                .subscribeOn(ioScheduler)
                .subscribe({
                    if (it.code() in 200..204)
                        mView.showBottomSheetSuccess()
                    else
                        mView.showError(APIUtils.convertToErro(it))
                }, {
                    mView.showError(APIUtils.convertToErro(it))
                })
        )
    }

    override fun prepareObjectToSubmit(objEc: BankAccountObj, profileType: String, params: ParamsEc): BankAccountObj {
        return objEc.apply {
            bankAccount.accountType =
                if (bankAccount.code == CODE_BANCO_CAIXA_ECONOMICA) {
                    when {
                        profileType == params.profileTypePF && bankAccount.accountType == params.currentAccount -> PF_CURRENT_ACCOUNT_CAIXA
                        profileType == params.profileTypePF && bankAccount.accountType == params.simpleAccount -> PF_SIMPLE_ACCOUNT_CAIXA
                        profileType == params.profileTypePJ && bankAccount.accountType == params.currentAccount -> PJ_CURRENT_ACCOUNT_CAIXA
                        profileType == params.profileTypePF && bankAccount.accountType == params.savingsAccount -> PF_SAVINGS_ACCOUNT_CAIXA
                        profileType == params.profileTypePJ && bankAccount.accountType == params.publicEntityAccount -> PJ_PUBLIC_ENTITITY_CAIXA
                        else -> PF_CURRENT_ACCOUNT_CAIXA
                    }
                } else {
                    when (bankAccount.accountType) {
                        params.currentAccount -> CURRENT_ACCOUNT
                        params.savingsAccount -> SAVINGS_ACCOUNT
                        else -> CURRENT_ACCOUNT
                    }
                }
        }
    }

    override fun onResume() {
        if (disposable.isDisposed) disposable = CompositeDisposable()
    }

    override fun onStop() {
        disposable.dispose()
    }
}

