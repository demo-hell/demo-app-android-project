package br.com.mobicare.cielo.solesp.ui.start

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.databinding.FragmentSolespStartBinding
import br.com.mobicare.cielo.pix.constants.EMPTY
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class SolespStartFragment : BaseFragment(), CieloNavigationListener, SolespStartContract.View {

    private val presenter: SolespStartPresenter by inject {
        parametersOf(this)
    }

    private var binding: FragmentSolespStartBinding? = null
    private var navigation: CieloNavigation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSolespStartBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.getSolespEnabled()
    }

    override fun onResume() {
        super.onResume()
        setupNavigation()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun showSolespDisabled() {
        bottomSheetGenericFlui(
            nameTopBar = EMPTY,
            R.drawable.ic_54,
            getString(R.string.text_unavailability_message_title),
            getString(R.string.text_unavailability_message),
            nameBtn1Bottom = EMPTY,
            nameBtn2Bottom = getString(R.string.entendi),
            statusBtnClose = false,
            statusBtnFirst = false,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true
        ).apply {
            this.onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    dismiss()
                    requireActivity().finish()
                }

                override fun onSwipeClosed() {
                    dismiss()
                    requireActivity().finish()
                }

                override fun onCancel() {
                    dismiss()
                    requireActivity().finish()
                }
            }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun onButtonClicked(labelButton: String) {
        findNavController().navigate(
            SolespStartFragmentDirections.actionSolespStartFragmentToSolespSelectTypeInfoFragment()
        )
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextButton(getString(R.string.txt_button_solesp_start))
            navigation?.enableButton(true)
            navigation?.showHelpButton(false)
            navigation?.setNavigationListener(this)
        }
    }

}