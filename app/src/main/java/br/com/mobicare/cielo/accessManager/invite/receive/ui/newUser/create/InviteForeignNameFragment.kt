package br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.showSoftKeyboard
import br.com.mobicare.cielo.databinding.FragmentInviteForeignUserBinding

class InviteForeignNameFragment : BaseFragment(), CieloNavigationListener {
    private var navigation: CieloNavigation? = null
    val args: InviteForeignNameFragmentArgs by navArgs()
    private var binding: FragmentInviteForeignUserBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentInviteForeignUserBinding
        .inflate(inflater, container, false)
        .also { binding = it }.root

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
        textNameConfiguration()
        binding?.apply {
            btBackArrow.setOnClickListener {
                requireActivity().onBackPressed()
            }

            tvName.doOnTextChanged { text, _, _, _ ->
                nextButtonForeignName.isEnabled = text.isNullOrEmpty().not()
            }

            nextButtonForeignName.setOnClickListener {
                binding?.tvName?.text?.let {
                    args.invitedetailsargs.foreignName = it.toString().trim()
                }
                findNavController().navigate(
                    InviteForeignNameFragmentDirections.actionInviteForeignNameFragmentToVerifyUserDataFragment(
                        args.invitetokenargs,
                        args.invitedetailsargs
                    )
                )
            }
        }
    }

    private fun textNameConfiguration() {
        binding?.apply {
            tvName.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            tvName.requestFocus()
            requireActivity().showSoftKeyboard(tvName)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}