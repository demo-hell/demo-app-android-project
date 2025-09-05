package br.com.mobicare.cielo.idOnboarding.updateUser.docPictures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ARG_MAIN_USER_ROLE
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingPicturesLaterBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2Analytics
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.*
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP2
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import org.koin.android.ext.android.inject

class IDOnboardingPicturesLaterFragment : BaseFragment(), CieloNavigationListener {

    private val analytics: IDOnboardingP2Analytics by inject()
    private val analyticsGA: IDOnboardingP2AnalyticsGA by inject()

    private var navigation: CieloNavigation? = null
    private var binding: FragmentIdOnboardingPicturesLaterBinding? = null

    private val mainUserRole: String? by lazy {
        arguments?.getString(ARG_MAIN_USER_ROLE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIdOnboardingPicturesLaterBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupText()
        setupListeners()
        analytics.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_CUSTOMER_RETENTION, this.javaClass)
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_SEND_PICTURES_SEND_LATER)

    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupText() {
        binding?.apply {
            tvTitle.text = getString(R.string.id_onboarding_pictures_later_title)
            tvMessage.text = HtmlCompat.fromHtml(
                getString(R.string.id_onboarding_pictures_later_message),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            tvMessageItems.text = HtmlCompat.fromHtml(
                getString(R.string.id_onboarding_pictures_later_message_items),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            analyticsGA.logIDSendPicturesLaterDisplay(ANALYTICS_ID_SCREEN_VIEW_SEND_PICTURES_SEND_LATER)
        }
    }

    private fun setupListeners() {
        binding?.btBackArrow?.setOnClickListener {
            analytics.logIDOnClickSendPicturesLater(ANALYTICS_ID_BTN_COME_BACK, mainUserRole)
            activity?.onBackPressed()
        }

        binding?.btSendNow?.setOnClickListener {
            analytics.logIDOnClickSendPicturesLater(ANALYTICS_ID_BTN_SEND_PICTURES, mainUserRole )
            analyticsGA.logIDSendPicturesNowSignUp(
                ANALYTICS_ID_SCREEN_VIEW_SEND_PICTURES_SEND_LATER)
            goToSendPhotosNow()
        }

        binding?.btSendLater?.setOnClickListener {
            analytics.logIDOnClickSendPicturesLater(ANALYTICS_ID_BTN_SEND_PICTURES_LATER,
                mainUserRole)
            analyticsGA.logIDSendPicturesLaterClick(
                ANALYTICS_ID_SCREEN_VIEW_SEND_PICTURES_SEND_LATER)
            activity?.moveToHome()
        }
    }

    private fun goToSendPhotosNow() {
        findNavController().navigate(
            if (IDOnboardingFlowHandler.checkpointP2 == IDOCheckpointP2.DOCUMENT_PHOTO_UPLOADED){
                IDOnboardingPicturesLaterFragmentDirections.actionIdOnboardingPicturesLaterFragmentToIdOnboardingPicturesSelfieGuideFragment()
            }else{
                IDOnboardingPicturesLaterFragmentDirections.actionIdOnboardingPicturesLaterFragmentToIdOnboardingPicturesSelectDocFragment()
            }
        )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}