package br.com.mobicare.cielo.tapOnPhone.presentation.terminal.ready

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.FragmentTapOnPhoneTerminalReadyBinding
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class TapOnPhoneTerminalReadyFragment : BaseFragment(), CieloNavigationListener {

    private val presenter: TapOnPhoneTerminalReadyPresenter by inject {
        parametersOf(this)
    }

    private var navigation: CieloNavigation? = null
    private var binding: FragmentTapOnPhoneTerminalReadyBinding? = null

    private val args: TapOnPhoneTerminalReadyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentTapOnPhoneTerminalReadyBinding.inflate(inflater, container, false)
            .also {
                binding = it
            }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupNavigation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextButton(getString(R.string.tap_on_phone_configured_environment))
            navigation?.showHelpButton()
            navigation?.showButton(isShow = true)
            navigation?.showBackIcon(isShow = false)
            navigation?.showCloseButton(isShow = true)
            navigation?.showContainerButton(isShow = true)
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupView() {
        binding?.startupStepsTapOnPhone?.apply {
            tvTitleTapOnPhone.text = getString(R.string.tap_on_phone_configured_environment_title)
            tvSubtitleTapOnPhone.text = HtmlCompat.fromHtml(
                getString(R.string.tap_on_phone_configured_environment_subtitle),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            ivTapOnPhone.setImageResource(R.drawable.ic_08)
        }
    }

    override fun onButtonClicked(labelButton: String) {
        presenter.onSaveVisualizationHistoric()
        findNavController().navigate(
            TapOnPhoneTerminalReadyFragmentDirections
                .actionTapOnPhoneTerminalReadyFragmentToTapOnPhoneSaleValueFragment(args.devicetapargs)
        )
    }

    override fun onCloseButtonClicked() {
        navigation?.goToHome()
    }
}