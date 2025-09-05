package br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.invite.receive.domain.InviteDetails
import br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base.InviteReceiveBaseFragment
import br.com.mobicare.cielo.databinding.FragmentInviteNewUserAcceptBinding
import br.com.mobicare.cielo.login.domains.entities.UserObj

class InviteNewUserAcceptFragment : InviteReceiveBaseFragment() {

    private var binding: FragmentInviteNewUserAcceptBinding? = null
    val args: InviteNewUserAcceptFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentInviteNewUserAcceptBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMessage(args.invitedetailsargs)
        setupButtons(args.invitedetailsargs)
    }

    private fun setupButtons(inviteDetails: InviteDetails) {
        binding?.apply {
            btnAcceptLater.setOnClickListener {
                showAcceptLaterBS()
            }

            btnAcceptNow.setOnClickListener {
                if (inviteDetails.foreign && inviteDetails.cpf.isNullOrEmpty()){
                    findNavController().navigate(
                        InviteNewUserAcceptFragmentDirections.actionInviteNewUserAcceptFragmentToInviteForeignNameFragment(
                            args.invitetokenargs,
                            inviteDetails
                        )
                    )
                } else {
                    findNavController().navigate(
                        InviteNewUserAcceptFragmentDirections.actionInviteAcceptFragmentToVerifyUserDataFragment(
                            args.invitetokenargs,
                            inviteDetails
                        )
                    )
                }
            }
        }
    }

    private fun showAcceptLaterBS() {
        navigation?.showCustomBottomSheet(
            title = getString(R.string.access_manager_answer_invitation_bs_accept_later_title),
            message = getString(R.string.access_manager_answer_invitation_bs_accept_later_new_user_message),
            bt1Title = getString(R.string.access_manager_answer_invitation_bs_accept_later_btn_accept_later),
            bt2Title = getString(R.string.access_manager_answer_invitation_bs_accept_later_btn_accept),
            bt1Callback = {
                goToLogin()
                false
            },
            bt2Callback = {
                findNavController().navigate(
                    InviteNewUserAcceptFragmentDirections.actionInviteAcceptFragmentToVerifyUserDataFragment(
                        args.invitetokenargs,
                        args.invitedetailsargs
                    )
                )
                false
            }
        )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setupMessage(inviteDetails: InviteDetails) {
        val roleName = when (inviteDetails.role) {
            UserObj.ADMIN -> getString(R.string.access_manager_admins)
            UserObj.ANALYST -> getString(R.string.access_manager_analyst)
            UserObj.TECHNICAL -> getString(R.string.access_manager_technical)
            else -> getString(R.string.access_manager_readers)
        }
        binding?.tvMessage?.text =
            getString(R.string.access_manager_welcome_message, roleName)
    }
}