package br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy.benefits.technical

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.alert.CieloAskQuestionDialogFragment
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.constants.SEPARATOR
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingTechnicalAccessBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2Analytics
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_BTN_SEND_PICTURES
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_BTN_SEND_PICTURES_LATER
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_ERROR_VALIDATE_IDENTITY
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_SEND_PICTURES
import br.com.mobicare.cielo.idOnboarding.enum.IDOCheckpointP2
import br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy.benefits.IDOnboardingBenefitsInfoP1PolicyPresenter
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class IDOnboardingTechnicalBenefitsInfoP1PolicyFragment : BaseFragment(),
    CieloNavigationListener {

    private val presenter: IDOnboardingBenefitsInfoP1PolicyPresenter by inject { parametersOf(this) }
    private val analytics: IDOnboardingP2Analytics by inject()
    private val analyticsGA: IDOnboardingP2AnalyticsGA by inject()

    private var permissionAlertDialog: CieloAskQuestionDialogFragment? = null
    private var navigation: CieloNavigation? = null
    private var binding: FragmentIdOnboardingTechnicalAccessBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            FragmentIdOnboardingTechnicalAccessBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupView()
        setupListeners()
        setupStepRestartBottomSheet()
        verifyLocationPermission()
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_SEND_PICTURES)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        val tradeName = presenter.fetchTradeName()
        val cnpj = presenter.fetchCnpj().replaceAfter(SEPARATOR, getString(R.string.cnpj_mask))

        binding?.apply {
            btSendNow.isEnabled = true
            btSendLater.isEnabled = true
            btGoToHelpCenter.isEnabled = true

            userStatus.p1Flow?.deadlineRemainingDays?.let { deadlineRemaining ->
                btSendLater.visible(deadlineRemaining > ZERO)
            }

            tvAccessExplanation.fromHtml(R.string.id_onboarding_p1_technical_access_can_title)

            includePersonalInfo.apply {
                tvRoleName.text =
                    getString(R.string.access_manager_role_group_card_description_technical_title)

                ivImage.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_13_selfie_update,
                        null
                    )
                )

                tvAccessProfile.fromHtml(
                    R.string.id_onboarding_access_profile_information_title,
                    tradeName,
                    cnpj
                )

                tvTitleWithPrimaryName.fromHtml(
                    R.string.id_onboarding_pictures_send_to_conclude,
                    userStatus.name?.substringBefore(ONE_SPACE).toLowerCasePTBR().capitalizePTBR()
                )
            }
        }
    }

    private fun setupListeners() {
        binding?.apply {
            btSendNow.setOnClickListener {
                it.isEnabled = false
                analytics.logIDOnClickSendPictures(
                    ANALYTICS_ID_BTN_SEND_PICTURES,
                    UserObj.MainRoleEnum.TECHNICAL.description
                )
                analyticsGA.logIDSendPicturesNowSignUp(
                    ANALYTICS_ID_SCREEN_VIEW_SEND_PICTURES)
                goToSendPhotosNow()
            }

            btSendLater.setOnClickListener {
                it.isEnabled = false
                analytics.logIDOnClickSendPictures(
                    ANALYTICS_ID_BTN_SEND_PICTURES_LATER,
                    UserObj.MainRoleEnum.TECHNICAL.description
                )
                analyticsGA.logIDSendPicturesLaterClick(
                    ANALYTICS_ID_SCREEN_VIEW_SEND_PICTURES)
                goToSendPhotosLater()
            }

            btGoToHelpCenter.setOnClickListener {
                it.isEnabled = false
                analytics.logIDOnClickHelpCenter(UserObj.MainRoleEnum.TECHNICAL.description)
                goToOnboardingHelpCenter()
            }
        }
    }

    private fun goToSendPhotosNow() {
        findNavController().navigate(
            when (IDOnboardingFlowHandler.checkpointP2) {
                IDOCheckpointP2.DOCUMENT_PHOTO_UPLOADED -> IDOnboardingTechnicalBenefitsInfoP1PolicyFragmentDirections.actionIdOnboardingTechnicalBenefitsFragmentToIdOnboardingPicturesSelfieGuideFragment()
                else -> IDOnboardingTechnicalBenefitsInfoP1PolicyFragmentDirections.actionIdOnboardingTechnicalBenefitsFragmentToIdOnboardingPicturesSelectDocFragment()
            }
        )
    }

    private fun goToSendPhotosLater() {
        findNavController().navigate(IDOnboardingTechnicalBenefitsInfoP1PolicyFragmentDirections.actionIdOnboardingTechnicalBenefitsFragmentToIdOnboardingPicturesLaterFragment(
            UserObj.MainRoleEnum.TECHNICAL.description
        ))
    }

    private fun goToOnboardingHelpCenter() {
        requireActivity().openFaq(
            tag = ConfigurationDef.TAG_HELP_CENTER_IDENTIDADE_DIGITAL,
            subCategoryName = getString(R.string.id_onboarding_name)
        )
    }

    private fun setupStepRestartBottomSheet() {
        if (userStatus.onboardingStatus?.userStatus?.restartP2 == true) {
            analytics.logIDModal(ANALYTICS_ID_ERROR_VALIDATE_IDENTITY)
            analyticsGA.logIDP2ValidateIdentityErrorDisplay()
            IDOnboardingFlowHandler.showCustomBottomSheet(
                activity = this@IDOnboardingTechnicalBenefitsInfoP1PolicyFragment.activity,
                title = getString(R.string.id_onboarding_pictures_restart_warning_title),
                message = getString(R.string.id_onboarding_pictures_restart_warning_message),
                bt2Title = getString(R.string.id_onboarding_pictures_restart_warning_button_label),
                bt2Callback = {
                    analytics.logIDOnClickTakeNewPicturesModal()
                    analyticsGA.logIDP2ValidateIdentityErrorSignUp()
                    false
                },
                isFullScreen = true
            )
        }
    }

    private fun verifyLocationPermission() {
        activity?.let { itActivity ->
            if (ContextCompat.checkSelfPermission(
                    itActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    setupExplainedPermissionDialog(itActivity)
                } else {
                    setupDefaultPermissionDialog(itActivity)
                }

                permissionAlertDialog?.show(parentFragmentManager, null)
            }
        }
    }

    private fun setupDefaultPermissionDialog(activity: FragmentActivity) {
        permissionAlertDialog = CieloAskQuestionDialogFragment.Builder()
            .title(getString(R.string.permission_location_title))
            .message(getString(R.string.permission_location_denied_message))
            .positiveTextButton(getString(R.string.permission_location_denied_show_config))
            .cancelTextButton(getString(R.string.cancelar))
            .setCancelButtonBackgroundResource(ResourcesCompat.ID_NULL)
            .onPositiveButtonClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts(
                    activity.getString(R.string.permission_needed_package),
                    activity.packageName,
                    null
                )
                intent.data = uri
                activity.startActivity(intent)
            }
            .build()
    }

    private fun setupExplainedPermissionDialog(activity: FragmentActivity) {
        permissionAlertDialog = CieloAskQuestionDialogFragment.Builder()
            .title(getString(R.string.permission_location_title))
            .message(getString(R.string.permission_location_message))
            .positiveTextButton(getString(R.string.permission_location_allow))
            .cancelTextButton(getString(R.string.cancelar))
            .setCancelButtonBackgroundResource(ResourcesCompat.ID_NULL)
            .onPositiveButtonClickListener {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
            .build()
    }

    override fun onDestroy() {
        permissionAlertDialog?.dismissAllowingStateLoss()
        super.onDestroy()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onBackButtonClicked(): Boolean {
        if (IDOnboardingFlowHandler.isLogin)
            baseLogout(isLoginScreen = false)
        else
            requireActivity().finishAndRemoveTask()
        return super.onBackButtonClicked()
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 12
    }
}