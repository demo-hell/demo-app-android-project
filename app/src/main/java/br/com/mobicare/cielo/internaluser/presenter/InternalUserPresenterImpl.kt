package br.com.mobicare.cielo.internaluser.presenter

import android.view.View
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.changeEc.CHANGE_EC_GRUPO_COMERCIAL
import br.com.mobicare.cielo.changeEc.CHANGE_EC_GRUPO_PAGAMENTO
import br.com.mobicare.cielo.changeEc.ChangeEcRepository
import br.com.mobicare.cielo.changeEc.domain.HierachyResponse
import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.changeEc.domain.MerchantsObj
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.internaluser.InternalUserView

private const val PAGE_SIZE = 25

class InternalUserPresenterImpl(private val view: InternalUserView,
                                private val userPreferences: UserPreferences,
                                private val repository: ChangeEcRepository) : InternalUserPresenter {

    private var isFirst = true
    private var isLastPage: Boolean? = false
    private var searchCriteria: String? = null
    private var merchant: Merchant? = null

    private val inpersonateChangetoNewEc = object : APICallbackDefault<Impersonate, String> {
        override fun onStart() {
            view.showLoading()
        }

        override fun onError(error: ErrorMessage) {
            view.showError(error)
        }

        override fun onSuccess(response: Impersonate) {
            merchant?.let {
                view.onChangetoNewEc(it, response)
            }
        }
    }

    override fun searchChild(page: Int, searchCriteria: String?) {
        if (searchCriteria.isNullOrEmpty().not()) {
            isLastPage = false
            isFirst = true
            this.searchCriteria = searchCriteria
        }

        if (isLastPage == true) return
        if (isFirst) view.showLoading() else view.showLoadingMore()

        repository.children(userPreferences.token, PAGE_SIZE, page, this.searchCriteria, callback = object : APICallbackDefault<HierachyResponse, String> {

            override fun onError(error: ErrorMessage) {
                view.showError(error)
                view.hideLoading()
                view.hideLoadingMore()
            }

            override fun onSuccess(response: HierachyResponse) {
                isLastPage = response.pagination?.lastPage
                callListChild(response)
            }
        })
    }

    override fun callChangeEc(merchant: Merchant, token: String,fingerprint: String) {
        this.merchant = merchant

        if (merchant.hierarchyLevel == CHANGE_EC_GRUPO_PAGAMENTO ||
                merchant.hierarchyLevel == CHANGE_EC_GRUPO_COMERCIAL) {
            changeEcDad(token, merchant,fingerprint)
        } else {
            changeEcChild(token, merchant,fingerprint)
        }
    }

    private fun callListChild(response: HierachyResponse) {
        val merchants: MutableList<Merchant> = ArrayList()
        isLastPage = response.pagination?.lastPage

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

        val merchantsToken = MerchantsObj(userPreferences.token, merchants as ArrayList<Merchant>)

        if (isFirst) view.hideLoading() else view.hideLoadingMore()
        isFirst = false

        if (merchantsToken.merchants.isNotEmpty()) {
            view.showEmptyListError(visibility = View.GONE,
                    background = R.drawable.outline_rounded_solid_white_color_c5ced7)
            view.showChildren(merchantsToken)

        } else view.showEmptyListError(visibility = View.VISIBLE,
                background = R.drawable.outline_rounded_solid_white_stroke_red)
    }

    private fun changeEcDad(token: String, merchant: Merchant,fingerprint: String) {
        merchant.let {
            repository.impersonate(it.id,
                    token,
                    ChangeEcRepository.HierarchyType.MERCHANT,
                    inpersonateChangetoNewEc,fingerprint)
        }
    }

    private fun changeEcChild(token: String, merchant: Merchant,fingerprint: String) {
        merchant.let {
            repository.impersonate(merchant.hierarchyNode,
                    token,
                    ChangeEcRepository.HierarchyType.NODE,
                    inpersonateChangetoNewEc,fingerprint)
        }
    }
}