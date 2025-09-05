package br.com.mobicare.cielo.pixMVVM.presentation.home.ui

import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.component.onboarding.model.BaseOnboardingPage
import br.com.mobicare.cielo.component.onboarding.ui.BaseOnboardingFragment
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.safeNavigate

class PixOnboardingHomeFragment : BaseOnboardingFragment() {
    override fun getPages(): List<BaseOnboardingPage> {
        return listOf(
            BaseOnboardingPage(
                title = R.string.text_onboarding_home_pix_title_page_one,
                subtitle = R.string.text_onboarding_home_pix_subtitle_page_one,
                image = R.drawable.img_105_pix,
            ),
            BaseOnboardingPage(
                title = R.string.text_onboarding_home_pix_title_page_two,
                subtitle = R.string.text_onboarding_home_pix_subtitle_page_two,
                image = R.drawable.img_28_transferencia_ok,
            ),
            BaseOnboardingPage(
                title = R.string.text_onboarding_home_pix_title_page_three,
                subtitle = R.string.text_onboarding_home_pix_subtitle_page_three,
                image = R.drawable.img_110_pix_01,
            ),
            BaseOnboardingPage(
                title = R.string.text_onboarding_home_pix_title_page_four,
                subtitle = R.string.text_onboarding_home_pix_subtitle_page_four,
                image = R.drawable.img_119_pix_saque_e_troco_01,
            ),
            BaseOnboardingPage(
                title = R.string.text_onboarding_home_pix_title_page_five,
                subtitle = R.string.text_onboarding_home_pix_subtitle_page_five,
                image = R.drawable.img_120_pix_saque_e_troco_02,
            ),
        )
    }

    override fun getTextFinishButton(): String {
        return getString(R.string.pix_onboarding_home_label_finish_button)
    }

    override fun getUserPreferencesViewOnboardingKey(): String {
        return UserPreferences.IS_PIX_ONBOARDING_HOME_VIEWED
    }

    override fun navigateTo(): () -> Unit =
        {
            doWhenResumed {
                findNavController().safeNavigate(
                    PixOnboardingHomeFragmentDirections.actionPixOnboardingHomeFragmentToPixHomeFragment(null),
                )
            }
        }

    override fun onClickBackButton(): () -> Unit =
        {
            requireContext().backToHome()
            requireActivity().finishAndRemoveTask()
        }
}
