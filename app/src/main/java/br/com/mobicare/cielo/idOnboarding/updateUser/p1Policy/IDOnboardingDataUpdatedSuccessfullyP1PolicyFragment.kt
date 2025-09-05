package br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ARG_USER_CPF_OR_EMAIL_WAS_CHANGED
import br.com.mobicare.cielo.commons.constants.ARG_USER_ROLE
import br.com.mobicare.cielo.commons.helpers.EditTextHelper.Companion.cpfMaskFormatter
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.phone
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingSuccessfullyValidatedDataBinding
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_ACCESS_ACCOUNT
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_VALIDATION_SUCCESS
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_VALIDATED_DATA
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class IDOnboardingDataUpdatedSuccessfullyP1PolicyFragment : BaseFragment(),
    CieloNavigationListener, IDOnboardingValidateP1PolicyContract.View {

    private val presenter: IDOnboardingValidateP1PolicyPresenter by inject { parametersOf(this) }
    private var navigation: CieloNavigation? = null
    private var binding: FragmentIdOnboardingSuccessfullyValidatedDataBinding? = null
    private val analyticsGA: IDOnboardingP1AnalyticsGA by inject()

    private val userRole: String? by lazy {
        arguments?.getString(ARG_USER_ROLE)
    }

    private val isCpfOrEmailChanged: Boolean? by lazy {
        arguments?.getBoolean(ARG_USER_CPF_OR_EMAIL_WAS_CHANGED)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIdOnboardingSuccessfullyValidatedDataBinding.inflate(
            layoutInflater,
            container,
            false
        )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
        setupView()
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_SUCCESS)
        analyticsGA.logIDValidateSignUp(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_SUCCESS,
            ANALYTICS_ID_VALIDATED_DATA)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupListeners() {
        binding?.btCallToAction?.setOnClickListener {
            analyticsGA.logIDValidateCodeClick(ANALYTICS_ID_SCREEN_VIEW_VALIDATION_SUCCESS,
                ANALYTICS_ID_ACCESS_ACCOUNT)
            when (userRole) {
                MainRole.ADMIN,
                MainRole.ANALYST,
                MainRole.TECHNICAL,
                MainRole.CUSTOM -> setupFlow()
                else -> goToReaderBenefits()
            }
        }
    }

    private fun setupFlow() {
        if (isUserChangedData())
            presenter.saveNewCpfToShowOnLogin()
        else
            navigateToBenefits()
    }

    private fun navigateToBenefits() {
        when (userRole) {
            MainRole.ADMIN -> goToAdminBenefits()
            MainRole.ANALYST -> goToAnalystBenefits()
            MainRole.TECHNICAL -> goToTechnicalBenefits()
            MainRole.CUSTOM -> goToCustomBenefits()
        }
    }

    private fun setupView() {
        binding?.apply {
            userStatus.apply {
                tvEmailValue.text = email
                if (cellphone.isNullOrEmpty()){
                    tvPhoneTitle.gone()
                    tvPhoneValue.gone()
                    ivThirdIcon.gone()
                }else{
                    tvPhoneTitle.visible()
                    tvPhoneValue.visible()
                    ivThirdIcon.visible()
                    cellphone?.let {
                        tvPhoneValue.text = if (userStatus.onboardingStatus?.userStatus?.foreign == true) it else it.phone()
                    }
                }

                if (userStatus.onboardingStatus?.userStatus?.foreign == true){
                    tvCpfTitle.text = getString(R.string.nome)
                    userStatus.name?.let {
                        tvCpfValue.text = it
                    }
                } else {
                    cpf?.let { itCpf ->
                        tvCpfValue.text = cpfMaskFormatter(itCpf).formattedText.string
                    }
                }
            }
        }
    }

    private fun goToReaderBenefits() {
        findNavController().navigate(
            if (userStatus.onboardingStatus?.userStatus?.foreign == true){
                IDOnboardingDataUpdatedSuccessfullyP1PolicyFragmentDirections.actionToIdOnboardingP2ForeignSuccessFragment()
            } else {
                IDOnboardingDataUpdatedSuccessfullyP1PolicyFragmentDirections.actionIdOnboardingDataUpdatedSuccessfullyP1PolicyFragmentToIdOnboardingReaderBenefitsFragment(
                    isUserChangedData()
                )
            }
        )
    }

    private fun goToAdminBenefits() {
        findNavController().navigate(IDOnboardingDataUpdatedSuccessfullyP1PolicyFragmentDirections.actionIdOnboardingDataUpdatedSuccessfullyP1PolicyFragmentToIdOnboardingAdminBenefitsFragment())
    }

    private fun goToAnalystBenefits() {
        findNavController().navigate(IDOnboardingDataUpdatedSuccessfullyP1PolicyFragmentDirections.actionIdOnboardingDataUpdatedSuccessfullyP1PolicyFragmentToIdOnboardingAnalystBenefitsFragment())
    }

    private fun goToTechnicalBenefits() {
        findNavController().navigate(IDOnboardingDataUpdatedSuccessfullyP1PolicyFragmentDirections.actionIdOnboardingDataUpdatedSuccessfullyP1PolicyFragmentToIdOnboardingTechnicalBenefitsFragment())
    }

    private fun goToCustomBenefits() {
        findNavController().navigate(IDOnboardingDataUpdatedSuccessfullyP1PolicyFragmentDirections.actionIdOnboardingDataUpdatedSuccessfullyP1PolicyFragmentToIdOnboardingCustomBenefitsFragment())
    }

    private fun isUserChangedData() = isCpfOrEmailChanged == true

    override fun onLogout() {
        baseLogout()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}