package br.com.mobicare.cielo.accessManager.invite.receive.ui.legacy.invite.accept

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base.InviteReceivePresenter
import br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base.InviteReceiveBaseFragment
import br.com.mobicare.cielo.accessManager.model.PendingInviteItem
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.messageError
import br.com.mobicare.cielo.databinding.FragmentInviteLegacyUserAcceptBinding
import br.com.mobicare.cielo.login.domains.entities.UserObj
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class InviteLegacyUserAcceptFragment : InviteReceiveBaseFragment() {

    private val presenter: InviteReceivePresenter by inject {
        parametersOf(this)
    }

    private var binding: FragmentInviteLegacyUserAcceptBinding? = null

    private var invitePendingInviteItem: PendingInviteItem? = null
    private var onboardingRequired: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            it.getParcelable<PendingInviteItem>(INVITE_ARGS)?.apply {
                invitePendingInviteItem = this
            }

            it.getBoolean(ONBOARDING_REQUIRED_ARGS).apply {
                onboardingRequired = this
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentInviteLegacyUserAcceptBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root
    }

    override fun onResume() {
        super.onResume()
        if (invitePendingInviteItem == null)
            presenter.getPendingInvites()
        else
            invitePendingInviteItem?.let { showPendingInvite(it) }
    }

    override fun showPendingInvite(invite: PendingInviteItem) {
        invitePendingInviteItem = invite
        setupMessage(invite)
        setupButtons(invite)
    }

    private fun setupButtons(invitePendingInviteItem: PendingInviteItem) {
        binding?.apply {
            btnAcceptLater.setOnClickListener {
                showAcceptLaterBS { presenter.acceptInvite(invitePendingInviteItem.id) }
            }

            btnAcceptNow.setOnClickListener {
                presenter.acceptInvite(invitePendingInviteItem.id)
            }
        }
    }

    private fun showAcceptLaterBS(callback: () -> Unit) {
        navigation?.showCustomBottomSheet(
            title = getString(R.string.access_manager_answer_invitation_bs_accept_later_title),
            message = getString(R.string.access_manager_answer_invitation_bs_accept_later_legacy_user_message),
            bt1Title = getString(R.string.access_manager_answer_invitation_bs_accept_later_btn_accept_later),
            bt2Title = getString(R.string.access_manager_answer_invitation_bs_accept_later_btn_accept),
            bt1Callback = {
                startDigitalID()
                false
            },
            bt2Callback = {
                callback.invoke()
                false
            }
        )
    }

    override fun onInviteAcceptSuccess() {
        navigateToAcceptedScreen()
    }

    private fun navigateToAcceptedScreen() {
        invitePendingInviteItem?.let {
            findNavController().navigate(
                InviteLegacyUserAcceptFragmentDirections.actionInviteLegacyUserAcceptFragmentToInviteLegacyUserAcceptedFragment(
                    it,
                    onboardingRequired
                )
            )
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun setupMessage(pendingInviteItem: PendingInviteItem) {
        val roleName = when (pendingInviteItem.role) {
            UserObj.ADMIN -> getString(R.string.access_manager_admins)
            UserObj.ANALYST -> getString(R.string.access_manager_analyst)
            UserObj.TECHNICAL -> getString(R.string.access_manager_technical)
            else -> R.string.access_manager_readers
        }

        binding?.tvMessage?.text = getString(
            R.string.access_manager_answer_invitation_message,
            pendingInviteItem.companyName,
            roleName
        )
    }

    override fun showError(error: ErrorMessage?) {
        navigation?.showCustomBottomSheet(
            message = messageError(error, requireActivity()),
            bt2Callback = {
                startDigitalID()
                false
            }
        )
    }

    companion object {
        const val INVITE_ARGS = "INVITE"
        const val ONBOARDING_REQUIRED_ARGS = "ONBOARDING_REQUIRED"
    }
}