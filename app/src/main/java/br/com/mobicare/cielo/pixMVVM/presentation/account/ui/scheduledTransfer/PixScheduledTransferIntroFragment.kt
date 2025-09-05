package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.scheduledTransfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.FragmentPixScheduledTransferIntroBinding
import br.com.mobicare.cielo.pixMVVM.presentation.account.ui.PixAccountBaseFragment

class PixScheduledTransferIntroFragment : PixAccountBaseFragment() {

    override val toolbarTitle get() = getString(R.string.pix_account_scheduled_transfer_title)

    override val footerButtonConfigurator get() = FooterButtonConfigurator(
        text = getString(R.string.text_configurar),
        onTap = ::onConfigureTap
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return FragmentPixScheduledTransferIntroBinding
            .inflate(inflater, container, false)
            .root
    }

    private fun onConfigureTap() {
        findNavController().navigate(
            PixScheduledTransferIntroFragmentDirections
                .actionPixScheduledTransferIntroFragmentToPixScheduledTransferConfigureFragment()
        )
    }

}