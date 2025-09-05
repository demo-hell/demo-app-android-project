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
import br.com.mobicare.cielo.databinding.FragmentIdOnboardingSelectDigitalDocBinding
import br.com.mobicare.cielo.idOnboarding.IDOnboardingFlowHandler

class IDOnboardingSelectDigitalDocFragment : BaseFragment(), CieloNavigationListener {

    private var navigation: CieloNavigation? = null
    private var binding: FragmentIdOnboardingSelectDigitalDocBinding? = null
    private val isCNH by lazy {
        IDOnboardingFlowHandler.userStatus.documentType?.get(ZERO)
            ?.uppercase() == IDOnboardingFlowHandler.CNH
    }
    private var digitalDocumentSelect: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentIdOnboardingSelectDigitalDocBinding.inflate(
        inflater,
        container,
        false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupView()
    }

    override fun onResume() {
        super.onResume()

        binding?.apply {
            if (digitalDocumentSelect) {
                clDigitalDocument.callOnClick()
            } else {
                clPhysicalDocument.callOnClick()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        binding?.apply {
            val subtitle = requireContext().getString(
                if (isCNH)
                    R.string.id_onboarding_select_digital_doc_physical_cnh_subtitle
                else
                    R.string.id_onboarding_select_digital_doc_physical_rg_subtitle
            )

            tvPhysicalDocumentDesc.text = subtitle

            tvDigitalDocumentDesc.text = subtitle

            val imageResource = if (isCNH)
                R.drawable.img_61_cnh_posicao_celular
            else
                R.drawable.img_62_rg_posicao_celular

            ivDocType.setImageResource(imageResource)

            btBackArrow.setOnClickListener {
                findNavController().popBackStack()
            }

            clPhysicalDocument.setOnClickListener {
                digitalDocumentSelect = false
                setupButtonSelect(
                    clPhysicalDocument,
                    listOf(clDigitalDocument),
                    rbPhysicalDocument,
                    listOf(rbDigitalDocument),
                    imageResource
                )
            }

            clDigitalDocument.setOnClickListener {
                digitalDocumentSelect = true
                setupButtonSelect(
                    clDigitalDocument,
                    listOf(clPhysicalDocument),
                    rbDigitalDocument,
                    listOf(rbPhysicalDocument),
                    imageResource
                )
            }

            btNext.setOnClickListener {
                if (digitalDocumentSelect) {
                    findNavController().navigate(
                        IDOnboardingSelectDigitalDocFragmentDirections
                            .actionIdOnboardingSelectDigitalDocFragmentToIdOnboardingDigitalDocGuideFragment()
                    )
                } else {
                    findNavController().navigate(
                        IDOnboardingSelectDigitalDocFragmentDirections
                            .actionIdOnboardingSelectDigitalDocFragmentToIdOnboardingPicturesDocGuideFragment()
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
        @DrawableRes ivDocTypeRes: Int = R.drawable.img_rg
    ) {
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

            clPhysicalDocument.requestLayout()
            clDigitalDocument.requestLayout()
        }
    }
}