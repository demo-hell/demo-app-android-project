package br.com.mobicare.cielo.main.presentation.ui

import android.content.Context
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.deeplink.model.DeepLinkModel
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleModal
import br.com.mobicare.cielo.lgpd.domain.entity.LgpdElegibilityEntity
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.merchant.domain.entity.MerchantResponseRegisterGet
import br.com.mobicare.cielo.merchant.domain.entity.ResponseDebitoContaEligible

interface MainBottomNavigationContract {

    interface Presenter : CommonPresenter {

        fun sendTokenFCM(merchantId: String)

        fun getUserInformation(isImpersonate: Boolean = false)
        fun updateAppMenus(accessToken: String)
        fun showCancelOnboard()
        fun procedeUserInformation()
        fun balcaoRecebiveisElegibility()
        fun sendPermisionRegister()
        fun balcaoRecebiveisPermissionRegister()
        fun debitoEmContaElegibility()
        fun sendDebitoContaPermission(optin: String)
        fun checkLgpd()
        fun procedureAfterLgpd()
        fun checkDeeplink()
        fun getUserObj(): UserObj?
    }

    interface View {

        fun onUserInformationsResponse(userInformations: MeResponse?, isImpersonate: Boolean = false) {}
        fun verifyNeedsOnboarding(userInformations: MeResponse?, isImpersonate: Boolean = false) {}
        fun callOnboardFirstAccess() {}
        fun getContext(): Context
        fun loadBottomNavigationItem(itemIndex: Int) {}
        fun showCancelOnboarding() {}
        fun showBannerBalcaoRecebiveis() {}
        fun bannerBalcaoRecebiveisNotElegivel() {}
        fun showMfaOnboarding() {}
        fun showLGPD(elegible: LgpdElegibilityEntity) {}
        fun getDataPermissionRegister(it: MerchantResponseRegisterGet) {}
        fun sucessPermissionRegister() {}
        fun errorPermissionRegister() {}
        fun errorGeneric(error: ErrorMessage?) {}
        fun erroUrlEligible(error: ErrorMessage?) {}
        fun getAuthorizationHistory(it: MerchantResponseRegisterGet?) {}
        fun bannerDebitoContaEligible() {}
        fun showBannerDebitoEmConta(it: ResponseDebitoContaEligible) {}
        fun showBannerDebitoEmContaWaiting(it: ResponseDebitoContaEligible?) {}
        fun showBannerDebitoEmContaActive() {}
        fun resultSearchDebitoEmContaActive(it: ResponseDebitoContaEligible) {}
        fun onShowWarningModal(modal: FeatureToggleModal, isImpersonate: Boolean){}
        fun onLoadOtherInformation(isImpersonate: Boolean){}
        fun startDeeplinkFlow(deepLinkModel: DeepLinkModel){}
        fun startMktDeeplink(mktDeeplinkUrl: String) {}
        fun startDeeplinkOpenFinance() {}
        fun startDeeplinkConclusionShareOPF() {}
        fun onLogout()
    }

    interface Listener {

        fun callLogout()
    }
}