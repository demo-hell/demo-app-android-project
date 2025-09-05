package br.com.mobicare.cielo.interactBannersOffersNew.utils

import androidx.fragment.app.FragmentActivity
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.interactBannersOffersNew.presentation.InteractBannersOffersNewFragment

object InteractBannersUtils {
    fun launchInteractBanner(
        bannerType: InteractBannerTypes,
        shouldGetOffersFromApi: Boolean = true,
        frame: Int,
        fragmentActivity: FragmentActivity,
        bannerControl: BannerControl? = null,
        onSuccess: (() -> Unit),
        onError: (() -> Unit)? = null
    ) {
        InteractBannersOffersNewFragment.newInstance(
            bannerType = bannerType,
            shouldGetOffersFromApi = shouldGetOffersFromApi,
            bannerControl = bannerControl
        ) { state ->
            when (state) {
                is InteractBannersUiState.OnSuccess -> {
                    onSuccess()
                }

                is InteractBannersUiState.OnError -> {
                    onError?.invoke()
                }
            }
        }.addInFrame(
            fragmentActivity.supportFragmentManager,
            frame
        )
    }
}