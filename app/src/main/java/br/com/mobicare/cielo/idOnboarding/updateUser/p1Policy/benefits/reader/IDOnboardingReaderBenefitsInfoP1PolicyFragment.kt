package br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy.benefits.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ARG_USER_CPF_OR_EMAIL_WAS_CHANGED
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.constants.SEPARATOR
import br.com.mobicare.cielo.commons.helpers.openFaq
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.ValidationUtils.isCPF
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingReaderAccessBinding
import br.com.mobicare.cielo.extensions.*
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2Analytics
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_ACCESS_ACCOUNT
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_COMPLETE_PROFILE_SETUP
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_SUCCESS
import br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy.IDOnboardingValidateP1PolicyContract
import br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy.benefits.IDOnboardingBenefitsInfoP1PolicyPresenter
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationDef
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class IDOnboardingReaderBenefitsInfoP1PolicyFragment : BaseFragment(),
    CieloNavigationListener, IDOnboardingValidateP1PolicyContract.View {

    private val presenter: IDOnboardingBenefitsInfoP1PolicyPresenter by inject { parametersOf(this) }
    private var navigation: CieloNavigation? = null
    private var binding: FragmentIdOnboardingReaderAccessBinding? = null
    private val analytics: IDOnboardingP2Analytics by inject()
    private val analyticsGA: IDOnboardingP1AnalyticsGA by inject()

    private val isCpfOrEmailChanged: Boolean? by lazy {
        arguments?.getBoolean(ARG_USER_CPF_OR_EMAIL_WAS_CHANGED)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIdOnboardingReaderAccessBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
        setupView()
        analytics.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_COMPLETE_PROFILE_SETUP + UserObj.MainRoleEnum.READER.description , this.javaClass)
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_SUCCESS)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupListeners() {
        binding?.btAction?.setOnClickListener {
            analyticsGA.logIDValidateCodeClick(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_SUCCESS,
                ANALYTICS_ID_ACCESS_ACCOUNT)
            baseLogout()
        }

        binding?.btGoToHelpCenter?.setOnClickListener {
            goToOnboardingHelpCenter()
        }
    }

    private fun goToOnboardingHelpCenter() {
        requireActivity().openFaq(
            tag = ConfigurationDef.TAG_HELP_CENTER_IDENTIDADE_DIGITAL,
            subCategoryName = getString(R.string.id_onboarding_name)
        )
    }

    private fun setupView() {
        val tradeName = presenter.fetchTradeName()
        val documentNumber = presenter.fetchCnpj()

        binding?.apply {
            includePersonalInfo.tvRoleName.text = getString(R.string.access_manager_role_group_card_description_reader_title)
            includePersonalInfo.ivImage.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.img_user_validated, null))

            includePersonalInfo.tvTitleWithPrimaryName.fromHtml(
                R.string.id_onboarding_p1_viewer_finish_validation,
                userStatus.name?.substringBefore(ONE_SPACE).toLowerCasePTBR().capitalizePTBR()
            )

            if(isCPF(documentNumber)) {
                customizePersonalDataInfo(
                    R.string.id_onboarding_access_profile_cpf_information_title,
                    tradeName,
                    documentNumber)
            } else {
                customizePersonalDataInfo(
                    R.string.id_onboarding_access_profile_information_title,
                    tradeName,
                    documentNumber.replaceAfter(SEPARATOR, getString(R.string.cnpj_mask))
                )
            }

            if (isUserChangedData()) {
                tvDataUpdatedInfo.visible()
                btAction.text = getString(R.string.id_onboarding_cpf_already_used_logout_action)
                helpContainerGroup.gone()
            }
        }
    }

    private fun customizePersonalDataInfo(@StringRes message: Int, tradeName: String, documentNumber: String) {
        binding?.includePersonalInfo?.tvAccessProfile?.fromHtml(
            message,
            tradeName,
            documentNumber
        )
    }

    private fun isUserChangedData() = isCpfOrEmailChanged == true
    private fun goToHome() = activity?.moveToHome() ?: baseLogout()

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
