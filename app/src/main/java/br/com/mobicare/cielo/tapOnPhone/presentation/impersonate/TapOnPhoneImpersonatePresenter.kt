package br.com.mobicare.cielo.tapOnPhone.presentation.impersonate

import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.changeEc.ChangeEcRepository
import br.com.mobicare.cielo.changeEc.domain.HierachyResponse
import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.TWENTYFIVE
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.tapOnPhone.domain.model.TapOnPhoneEligibilityResponse

private const val CHANGE_EC_PAYMENT = "GRUPO_PAGAMENTO"
private const val CHANGE_EC_COMMERCIAL = "GRUPO_COMERCIAL"

class TapOnPhoneImpersonatePresenter(
    private val view: TapOnPhoneImpersonateContract.View,
    private val repository: ChangeEcRepository,
    private val userPreferences: UserPreferences
) : TapOnPhoneImpersonateContract.Presenter {

    override fun findECTap(
        eligibilityResponse: TapOnPhoneEligibilityResponse?,
        fingerprint: String
    ) {
        eligibilityResponse?.merchant?.let { ec ->
            repository.children(token = userPreferences.token,
                pageSize = TWENTYFIVE,
                pageNumber = ONE,
                searchCriteria = ec,
                callback = object : APICallbackDefault<HierachyResponse, String> {
                    override fun onStart() {
                        view.onShowLoading(R.string.tap_on_phone_change_ec)
                    }

                    override fun onError(error: ErrorMessage) {
                        showError(error)
                    }

                    override fun onSuccess(response: HierachyResponse) {
                        response.hierarchies?.firstOrNull()?.let {
                            val merchant = Merchant(
                                it.nivelHierarquia,
                                it.nomeHierarquia,
                                it.noHierarquia,
                                it.id,
                                it.nomeFantasia
                            )
                            impersonateECTap(merchant, fingerprint)
                        } ?: showError()
                    }
                })
        }
    }

    private fun impersonateECTap(
        merchant: Merchant?,
        fingerprint: String
    ) {
        merchant?.let { itMerchant ->
            var establishment = itMerchant.hierarchyNode
            var hierarchyLevel = ChangeEcRepository.HierarchyType.NODE
            if (itMerchant.hierarchyLevel == CHANGE_EC_PAYMENT ||
                itMerchant.hierarchyLevel == CHANGE_EC_COMMERCIAL
            ) {
                establishment = itMerchant.id
                hierarchyLevel = ChangeEcRepository.HierarchyType.MERCHANT
            }

            repository.impersonate(
                establishment, userPreferences.token, hierarchyLevel,
                object : APICallbackDefault<Impersonate, String> {
                    override fun onError(error: ErrorMessage) {
                        view.onShowError(error)
                    }

                    override fun onSuccess(response: Impersonate) {
                        view.onSuccessImpersonateECTap(response, itMerchant)
                    }

                    override fun onFinish() {
                        view.onHideLoading()
                    }
                },
                fingerprint
            )
        } ?: showError()
    }

    private fun showError(errorMessage: ErrorMessage? = null) {
        view.onHideLoading()
        view.onShowError(errorMessage)
    }

}