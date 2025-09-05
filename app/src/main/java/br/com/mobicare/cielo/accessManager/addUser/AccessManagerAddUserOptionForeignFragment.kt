package br.com.mobicare.cielo.accessManager.addUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentAccessManagerAddUserOptionForeignBinding

class AccessManagerAddUserOptionForeignFragment : BaseFragment(), CieloNavigationListener {
    private var navigation: CieloNavigation? = null
    private var foreign: Boolean = false
    private var _binding: FragmentAccessManagerAddUserOptionForeignBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentAccessManagerAddUserOptionForeignBinding.inflate(inflater, container, false)
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

    private fun setupListeners() {

        binding?.apply {

            btBackArrow.setOnClickListener {
                findNavController().popBackStack(R.id.accessManagerHomeFragment, false)
            }

            rgOption.setOnCheckedChangeListener { radioGroup, _ ->
                val selectedView =
                    radioGroup.findViewById<AppCompatRadioButton>(radioGroup.checkedRadioButtonId)
                selectedView.setBackgroundResource(R.drawable.shape_solid_white_border_color_blue)
                when (radioGroup.indexOfChild(selectedView)) {
                    ZERO -> {
                        radioGroup?.get(ONE)
                            ?.setBackgroundResource(R.drawable.background_white_stroke_c5ced7_1_radius_8)
                        foreign = false
                    }
                    ONE -> {
                        radioGroup?.get(ZERO)
                            ?.setBackgroundResource(R.drawable.background_white_stroke_c5ced7_1_radius_8)
                        foreign = true
                    }
                }
            }

            rbBrazilian.isChecked = foreign.not()
            rbForeign.isChecked = foreign

            btnNext.setOnClickListener {
                if (rbBrazilian.isChecked) {
                    findNavController().navigate(
                        AccessManagerAddUserOptionForeignFragmentDirections.
                        actionAccessManagerAddUserOptionForeignFragmentToAccessManagerAddUserTypeFragment(
                            false, EMPTY
                        )
                    )
                } else {
                    findNavController().navigate(
                        AccessManagerAddUserOptionForeignFragmentDirections.actionAccessManagerAddUserOptionForeignFragmentToAccessManagerAddUserNationalityFragment()
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}