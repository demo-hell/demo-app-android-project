package br.com.mobicare.cielo.accessManager.addUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.text.parseAsHtml
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentAccessManagerAddUserTypeBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference

class AccessManagerAddUserTypeFragment : BaseFragment(), CieloNavigationListener {
    private var navigation: CieloNavigation? = null
    val args: AccessManagerAddUserTypeFragmentArgs by navArgs()
    private var _binding: FragmentAccessManagerAddUserTypeBinding? = null
    private val binding get() = _binding
    private var selectedRole: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccessManagerAddUserTypeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun getRole(): String {
        val radioButtonID: Int? = binding?.roleType?.radioGroupAccountType?.checkedRadioButtonId
        val radioButton: View? = radioButtonID?.let {
            binding?.roleType?.radioGroupAccountType?.findViewById(radioButtonID)
        }
        return when (binding?.roleType?.radioGroupAccountType?.indexOfChild(radioButton)) {
            ADMIN -> ADMINROLE
            READER -> READERROLE
            ANALYST -> ANALYSTROLE
            TECHNICAL -> TECHNICALROLE
            else -> {
                ANALYSTROLE
            }
        }

    }

    private fun setupListeners() {
        binding?.roleType?.radioGroupAccountType?.setOnCheckedChangeListener { radioGroup, _ ->
            val selectedRoleView =
                radioGroup.findViewById<AppCompatRadioButton>(radioGroup.checkedRadioButtonId)
            selectedRoleView.setBackgroundResource(R.drawable.shape_solid_white_border_color_blue)
            when (radioGroup.indexOfChild(selectedRoleView)) {
                ZERO -> {
                    binding?.roleType?.rbAdmin?.text =
                        context?.getString(R.string.access_manager_role_group_card_admin_description_subtitle_selected)
                            ?.parseAsHtml()
                    binding?.roleType?.rbReader?.text =
                        context?.getString(R.string.access_manager_role_group_card_reader_description_subtitle)
                            ?.parseAsHtml()
                    binding?.roleType?.rbAnalyst?.text =
                        context?.getString(R.string.access_manager_role_group_card_analyst_description_subtitle)
                            ?.parseAsHtml()
                    binding?.roleType?.rbTechnical?.text =
                        context?.getString(R.string.access_manager_role_group_card_technical_description_subtitle)
                            ?.parseAsHtml()
                    binding?.roleType?.radioGroupAccountType?.get(ONE)
                        ?.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                    binding?.roleType?.radioGroupAccountType?.get(TWO)
                        ?.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                    binding?.roleType?.radioGroupAccountType?.get(THREE)
                        ?.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                }
                ONE -> {
                    binding?.roleType?.rbReader?.text =
                        context?.getString(R.string.access_manager_role_group_card_reader_description_subtitle_selected)
                            ?.parseAsHtml()
                    binding?.roleType?.rbAdmin?.text =
                        context?.getString(R.string.access_manager_role_group_card_admin_description_subtitle)
                            ?.parseAsHtml()
                    binding?.roleType?.rbAnalyst?.text =
                        context?.getString(R.string.access_manager_role_group_card_analyst_description_subtitle)
                            ?.parseAsHtml()
                    binding?.roleType?.rbTechnical?.text =
                        context?.getString(R.string.access_manager_role_group_card_technical_description_subtitle)
                            ?.parseAsHtml()
                    binding?.roleType?.radioGroupAccountType?.get(ZERO)
                        ?.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                    binding?.roleType?.radioGroupAccountType?.get(TWO)
                        ?.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                    binding?.roleType?.radioGroupAccountType?.get(THREE)
                        ?.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                }
                TWO -> {
                    binding?.roleType?.rbAnalyst?.text =
                        context?.getString(R.string.access_manager_role_group_card_analyst_description_subtitle_selected)
                            ?.parseAsHtml()
                    binding?.roleType?.rbAdmin?.text =
                        context?.getString(R.string.access_manager_role_group_card_admin_description_subtitle)
                            ?.parseAsHtml()
                    binding?.roleType?.rbReader?.text =
                        context?.getString(R.string.access_manager_role_group_card_reader_description_subtitle)
                            ?.parseAsHtml()
                    binding?.roleType?.rbTechnical?.text =
                        context?.getString(R.string.access_manager_role_group_card_technical_description_subtitle)
                            ?.parseAsHtml()
                    binding?.roleType?.radioGroupAccountType?.get(ZERO)
                        ?.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                    binding?.roleType?.radioGroupAccountType?.get(ONE)
                        ?.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                    binding?.roleType?.radioGroupAccountType?.get(THREE)
                        ?.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                }
                THREE -> {
                    binding?.roleType?.rbTechnical?.text =
                        context?.getString(R.string.access_manager_role_group_card_technical_description_subtitle_selected)
                            ?.parseAsHtml()
                    binding?.roleType?.rbAdmin?.text =
                        context?.getString(R.string.access_manager_role_group_card_admin_description_subtitle)
                            ?.parseAsHtml()
                    binding?.roleType?.rbReader?.text =
                        context?.getString(R.string.access_manager_role_group_card_reader_description_subtitle)
                            ?.parseAsHtml()
                    binding?.roleType?.rbAnalyst?.text =
                        context?.getString(R.string.access_manager_role_group_card_analyst_description_subtitle)
                            ?.parseAsHtml()
                    binding?.roleType?.radioGroupAccountType?.get(ZERO)
                        ?.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                    binding?.roleType?.radioGroupAccountType?.get(ONE)
                        ?.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                    binding?.roleType?.radioGroupAccountType?.get(TWO)
                        ?.setBackgroundResource(R.drawable.shape_solid_white_border_color_c5ced7)
                }
            }
        }

        binding?.btBackArrow?.setOnClickListener {
            activity?.onBackPressed()
        }
        binding?.roleType?.rbAdmin?.text =
            context?.getString(R.string.access_manager_role_group_card_admin_description_subtitle_selected)
                ?.parseAsHtml()
        binding?.roleType?.radioGroupAccountType?.get(ZERO)
            ?.setBackgroundResource(R.drawable.shape_solid_white_border_color_blue)
        binding?.roleType?.rbReader?.text =
            context?.getString(R.string.access_manager_role_group_card_reader_description_subtitle)
                ?.parseAsHtml()
        binding?.roleType?.rbAnalyst?.text =
            context?.getString(R.string.access_manager_role_group_card_analyst_description_subtitle)
                ?.parseAsHtml()
        binding?.roleType?.rbTechnical?.text =
            context?.getString(R.string.access_manager_role_group_card_technical_description_subtitle)
                ?.parseAsHtml()
        binding?.btBackArrow?.setOnClickListener {
            activity?.onBackPressed()
        }

        binding?.roleType?.rbTechnical?.visible(FeatureTogglePreference.instance.getFeatureTogle(FeatureTogglePreference.PERFIL_TECNICO))

        binding?.NextButton?.setOnClickListener {
            if (getRole() == TECHNICALROLE) {
                showTechnicalBottomSheet()
            } else {
                goNextStep()
            }
        }
    }

    private fun showTechnicalBottomSheet() {
        navigation?.showCustomBottomSheet(
            image = R.drawable.ic_10,
            title = getString(R.string.alert_error_date_title),
            message = getString(R.string.bottom_sheet_technical_invite_mensage),
            bt1Title = getString(R.string.select_other_role),
            bt2Title = getString(R.string.continuar),
            bt2Callback = {
                goNextStep()
                false
            },
            isPhone = false
        )
    }

    private fun goNextStep(){
        findNavController().navigate(
            AccessManagerAddUserTypeFragmentDirections.
            accessManagerAddUserTypeFragmentToAccessManagerAddUserCpfFragment(
                args.isforeignargs, args.nationalitycodeargs, getRole())
        )
    }

    companion object {
        private const val ADMIN = 0
        private const val READER = 1
        private const val ANALYST = 2
        private const val TECHNICAL = 3

        private const val ADMINROLE = "ADMIN"
        private const val READERROLE = "READER"
        private const val ANALYSTROLE = "ANALYST"
        private const val TECHNICALROLE = "TECHNICAL"
    }
}