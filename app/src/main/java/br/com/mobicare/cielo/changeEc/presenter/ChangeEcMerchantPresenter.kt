package br.com.mobicare.cielo.changeEc.presenter

import br.com.mobicare.cielo.changeEc.CHANGE_EC_GRUPO_COMERCIAL
import br.com.mobicare.cielo.changeEc.CHANGE_EC_GRUPO_PAGAMENTO
import br.com.mobicare.cielo.changeEc.ChangeEcRepository
import br.com.mobicare.cielo.changeEc.domain.HierachyResponse
import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.changeEc.domain.MerchantsObj
import br.com.mobicare.cielo.changeEc.fragment.ChanceEcFragmentContract
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.merchants.MerchantsResponse

class ChangeEcMerchantPresenter(private val mRepository: ChangeEcRepository) : ChanceEcFragmentContract.Presenter {

    private lateinit var mView: ChanceEcFragmentContract.View
    private var mMerchants: ArrayList<Merchant>? = null
    private var mMerchant: Merchant? = null
    private val PAGE_SIZE = 25
    private var impersonate: Impersonate? = null
    private var isFirst = true
    private var isLastPage: Boolean? = false
    private var currentPage = 0

    val inpersonateChangetoNewEc = object : APICallbackDefault<Impersonate, String> {
        override fun onStart() {
            mView.showLoading()
        }

        override fun onError(error: ErrorMessage) {
            if (error.logout) {
                mView.logout(error)
            } else {
                mView.showError(error)
            }
        }

        override fun onSuccess(response: Impersonate) {
            mMerchant?.let {
                mView.onChangetoNewEc(it, response)
            }
        }
    }

    val inpersonateChangetoChildren = object : APICallbackDefault<Impersonate, String> {
        override fun onStart() {
            mView.showLoading()
        }

        override fun onError(error: ErrorMessage) {
            if (error.logout) {
                mView.logout(error)
            } else {
                mView.showError(error)
            }
        }

        override fun onSuccess(response: Impersonate) {
            impersonate = response
            currentPage = 0
            getChildrens()
        }
    }


    override fun setView(view: ChanceEcFragmentContract.View) {
        mView = view
    }

    override fun loadItens() {

        this.mView.showLoading()
        this.mRepository.getMerchants(UserPreferences.getInstance().token, object : APICallbackDefault<MerchantsResponse, String> {
            override fun onError(error: ErrorMessage) {
                this@ChangeEcMerchantPresenter.mView.showError(error)
            }

            override fun onSuccess(response: MerchantsResponse) {
                val merchantList = ArrayList<Merchant>(response.merchants)
                merchantList.forEach {
                    if (it.hierarchies == null){
                        it.hierarchies = emptyArray()
                    }
                }
                this@ChangeEcMerchantPresenter.mMerchants = merchantList
                this@ChangeEcMerchantPresenter.mView.hideLoading()
                this@ChangeEcMerchantPresenter.mView.showMerchants(merchantList)
            }
        })
    }

    override fun callChangeEc(merchant: Merchant, token: String, isLogar: Boolean,
                              isMerchantNode: Boolean, callChildScreen: Boolean,fingerprint: String) {
        this.mMerchant = merchant

        if (UserPreferences.getInstance().isConvivenciaUser) {
            if (merchant.hierarchyLevel == CHANGE_EC_GRUPO_PAGAMENTO ||
                    merchant.hierarchyLevel == CHANGE_EC_GRUPO_COMERCIAL) {
                if (!isLogar) {
                    if (callChildScreen) mView.callChildrenScreen(merchant)
                    else callChildren(token, merchant, fingerprint)
                } else {
                    changeEcDad(token, merchant, fingerprint)
                }
            } else {
                if (isMerchantNode) {
                    changeEcNodeChild(token, merchant, fingerprint)
                } else {
                    changeEcChild(merchant, fingerprint)
                }
            }
        } else {
            if (merchant.hierarchies.isNullOrEmpty()) {
                changeEcChildNotConvivencia(token, merchant, fingerprint)
            } else {
                callChildrenNotConvivencia(merchant)
            }
        }
    }

    private fun changeEcNodeChild(token: String, merchant: Merchant, fingerprint: String) {
        mRepository.impersonate(merchant.id, token,
                ChangeEcRepository.HierarchyType.MERCHANT, inpersonateChangetoNewEc, fingerprint)
    }

    private fun callChildren(token: String, merchant: Merchant, fingerprint: String) {

        this.mMerchant = merchant

        mRepository.impersonate(merchant.id, token, ChangeEcRepository.HierarchyType.MERCHANT,
                inpersonateChangetoChildren, fingerprint)

  }

    override fun getChildrens() {
        if (isLastPage == true) return
        if (isFirst.not()) mView.showLoadingMore()

        impersonate?.accessToken?.let { token ->
            mRepository.children(token, PAGE_SIZE, currentPage + 1, null, callback = object : APICallbackDefault<HierachyResponse, String> {

                override fun onError(error: ErrorMessage) {
                    mView.showError(error)
                    mView.hideLoadingMore()
                }

                override fun onSuccess(response: HierachyResponse) {
                    val merchants: MutableList<Merchant> = ArrayList()
                    isLastPage = response.pagination?.lastPage
                    ++currentPage

                    this@ChangeEcMerchantPresenter.mMerchant?.let {
                        if (isFirst) merchants.add(it)
                        isFirst = false
                    }

                    response.hierarchies?.forEach {
                        merchants.add(Merchant(
                                it.nivelHierarquia,
                                it.nomeHierarquia,
                                it.noHierarquia,
                                it.id,
                                it.nomeFantasia,
                                emptyArray()
                        ))
                    }

                    callListChedren(token, merchants)

                    mView.hideLoadingMore()
                }
            })
        }
    }

    private fun callChildrenNotConvivencia(merchant: Merchant) {
        val merchants: MutableList<Merchant> = ArrayList()


        merchant.let {
            merchants.add(Merchant(
                    it.hierarchyLevel,
                    it.hierarchyLevelDescription,
                    it.hierarchyNode,
                    it.id,
                    it.tradingName,
                    emptyArray()
            ))
        }

        merchant.hierarchies?.forEach {
            merchants.add(Merchant(
                    it.nivelHierarquia,
                    it.formaRecebimento,
                    it.noHierarquia,
                    it.id,
                    it.nome,
                    emptyArray()))
        }

        callListChedren("", merchants)
    }

    private fun callListChedren(token: String, merchants: MutableList<Merchant>) {
        val merchantsToken = MerchantsObj(token, merchants as ArrayList<Merchant>)
        mView.hideLoading()
        mView.showChildren(merchantsToken)
    }

    private fun changeEcChildNotConvivencia(token: String, merchant: Merchant, fingerprint: String) {
        merchant.let {

            mRepository.impersonate(it.id, token, ChangeEcRepository.HierarchyType.NODE,
                    inpersonateChangetoNewEc, fingerprint)


        }
    }

    private fun changeEcDad(token: String, merchant: Merchant, fingerprint: String) {
        merchant.let {

            mRepository.impersonate(it.id, token, ChangeEcRepository.HierarchyType.MERCHANT, inpersonateChangetoNewEc, fingerprint)

        }
    }

    private fun changeEcChild(merchant: Merchant, fingerprint: String) {
        UserPreferences.getInstance().tokenImpersonate?.let { tokenImpersonate ->
            merchant.let {
                mRepository.impersonate(
                    merchant.hierarchyNode, tokenImpersonate,
                    ChangeEcRepository.HierarchyType.NODE,
                    inpersonateChangetoNewEc, fingerprint
                )
            }
        }
    }
}