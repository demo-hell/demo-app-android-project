package br.com.mobicare.cielo.coil.presentation.adress

import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.coil.CoilRepository
import br.com.mobicare.cielo.coil.domain.*
import br.com.mobicare.cielo.coil.domains.CoilOptionObj
import br.com.mobicare.cielo.commons.constants.HTTP_500_ERROR
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import java.util.*

class ServiceAddressPresenter(
        private val mRepository: CoilRepository) : ServiceAddressContract.Presenter {

    private var supplies: ArrayList<CoilOptionObj>? = null
    private var merchantAddress: MerchantAddress? = null

    private var errorStep = ZERO

    override fun setSupplies(supplies: ArrayList<CoilOptionObj>) {
        this.supplies = supplies
    }

    private lateinit var mView: ServiceAddressContract.View

    override fun setView(view: ServiceAddressContract.View) {
        mView = view
    }

    override fun onCleared() {
        mRepository.disposable()
    }

    override fun resubmit() {
        if (errorStep == ZERO) {
            loadAdress()
        } else  {
            buySupples()
        }
    }

    override fun loadAdress() {

        val token: String? = UserPreferences.getInstance().token

        token?.let {
            mRepository.merchantAddress(it, object : APICallbackDefault<MerchantAddressResponse, String> {
                override fun onStart() {
                    super.onStart()
                    mView.showLoading()
                }

                override fun onError(error: ErrorMessage) {
                    mView.hideLoading()
                    errorStep = ZERO
                    when {
                        error.logout -> mView.logout(error)
                        error.httpStatus >= HTTP_500_ERROR -> mView.showSubmit(error)
                        else -> mView.showError(error)
                    }
                }
                override fun onSuccess(response: MerchantAddressResponse) {
                    this@ServiceAddressPresenter.merchantAddress = response.address
                    mView.showAddress(response.address)
                    mView.hideLoading()
                }

            })
        }

    }

    override fun buySupples() {

        val token: String? = UserPreferences.getInstance().token
        if (token != null && supplies != null && merchantAddress != null) {

            val arraySuppliesCosen = getMerchantSupplyChosenRequest()

            mRepository.merchantBuySupply(token, arraySuppliesCosen, object : APICallbackDefault<MerchantBuySupplyChosenResponse, String> {
                override fun onStart() {
                    super.onStart()
                    mView.showLoading()
                }

                override fun onError(error: ErrorMessage) {
                    mView.hideLoading()
                    errorStep = ONE
                    when {
                        error.logout -> mView.logout(error)
                        error.httpStatus >= HTTP_500_ERROR -> mView.showSubmit(error)
                        else -> mView.showError(error)
                    }
                }

                override fun onSuccess(response: MerchantBuySupplyChosenResponse) {
                    supplies?.let { spl ->
                        response.supplies.forEach {
                            val coilOption = spl.find { msc ->
                                it.supplyCode == msc.code
                            }
                            coilOption?.let { msc ->
                                it.title = msc.title
                                it.description = msc.description
                            }
                        }
                    }
                    mView.showSucess(response)
                    mView.hideLoading()
                }
            })
        }
    }

    private fun getMerchantSupplyChosenRequest(): MerchantSupplyChosenRequest {
        val count = supplies?.size
        val suppliesChosen = ArrayList<MerchantSupplyChosen>(count!!)
        supplies?.forEach {
            suppliesChosen.add(supplyItemChosen(it, merchantAddress!!))
        }
        val arraySuppliesCosen = MerchantSupplyChosenRequest(suppliesChosen)
        return arraySuppliesCosen
    }

    private fun supplyItemChosen(coilOption: CoilOptionObj, merchantAddress: MerchantAddress): MerchantSupplyChosen {
        return if (coilOption.quantity == ZERO)
            MerchantSupplyChosen(supplyCode = coilOption.code, quantity = ONE,
                    deliveryAddressZipCode = merchantAddress.zipCode.orEmpty())
        else
            MerchantSupplyChosen(supplyCode = coilOption.code,
                    quantity = coilOption.quantity,
                    deliveryAddressZipCode = merchantAddress.zipCode.orEmpty())
    }
}