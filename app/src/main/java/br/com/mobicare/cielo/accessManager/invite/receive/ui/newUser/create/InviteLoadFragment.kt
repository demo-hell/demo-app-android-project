package br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base.InviteReceivePresenter
import br.com.mobicare.cielo.accessManager.invite.receive.domain.InviteDetails
import br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base.InviteReceiveBaseFragment
import br.com.mobicare.cielo.databinding.FragmentInviteLoadDetailsBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.fromHtml
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class InviteLoadFragment : InviteReceiveBaseFragment() {

    private var binding: FragmentInviteLoadDetailsBinding? = null
    val args: InviteLoadFragmentArgs by navArgs()

    private val presenter: InviteReceivePresenter by inject {
        parametersOf(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentInviteLoadDetailsBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root
    }

    override fun onInviteDetails(inviteDetails: InviteDetails) {
        if (inviteDetails.userExists) {
            if (inviteDetails.unauthenticatedAnswerMandatory){
                showAnswerAcceptInvite(inviteDetails.companyName)
            }else{
                findNavController().navigate(
                    InviteLoadFragmentDirections.actionInviteLoadFragmentToInviteLegacyUserAcceptFragment(
                        args.invitetokenargs,
                        inviteDetails
                    )
                )
            }
        } else {
            findNavController().navigate(
                InviteLoadFragmentDirections.actionInviteLoadFragmentToInviteNewUserAcceptFragment(
                    args.invitetokenargs,
                    inviteDetails
                )
            )
        }
    }

    override fun showLoading() {
        doWhenResumed {
            navigation?.showLoading(true, R.string.loading_validating_infos)
        }
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        presenter.getInviteData(args.invitetokenargs)
    }

    fun showAnswerAcceptInvite(companyName: String) {
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_131_gestao_acesso,
            title = getString(R.string.access_manager_answer_accept_invitate_title, companyName).fromHtml().toString(),
            message = getString(R.string.access_manager_answer_accept_invitate_message),
            bt1Title = getString(R.string.access_manager_answer_accept_invitate_no),
            bt1Callback = {
                showConfirmDeclineInvite(companyName)
                false
            },
            bt2Title = getString(R.string.access_manager_answer_accept_invitate_yes),
            bt2Callback = {
                presenter.acceptInviteToken(args.invitetokenargs)
                false
            },
            closeCallback = {
                baseLogout()
            },
            isCancelable = false
        )
    }

    fun showConfirmDeclineInvite(companyName: String){
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_10_erro,
            title = getString(R.string.access_manager_confirm_decline_title).fromHtml().toString(),
            message = getString(R.string.access_manager_confirm_decline_message),
            bt1Title = getString(R.string.back),
            bt1Callback = {
                showAnswerAcceptInvite(companyName)
                false
                          },
            bt2Title = getString(R.string.confirm),
            bt2Callback = {
                presenter.declineInviteToken(args.invitetokenargs)
                false
            },
            closeCallback = {
                baseLogout()
            },
            isCancelable = false
        )
    }
    override fun onDeclineInviteTokenSuccess(){
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_117_perfil_impedido,
            title = getString(R.string.access_manager_decline_invitate_success_title).fromHtml().toString(),
            message = getString(R.string.access_manager_decline_invitate_success_message),
            bt2Title = getString(R.string.finish),
            bt2Callback = {
                goToLogin()
                false
            },
            closeCallback = {
                baseLogout()
            },
            isCancelable = false
        )
    }

    override fun onAcceptInviteTokenSuccess() {
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_14_estrelas,
            title = getString(R.string.access_manager_accept_invitate_success_title).fromHtml().toString(),
            message = getString(R.string.access_manager_accept_invitate_success_message),
            bt2Title = getString(R.string.finish),
            bt2Callback = {
                goToLogin()
                false
            },
            closeCallback = {
                baseLogout()
            },
            isCancelable = false
        )
    }

    override fun onShowGenericError() {
        navigation?.showCustomBottomSheet(
            image = R.drawable.img_117_perfil_impedido,
            title = getString(R.string.access_manager_generic_error_token_title).fromHtml().toString(),
            message = getString(R.string.access_manager_generic_error_token_message),
            bt2Title = getString(R.string.entendi),
            bt2Callback = {
                goToLogin()
                false
            },
            closeCallback = {
                baseLogout()
            },
            isCancelable = false
        )
    }
}