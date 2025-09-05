package br.com.mobicare.cielo.accessManager.invite.receive.ui.legacy.invite.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.parseAsHtml
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base.InviteReceiveBaseFragment
import br.com.mobicare.cielo.databinding.FragmentInviteLegacyUserWelcomeBinding

class InviteLegacyUserWelcomeFragment : InviteReceiveBaseFragment() {

    private var binding: FragmentInviteLegacyUserWelcomeBinding? = null
    val args: InviteLegacyUserWelcomeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentInviteLegacyUserWelcomeBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
    }

    private fun setupButtons() {
        binding?.apply {
            tvMessage.text = getString(R.string.access_manager_invite_legacy_user_message).parseAsHtml()
            btnAccessAccount.setOnClickListener {
                goToLogin()
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}