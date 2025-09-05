package br.com.mobicare.cielo.coil.presentation.choose

import android.content.Context
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.coil.CoilRepository
import br.com.mobicare.cielo.coil.domain.MerchantSuppliesResponde
import br.com.mobicare.cielo.coil.domain.MerchantSupply
import br.com.mobicare.cielo.coil.domains.CoilOptionObj
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage

class CoilChoosePresenter(
        private val mContext: Context,
        private val mRepository: CoilRepository) : CoilChooseContract.Presenter {

    private lateinit var mView: CoilChooseContract.View

    private val COIL_UNIFIELD = "5070"
    private val COIL_LIO = "5071"

    override fun setView(view: CoilChooseContract.View) {
        mView = view
    }

    override fun onClieared() {
        mRepository.disposable()
    }

    override fun loadSupplies() {

        val token: String? = UserPreferences.getInstance().token

        token?.let {
            mRepository.merchantSupplies(it, object : APICallbackDefault<MerchantSuppliesResponde, String> {
                override fun onStart() {
                    super.onStart()
                    mView.showLoading()
                }

                override fun onError(error: ErrorMessage) {
                    mView.hideLoading()
                    if (error.logout) {
                        mView.logout(error)
                    }
                    else if (error.httpStatus == 420) {
                        mView.showIneligible(error.errorMessage)
                    }
                    else {
                        mView.showError(error)
                    }
                }

                override fun onSuccess(response: MerchantSuppliesResponde) {
                    val coilOptions = ArrayList<CoilOptionObj>()
                    val supplies = response.supplies.filter {
                        it.code == COIL_UNIFIELD || it.code == COIL_LIO
                    }

                    supplies.forEach {
                        when (it.code) {
                            COIL_UNIFIELD -> {
                                coilOptions.add(
                                        coilOptionObject(it,
                                                mContext.getString(R.string.coil_text_coil_bobina_unificada),
                                                mContext.getString(R.string.coil_text_coil_unified),
                                                mContext.getString(R.string.coil_text_coil_unified_complement)))
                            }
                            COIL_LIO -> {
                                coilOptions.add(
                                        coilOptionObject(it,
                                                mContext.getString(R.string.coil_text_coil_bobina_lio),
                                                mContext.getString(R.string.coil_text_coil_lio),
                                                ""))
                            }
                        }
                    }
                    mView.showSupplies(coilOptions)
                    mView.hideLoading()
                }

                private fun coilOptionObject(supply: MerchantSupply,
                                             title: String,
                                             description: String,
                                             descriptionComplement: String): CoilOptionObj {
                    val coilOptionObj = CoilOptionObj()
                    coilOptionObj.code = supply.code
                    coilOptionObj.title = title
                    coilOptionObj.description = description
                    coilOptionObj.descriptionComplement = descriptionComplement
                    coilOptionObj.allowedQuantity = supply.allowedQuantity
                    coilOptionObj.type = supply.type
                    return coilOptionObj
                }

            })
        }

    }


}