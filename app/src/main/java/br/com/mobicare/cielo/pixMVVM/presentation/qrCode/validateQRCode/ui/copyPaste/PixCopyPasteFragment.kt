package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.ui.copyPaste

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.inputtext.CieloInputTextMultiline
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_THOUSAND
import br.com.mobicare.cielo.commons.constants.SEVEN
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.Utils.clipboardHasPlainText
import br.com.mobicare.cielo.commons.utils.Utils.pastePlainTextFromClipboard
import br.com.mobicare.cielo.databinding.FragmentPixCopyPasteBinding
import br.com.mobicare.cielo.databinding.LayoutPixFooterRoundedTwoButtonsBinding
import br.com.mobicare.cielo.extensions.safeNavigate
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.enums.PixQRCodeScreenEnum
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.viewModel.PixValidateQRCodeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PixCopyPasteFragment : BaseFragment() {
    private var binding: FragmentPixCopyPasteBinding? = null
    private var bindingFooter: LayoutPixFooterRoundedTwoButtonsBinding? = null

    private val viewModel: PixValidateQRCodeViewModel by sharedViewModel()

    private var navigation: CieloNavigation? = null

    private val toolbarConfigurator get() =
        CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
            toolbar =
                CieloCollapsingToolbarLayout.Toolbar(
                    title = getString(R.string.pix_qr_code_copy_paste_toolbar_title),
                    menu =
                        CieloCollapsingToolbarLayout.ToolbarMenu(
                            menuRes = R.menu.menu_pix_read_qr_code,
                            onOptionsItemSelected = ::onClickOptionsItemMenuToolbar,
                        ),
                ),
            footerView = bindingFooter?.root,
        )

    private val onTextChangeListenerCopyPaste get() =
        object : CieloInputTextMultiline.TextChangeListener {
            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int,
            ) {
                enableNextButton()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = FragmentPixCopyPasteBinding
        .inflate(inflater, container, false)
        .also {
            bindingFooter = LayoutPixFooterRoundedTwoButtonsBinding.inflate(inflater, container, false)
            binding = it
        }.root

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupInput()
        setupListeners()
        setupObserver()
    }

    override fun onResume() {
        super.onResume()
        setupNavigation()
        enableNextButton()
        showPasteCodeButton()
        viewModel.setScreenOriginDecode(PixQRCodeScreenEnum.COPY_PASTE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        bindingFooter = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.also {
                it.showContent(true)
                it.configureCollapsingToolbar(toolbarConfigurator)
            }
        }
    }

    private fun setupObserver() {
        viewModel.qrCode.observe(viewLifecycleOwner) { qrCode ->
            if (qrCode.isNotEmpty()) navigateToPixValidateQRCodeFragment()
        }
    }

    private fun setupListeners() {
        binding?.inputTextPixCopyPaste?.setOnTextChangeListener(onTextChangeListenerCopyPaste)

        bindingFooter?.apply {
            buttonPrimary.setOnClickListener { onClickNextButton() }
            buttonSecondary.setOnClickListener { pasteCode() }
        }
    }

    private fun setupInput() {
        binding?.inputTextPixCopyPaste?.apply {
            inputLayout.apply {
                isHintEnabled = false
                isCounterEnabled = false
                setLines(SEVEN)
            }

            inputEditText.hint = getString(R.string.pix_qr_code_copy_paste_toolbar_label_hint_copy_paste_input)

            setMaxLength(ONE_THOUSAND)
        }
    }

    private fun showPasteCodeButton() {
        binding?.inputTextPixCopyPaste?.inputEditText?.post {
            bindingFooter?.buttonSecondary?.apply {
                setButtonBackgroundResource(R.drawable.selector_button_transparent)
                visible(clipboardHasPlainText(requireActivity()))
            }
        }
    }

    private fun pasteCode() {
        binding?.inputTextPixCopyPaste?.inputEditText?.setText(pastePlainTextFromClipboard(requireActivity()))
        clearFocusInput()
        enableNextButton()
    }

    private fun enableNextButton() {
        val enable = binding?.inputTextPixCopyPaste?.getText()?.isNotBlank() == true

        if (bindingFooter?.buttonPrimary?.isButtonEnabled != enable) {
            bindingFooter?.buttonPrimary?.isButtonEnabled = enable
        }
    }

    private fun clearFocusInput() {
        binding?.inputTextPixCopyPaste?.inputLayout?.clearFocus()
    }

    private fun onClickOptionsItemMenuToolbar(item: MenuItem) {
        when (item.itemId) {
            R.id.icReadQRCode -> navigateToPixDecodeQRCodeFragment()
        }
    }

    private fun onClickNextButton() {
        clearFocusInput()
        viewModel.setQRCode(binding?.inputTextPixCopyPaste?.getText().orEmpty())
    }

    private fun navigateToPixDecodeQRCodeFragment() {
        findNavController().safeNavigate(
            PixCopyPasteFragmentDirections.actionPixCopyPasteFragmentToPixDecodeQRCodeFragment(),
        )
    }

    private fun navigateToPixValidateQRCodeFragment() {
        findNavController().safeNavigate(
            PixCopyPasteFragmentDirections.actionPixCopyPasteFragmentToPixValidateQRCodeFragment(),
        )
    }
}
