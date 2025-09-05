package br.com.mobicare.cielo.pixMVVM.presentation.infringement.ui.detailWhatHappened

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.collapsingToolbarLayout.CieloCollapsingToolbarLayout
import br.com.cielo.libflue.inputtext.CieloInputTextMultiline
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.TEN
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.databinding.FragmentPixInfringementDetailWhatHappenedBinding
import br.com.mobicare.cielo.databinding.LayoutPixFooterButtonBinding
import br.com.mobicare.cielo.extensions.safeNavigate

class PixInfringementDetailWhatHappenedFragment : BaseFragment(), CieloNavigationListener {

    private var binding: FragmentPixInfringementDetailWhatHappenedBinding? = null
    private var footerBinding: LayoutPixFooterButtonBinding? = null
    private var navigation: CieloNavigation? = null

    private val args: PixInfringementDetailWhatHappenedFragmentArgs by navArgs()
    private val pixCreateNotifyInfringement by lazy {
        args.pixcreateinfringement
    }

    private val collapsingToolbar
        get() = CieloCollapsingToolbarLayout.Configurator(
            layoutMode = CieloCollapsingToolbarLayout.LayoutMode.SCROLLABLE,
            toolbar = CieloCollapsingToolbarLayout.Toolbar(
                title = getString(R.string.pix_infringement_detail_what_happened_title_toolbar)
            ),
            footerView = footerBinding?.root
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        LayoutPixFooterButtonBinding.inflate(
            inflater, container, false
        ).also { footerBinding = it }

        return FragmentPixInfringementDetailWhatHappenedBinding.inflate(
            inflater, container, false
        ).also { binding = it }.root
    }

    override fun onResume() {
        super.onResume()

        setupNavigation()
        setupView()
        setupListeners()
        enableBtnContinue()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding = null
        footerBinding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = (requireActivity() as CieloNavigation).also {
                it.configureCollapsingToolbar(collapsingToolbar)
            }
        }
    }

    private fun setupView() {
        footerBinding?.button?.text = getString(R.string.continuar)
    }

    private fun setupListeners() {
        footerBinding?.button?.setOnClickListener(::onClickContinue)
        binding?.apply {
            root.setOnClickListener {
                requireActivity().hideSoftKeyboard()
                itDetailWhatHappened.clearFocus()
            }

            itDetailWhatHappened.setOnTextChangeListener(object :
                CieloInputTextMultiline.TextChangeListener {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    enableBtnContinue()
                }
            })
        }
    }

    private fun enableBtnContinue() {
        footerBinding?.button?.isEnabled =
            binding?.itDetailWhatHappened?.getText().orEmpty().length >= TEN
    }

    private fun onClickContinue(view: View) {
        val newPixCreateNotifyInfringement = pixCreateNotifyInfringement?.copy(
            message = binding?.itDetailWhatHappened?.getText().orEmpty()
        )

        requireActivity().hideSoftKeyboard()
        binding?.itDetailWhatHappened?.clearFocus()

        findNavController().safeNavigate(
            PixInfringementDetailWhatHappenedFragmentDirections
                .actionPixInfringementDetailWhatHappenedFragmentToPixInfringementSendRequestFragment(
                    newPixCreateNotifyInfringement
                )
        )
    }

}