package br.com.mobicare.cielo.idOnboarding.updateUser.onboarding.status

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.dialog.CieloDialog
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.Action
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.ScreenView.SCREEN_NAME
import br.com.mobicare.cielo.commons.bottomsheet.callhelpcenter.CallHelpCenterBottomSheet
import br.com.mobicare.cielo.commons.constants.FIVE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.dateFormatToBr
import br.com.mobicare.cielo.commons.utils.firstWordCapitalize
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingP1CompletionStatusBinding
import br.com.mobicare.cielo.extensions.moveToHome
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.checkpointP1
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.isShowWarning
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1Analytics
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.*
import br.com.mobicare.cielo.idOnboarding.enum.IDOnboardingComeBackEnum
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class IDOnboardingP1CompletionStatusFragment : BaseFragment(), CieloNavigationListener,
    IDOnboardingP1CompletionStatusContract.View {

    private val presenter: IDOnboardingP1CompletionStatusPresenter by inject { parametersOf(this) }

    private var navigation: CieloNavigation? = null

    private var _binding: FragmentIdOnboardingP1CompletionStatusBinding? = null
    private val binding get() = _binding

    private var comeBack: IDOnboardingComeBackEnum = IDOnboardingComeBackEnum.HOME

    private val analytics: IDOnboardingP1Analytics by inject()
    private val analyticsGA: IDOnboardingP1AnalyticsGA by inject()
    private var analyticsScreenNameGA: String = EMPTY
    private var analyticsScreenName: String = EMPTY
    private var analyticsSubCategory: String = EMPTY
    private var analyticsDays: Int = ZERO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIdOnboardingP1CompletionStatusBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        presenter.onProcessP1CompletionStatus(
            checkpointP1,
            userStatus.p1Flow?.deadlineRemainingDays,
            userStatus.onboardingStatus
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView(
        @DrawableRes image: Int,
        title: String,
        subtitle: String,
        @StringRes firstBtnText: Int,
        @StringRes secondBtnText: Int,
        @ColorRes colorTitle: Int = R.color.brand_600,
        comeBack: IDOnboardingComeBackEnum,
        isShowBtnClose: Boolean = false,
        isShowFirstBtn: Boolean = false
    ) {
        this.comeBack = comeBack
        if (isShowWarning || comeBack == IDOnboardingComeBackEnum.BLOCKED) {
            binding?.apply {
                ivClose.visible(isShowBtnClose)
                btnFirst.visible(isShowFirstBtn)

                ivStatus.setImageResource(image)
                tvTitleStatus.text = html(title)
                tvSubtitleStatus.text = html(subtitle)

                btnFirst.setText(getString(firstBtnText))
                btnSecond.text = getString(secondBtnText)

                tvTitleStatus.setTextColor(ContextCompat.getColor(requireContext(), colorTitle))
            }

            setupListeners()
            setupAdditionalActionButton()
        } else
            gotoP1()
    }

    private fun setupAdditionalActionButton() {
        val isBlocked = comeBack == IDOnboardingComeBackEnum.BLOCKED

        val iconRes = if (isBlocked)
            R.drawable.ic_left_icon_blue_16dp
        else
            R.drawable.ic_help_circle_blue_16dp

        val textRes = if (isBlocked)
            R.string.text_call_center_action
        else
            R.string.id_onboarding_status_continue_p1_action

        binding?.btnActionOnboarding?.apply {
            text = getString(textRes)
            setCompoundDrawablesWithIntrinsicBounds(ZERO, ZERO, iconRes, ZERO)
        }
    }

    private fun html(text: String): SpannableString = SpannableString(
        HtmlCompat
            .fromHtml(
                text,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
    )

    private fun showDialog() {
        val message = getString(
            R.string.id_onboarding_status_update_dialog_subtitle,
            userStatus.p1Flow?.deadlineOn.dateFormatToBr()
        )

        CieloDialog.create(
            title = getString(R.string.id_onboarding_status_update_dialog_title),
            message = message
        ).setTitleTextAlignment(View.TEXT_ALIGNMENT_CENTER)
            .setMessageTextAlignment(View.TEXT_ALIGNMENT_CENTER)
            .setImage(R.drawable.ic_8)
            .setTitleColor(R.color.brand_600)
            .setPrimaryButton(getString(R.string.id_onboarding_status_update_now_dialog))
            .setSecondaryButton(getString(R.string.id_onboarding_status_update_later_dialog))
            .setOnPrimaryButtonClickListener {
                analyticsGA.logIDValidateSignUp(ANALYTICS_ID_SCREEN_VIEW_START_VALIDATION,
                    ANALYTICS_ID_START_VALIDATION)
                gotoP1()
            }.setOnSecondaryButtonClickListener {
                activity?.moveToHome()
            }.show(
                childFragmentManager,
                IDOnboardingP1CompletionStatusFragment::class.java.simpleName
            )
        analyticsGA.logIDValidateLastDaysDisplay(ANALYTICS_ID_SCREEN_VIEW_START_VALIDATION)
    }

    private fun gotoP1() {
        findNavController().navigate(
            presenter.onDestination(
                checkpointP1,
                userStatus.cpf,
                isShowWarning,
                userStatus.onboardingStatus?.userStatus?.foreign
            )
        )
    }

    private fun setupListeners() {
        firstButtonListener()
        secondButtonListener()
        closeButtonListener()
        additionalActionButtonListener()
    }

    private fun firstButtonListener() {
        binding?.btnFirst?.setOnClickListener {
            analytics.logIDStartAlertClick(
                analyticsSubCategory,
                analyticsDays,
                binding?.btnFirst?.getText() ?: EMPTY
            )
            analyticsGA.logIDValidateClick(analyticsScreenNameGA)
            activity?.moveToHome()
        }
    }

    private fun secondButtonListener() {
        binding?.btnSecond?.setOnClickListener {
            analytics.logIDStartAlertClick(
                analyticsSubCategory,
                analyticsDays,
                (binding?.btnSecond?.text ?: EMPTY) as String
            )
            if (comeBack == IDOnboardingComeBackEnum.BLOCKED)
                logout()
            else{
                if (analyticsScreenNameGA.equals(ANALYTICS_ID_SCREEN_VIEW_CONTINUE_VALIDATION)
                    || analyticsScreenNameGA.equals(ANALYTICS_ID_SCREEN_VIEW_BLOCKED_CONTINUE_VALIDATION)){
                    analyticsGA.logIDContinueValidationClick(analyticsScreenNameGA)
                }else{
                    analyticsGA.logIDValidateSignUp(analyticsScreenNameGA,
                        ANALYTICS_ID_START_VALIDATION)
                }
                gotoP1()
            }
        }
    }

    private fun closeButtonListener() {
        binding?.ivClose?.setOnClickListener {
            analytics.logIDStartAlertClick(
                analyticsSubCategory,
                analyticsDays,
                Action.FECHAR
            )
            if (comeBack == IDOnboardingComeBackEnum.HOME)
                activity?.moveToHome()
            else
                showDialog()
        }
    }

    private fun additionalActionButtonListener() {
        binding?.btnActionOnboarding?.setOnClickListener {
            analytics.logIDStartAlertClick(
                analyticsSubCategory,
                analyticsDays,
                (binding?.btnActionOnboarding?.text ?: EMPTY) as String
            )
            if (comeBack == IDOnboardingComeBackEnum.BLOCKED) {
                analyticsGA.logIDBlockedAccessCallHelpCenterClick(analyticsScreenNameGA)

                val result = Bundle()
                result.putString(SCREEN_NAME, ANALYTICS_ID_SCREEN_VIEW_BLOCKED_ACCESS_CALL_HELP_CENTER)
                CallHelpCenterBottomSheet.newInstance().apply{
                    arguments = result
                }.show(childFragmentManager, tag)

            }else
                goToHelpCenter()
        }
    }

    private fun goToHelpCenter() {
        requireActivity().openFaq(
            tag = ConfigurationDef.TAG_HELP_CENTER_IDENTIDADE_DIGITAL,
            subCategoryName = getString(R.string.id_onboarding_name)
        )
    }

    private fun logout() {
        SessionExpiredHandler.userSessionExpires(requireContext(), true)
    }

    private fun name() = firstWordCapitalize(userStatus.name, getString(R.string.hello))

    private fun didNotStartTitle(): String {
        val remainingDays = userStatus.p1Flow?.deadlineRemainingDays ?: ZERO
        analyticsDays = remainingDays.toInt()
        return resources.getQuantityString(
            R.plurals.id_onboarding_status_did_start_title, remainingDays.toInt(), remainingDays
        )
    }

    override fun onStarted(comeBack: IDOnboardingComeBackEnum) {
        val title = getString(
            R.string.id_onboarding_status_continue_p1_title,
            name()
        )
        val subtitle = getString(
            R.string.id_onboarding_status_continue_p1_subtitle,
            userStatus.p1Flow?.deadlineOn.dateFormatToBr()
        )

        setupView(
            image = R.drawable.ic_iniciar_230dp,
            title = title,
            subtitle = subtitle,
            firstBtnText = R.string.id_onboarding_status_update_later,
            secondBtnText = R.string.id_onboarding_status_continue_update,
            comeBack = comeBack,
            isShowFirstBtn = true,
            isShowBtnClose = true
        )

        analyticsScreenNameGA = ANALYTICS_ID_SCREEN_VIEW_CONTINUE_VALIDATION
        analyticsScreenName = ANALYTICS_ID_SCREEN_VIEW_ALERT_UPADTE_CONTINUE
        analyticsSubCategory = ANALYTICS_ID_REMEMBER_UPDATE_CONTINUE
        logScreenView()
        analyticsGA.logIDContinueValidationDisplay(ANALYTICS_ID_SCREEN_VIEW_CONTINUE_VALIDATION)
    }

    override fun onStartedAndIsBlocked(comeBack: IDOnboardingComeBackEnum) {
        val title = getString(
            R.string.id_onboarding_status_blocked_title,
            name()
        )
        setupView(
            image = R.drawable.ic_sem_prazo,
            title = title,
            subtitle = getString(R.string.id_onboarding_status_blocked_subtitle),
            firstBtnText = R.string.id_onboarding_status_update_later,
            secondBtnText = R.string.id_onboarding_status_continue_update,
            comeBack = comeBack
        )

        analyticsScreenNameGA = ANALYTICS_ID_SCREEN_VIEW_BLOCKED_CONTINUE_VALIDATION
        analyticsScreenName = ANALYTICS_ID_SCREEN_VIEW_ALERT_UPADTE_CONTINUE_MANDATORY
        analyticsSubCategory = ANALYTICS_ID_REMEMBER_UPDATE_CONTINUE_MANDATORY
        logScreenView()
    }

    override fun onDidNotStart(comeBack: IDOnboardingComeBackEnum) {
        setupView(
            image = R.drawable.img_preenchimento_dados,
            title = didNotStartTitle(),
            subtitle = getString(R.string.id_onboarding_status_did_start_subtitle),
            firstBtnText = R.string.id_onboarding_status_update_later,
            secondBtnText = R.string.id_onboarding_status_did_start_update,
            comeBack = comeBack,
            isShowFirstBtn = true,
            isShowBtnClose = true
        )

        analyticsSelectScreenNameAndSubCategory()
        analyticsScreenNameGA = ANALYTICS_ID_SCREEN_VIEW_START_VALIDATION
        logScreenView()
    }

    override fun onDidNotStartAndIsBlocked(comeBack: IDOnboardingComeBackEnum) {
        setupView(
            image = R.drawable.img_perfil_impedido,
            title = getString(R.string.id_onboarding_status_did_start_and_blocked_title),
            subtitle = getString(R.string.id_onboarding_status_did_start_and_blocked_subtitle),
            firstBtnText = R.string.id_onboarding_status_update_later,
            secondBtnText = R.string.id_onboarding_status_update_my_data,
            colorTitle = R.color.danger_400,
            comeBack = comeBack
        )

        analyticsScreenNameGA = ANALYTICS_ID_SCREEN_VIEW_START_VALIDATION
        analyticsScreenName = ANALYTICS_ID_SCREEN_VIEW_ALERT_UPADTE_EXPIRED
        analyticsSubCategory = ANALYTICS_ID_REMEMBER_UPDATE_EXPIRED
        logScreenView()
        analyticsGA.logIDValidateDisplay(analyticsScreenNameGA, EMPTY)
    }

    override fun onDidNotStartAndInTheLastDays(comeBack: IDOnboardingComeBackEnum) {
        val subtitle = getString(
            R.string.id_onboarding_status_did_start_and_in_the_last_days_subtitle,
            userStatus.p1Flow?.deadlineOn.dateFormatToBr()
        )

        setupView(
            image = R.drawable.ic_28_preenchimento_ultimos_5,
            title = didNotStartTitle(),
            subtitle = subtitle,
            firstBtnText = R.string.id_onboarding_status_update_later,
            secondBtnText = R.string.id_onboarding_status_did_start_update,
            comeBack = comeBack,
            isShowBtnClose = true
        )

        analyticsSelectScreenNameAndSubCategory()
        analyticsScreenNameGA = ANALYTICS_ID_SCREEN_VIEW_START_VALIDATION
        logScreenView()
    }

    override fun onBlocked(comeBack: IDOnboardingComeBackEnum) {
        setupView(
            image = R.drawable.img_perfil_impedido,
            title = getString(R.string.id_onboarding_status_access_currently_blocked_title),
            subtitle = getString(R.string.id_onboarding_status_access_currently_blocked_message),
            firstBtnText = R.string.text_close,
            secondBtnText = R.string.text_close,
            colorTitle = R.color.danger_400,
            comeBack = comeBack
        )
        analyticsScreenNameGA = ANALYTICS_ID_SCREEN_VIEW_BLOCKED_ACCESS
        analyticsGA.logIDScreenView(analyticsScreenNameGA)
        analyticsGA.logIDBlockedAccessDisplay(analyticsScreenNameGA, EMPTY)
    }

    override fun onBackButtonClicked(): Boolean {
        when (comeBack) {
            IDOnboardingComeBackEnum.HOME -> if (isShowWarning) activity?.moveToHome()
            IDOnboardingComeBackEnum.BLOCKED -> logout()
            IDOnboardingComeBackEnum.LOGOUT -> if (isShowWarning) logout()
            IDOnboardingComeBackEnum.DIALOG -> if (isShowWarning) showDialog()
        }
        return isShowWarning
    }

    private fun logScreenView() {
        analytics.logScreenView(analyticsScreenName, this.javaClass)
        analyticsGA.logIDScreenView(analyticsScreenNameGA)
    }

    private fun analyticsSelectScreenNameAndSubCategory() {
        if (analyticsDays > FIVE ){
            analyticsScreenName = ANALYTICS_ID_SCREEN_VIEW_ALERT_UPADTE_DATA_30_DAYS
            analyticsSubCategory = ANALYTICS_ID_REMEMBER_UPDATE_DATA_30_DAYS
        }else{
            analyticsScreenName = ANALYTICS_ID_SCREEN_VIEW_ALERT_UPADTE_DATA_5_DAYS
            analyticsSubCategory = ANALYTICS_ID_REMEMBER_UPDATE_DATA_5_DAYS
        }
    }
}