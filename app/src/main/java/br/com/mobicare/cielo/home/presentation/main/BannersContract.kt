package br.com.mobicare.cielo.home.presentation.main

import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.presentation.CommonPresenter
import br.com.mobicare.cielo.idOnboarding.updateUser.homeCard.IDOnboardingHomeCardStatusEnum
import br.com.mobicare.cielo.login.domain.UserStatusPrepago
import br.com.mobicare.cielo.login.domains.entities.UserObj

interface BannersContract {

    interface View : BaseView {
        fun loadCardPrepago(data: UserStatusPrepago?)
        fun errorLoadStatus()
        fun loadBannerMigration()
        fun showLoadingMerchantStatusChallengeMFA()
        fun hideLoadingMerchantStatusChallengeMFA()
        fun showMerchantStatusChallengeMFA(merchantStatusMFA: String)
        fun showMerchantStatusPendingChallengeMFA()
        fun showMerchantStatusErroPennyDropAndNotEligibleChallengeMFA(message: Int)
        fun hideMerchantStatusChallengeMFA()
        fun showSallesAndIncommingCards()
        fun showIdOnboardingHomeStatusCard(status: IDOnboardingHomeCardStatusEnum) {}
        fun showInteractBanner()
        fun showLockedProfileScreen(userObj: UserObj?) {}
    }

    interface Presenter : CommonPresenter {
        fun loadMenu()
        fun loadUserStatus()
        fun vericationIfUserMigration()
        fun loadNotification(notificationCount: (Int) -> Unit)
        fun getMerchantMFAStatus()
        fun checkFeatureToggleInteractBanner()
        fun checkProfileType()
        fun getUserObj(): UserObj?
    }

}