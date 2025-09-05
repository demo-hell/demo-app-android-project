package br.com.mobicare.cielo.accessManager.invite.receive.ui.legacy.invite.accept

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.accessManager.invite.receive.ui.newUser.create.base.InviteReceiveBaseFragment
import br.com.mobicare.cielo.databinding.FragmentInviteLegacyUserAcceptedBinding

class InviteLegacyUserAcceptedFragment : InviteReceiveBaseFragment() {

    private var binding: FragmentInviteLegacyUserAcceptedBinding? = null
    val args: InviteLegacyUserAcceptedFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentInviteLegacyUserAcceptedBinding.inflate(
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
            btnClose.setOnClickListener {
                startDigitalID()
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}