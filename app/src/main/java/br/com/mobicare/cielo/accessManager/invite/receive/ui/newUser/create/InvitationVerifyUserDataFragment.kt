package br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base.InviteReceiveBaseFragment
import br.com.mobicare.cielo.databinding.FragmentInviteAccountCreateVerifyUserDataBinding

class InvitationVerifyUserDataFragment : InviteReceiveBaseFragment() {

    private var binding: FragmentInviteAccountCreateVerifyUserDataBinding? = null
    val args: InvitationVerifyUserDataFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentInviteAccountCreateVerifyUserDataBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            if (args.invitedetailsargs.cpf.isNullOrEmpty()) {
                tvCPF.gone()
                tvCPFNumber.gone()
            } else {
                tvCPFNumber.text = args.invitedetailsargs.cpf
            }

            if (args.invitedetailsargs.foreignName.isNullOrEmpty()){
                tvName.gone()
                tvForeignName.gone()
            } else {
                tvForeignName.text = args.invitedetailsargs.foreignName
            }

            tvEmailData.text = args.invitedetailsargs.email
            btBackArrow.setOnClickListener {
                findNavController().navigateUp()
            }

            btnNext.setOnClickListener {
                findNavController().navigate(
                    InvitationVerifyUserDataFragmentDirections.actionVerifyUserDataFragmentToTermAcceptFragment(
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
}