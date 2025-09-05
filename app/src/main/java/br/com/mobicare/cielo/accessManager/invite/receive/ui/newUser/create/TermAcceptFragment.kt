package br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base.InviteReceiveBaseFragment
import br.com.mobicare.cielo.databinding.FragmentTermAndConditionBinding

class TermAcceptFragment : InviteReceiveBaseFragment() {

    private var binding: FragmentTermAndConditionBinding? = null
    val args: TermAcceptFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentTermAndConditionBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding?.apply {
            llReadTermsLink.setOnClickListener {
                showTerms()
            }
            llcheckboxArea.setOnClickListener {
                checkBoxTerms.toggle()
            }

            checkBoxTerms.setOnCheckedChangeListener { _, isChecked ->
                btnNext.isEnabled = isChecked
            }

            btnNext.setOnClickListener {
                findNavController().navigate(
                    TermAcceptFragmentDirections.actionTermAcceptFragmentToPasswordCreateFragment(
                        args.invitetokenargs, args.invitedetailsargs
                    )
                )
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun showTerms() =
        findNavController().navigate(
            TermAcceptFragmentDirections.actionTermAcceptFragmentToPdfViewFragment(
                BuildConfig.CONDITION_TERMS_URL, args.invitedetailsargs
            )
        )
}