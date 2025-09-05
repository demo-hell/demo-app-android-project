package br.com.mobicare.cielo.accessManager.customProfile

import br.com.mobicare.cielo.accessManager.model.AccessManagerCustomProfileDetailResponse
import br.com.mobicare.cielo.accessManager.model.AccessManagerCustomProfileResponse
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser
import br.com.mobicare.cielo.commons.presentation.BaseView

interface AccessManagerCustomProfileContract {
    interface Presenter {
        fun getCustomActiveProfiles()
        fun getDetailCustomProfile(profileId: String)
        fun onResume()
        fun onPause()
    }
    interface View : BaseView {
        fun showCustomProfiles(customProfiles: List<AccessManagerCustomProfileResponse>?)
        fun showCustomUsers(customUsers: List<AccessManagerUser>?)
        fun getDetailSuccess(userDetail: AccessManagerCustomProfileDetailResponse)
        fun showErrorProfile()
        fun showErrorEmptyProfiles(userSelected: AccessManagerUser? = null)
    }
}
