package br.com.mobicare.cielo.idOnboarding.updateUser.docPictures

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.firstWordCapitalize
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingPicturesSelectDocFragmentBinding
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.CNH
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.CNH2022
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.DNI
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.RG
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.CRNM
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler.userStatus
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2Analytics
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_DOCUMENT_YOU_WANT_TO_SEND
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_DOCUMENT_YOU_WANT_TO_SEND
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_SELECT_DOCUMENT_TYPE
import br.com.mobicare.cielo.idOnboarding.analytics.constants.ANALYTICS_ID_SCREEN_VIEW_SEND_PICTURES_SEND_LATER
import org.koin.android.ext.android.inject

class IDOnboardingPicturesSelectDocFragment : BaseFragment(), CieloNavigationListener {

    private val analytics: IDOnboardingP2Analytics by inject()
    private val analyticsGA: IDOnboardingP2AnalyticsGA by inject()
    private var navigation: CieloNavigation? = null

    private var _binding: FragmentIdOnboardingPicturesSelectDocFragmentBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentIdOnboardingPicturesSelectDocFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupText()
        setupListeners()
        analytics.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_DOCUMENT_YOU_WANT_TO_SEND, this.javaClass)
        analyticsGA.logIDScreenView(ANALYTICS_ID_SCREEN_VIEW_SELECT_DOCUMENT_TYPE)
    }

    override fun onResume() {
        super.onResume()

        binding?.apply {
            val documentType = userStatus.documentType?.get(ZERO)?.uppercase() ?: DNI
            analytics.logIDOnSelectDocument(documentType)
            analyticsGA.logIDSelectDocumentSignUp(documentType)

            when (documentType) {
                CNH -> clCNHButton.callOnClick()
                RG -> clRGButton.callOnClick()
                else -> clUniqueRGButton.callOnClick()
            }
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupText() {
        binding?.tvTitle?.text =
            getString(R.string.id_onboarding_pictures_select_doc_title, firstWordCapitalize(userStatus.name, getString(R.string.hello)))
    }

    private fun setupListeners() {
        binding?.apply {

            btBackArrow.setOnClickListener {
                analytics.logIDOnClickComeBack(ANALYTICS_ID_DOCUMENT_YOU_WANT_TO_SEND)
                activity?.onBackPressed()
            }

            clRGButton.setOnClickListener {
                analyticsGA.logIDSelectDocumentSignUp(RG)
                userStatus.documentType = arrayOf(RG.toLowerCasePTBR())
                setupButtonSelect(
                    clRGButton,
                    listOf(clCNHButton, clUniqueRGButton, clRneButton),
                    rbRG,
                    listOf(rbCNH, rbUniqueRG, rbRne),
                    documentType = RG
                )
            }

            clUniqueRGButton.setOnClickListener {
                analyticsGA.logIDSelectDocumentSignUp(DNI)
                userStatus.documentType = arrayOf(DNI.toLowerCasePTBR())
                setupButtonSelect(
                    clUniqueRGButton,
                    listOf(clCNHButton, clRGButton, clRneButton),
                    rbUniqueRG,
                    listOf(rbCNH, rbRG, rbRne),
                    R.drawable.img_124_rg_unico,
                    DNI
                )
            }

            clCNHButton.setOnClickListener {
                analyticsGA.logIDSelectDocumentSignUp(CNH)
                userStatus.documentType = arrayOf(CNH.toLowerCasePTBR(), CNH2022.toLowerCasePTBR())
                setupButtonSelect(
                    clCNHButton,
                    listOf(clRGButton, clUniqueRGButton, clRneButton),
                    rbCNH,
                    listOf(rbRG, rbUniqueRG, rbRne),
                    R.drawable.img_124_rg_unico,
                    CNH
                )
            }

            clRneButton.setOnClickListener {
                userStatus.documentType = arrayOf(CRNM.toLowerCasePTBR())
                setupButtonSelect(
                    clRneButton,
                    listOf(clRGButton, clUniqueRGButton, clCNHButton),
                    rbRne,
                    listOf(rbRG, rbUniqueRG, rbCNH),
                    R.drawable.img_59_cnh_plastificada,
                    CRNM
                )
            }

            btNext.setOnClickListener {
                analytics.logIDOnClickNext(ANALYTICS_ID_DOCUMENT_YOU_WANT_TO_SEND)
                if (userStatus.documentType?.get(ZERO)?.uppercase() == CRNM){
                    findNavController().navigate(
                        IDOnboardingPicturesSelectDocFragmentDirections
                            .actionIdOnboardingPicturesSelectDocFragmentToIdOnboardingPicturesDocGuideFragment()
                    )
                } else {
                    findNavController().navigate(
                        IDOnboardingPicturesSelectDocFragmentDirections
                            .actionIdOnboardingPicturesSelectDocFragmentToIdOnboardingSelectDigitalDocFragment()
                    )

                }
            }
        }
    }

    private fun setupButtonSelect(
        btnSelected: ConstraintLayout,
        btnUnselectedList: List<ConstraintLayout>,
        rbSelected: AppCompatRadioButton,
        rbUnselectedList: List<AppCompatRadioButton>,
        @DrawableRes ivDocTypeRes: Int = R.drawable.img_rg,
        documentType: String
    ) {
        analytics.logIDOnSelectDocument(documentType)
        btnSelected.setBackgroundResource(R.drawable.background_border_blue)
        rbSelected.isChecked = true

        btnUnselectedList.forEach {
            it.setBackgroundResource(R.drawable.rounded_outline_display_200)
        }
        rbUnselectedList.forEach {
            it.isChecked = false
        }

        binding?.apply {
            ivDocType.setImageResource(ivDocTypeRes)

            clCNHButton.requestLayout()
            clRGButton.requestLayout()
            clUniqueRGButton.requestLayout()
        }
    }
}