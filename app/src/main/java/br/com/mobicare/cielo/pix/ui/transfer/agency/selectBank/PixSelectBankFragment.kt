package br.com.mobicare.cielo.pix.ui.transfer.agency.selectBank

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.getTitlePix
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import br.com.mobicare.cielo.commons.utils.showSoftKeyboard
import br.com.mobicare.cielo.databinding.FragmentPixSelectBankBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pix.constants.DEFAULT_BALANCE
import br.com.mobicare.cielo.pix.constants.PIX_BALANCE_ARGS
import br.com.mobicare.cielo.pix.constants.PIX_IS_TRUSTED_DESTINATION_ARGS
import br.com.mobicare.cielo.pix.model.PixBank
import br.com.mobicare.cielo.pix.ui.transfer.agency.selectBank.adapter.SelectBankPixAdapter
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PixSelectBankFragment : BaseFragment(), CieloNavigationListener, PixSelectBankContract.View {

    private var binding: FragmentPixSelectBankBinding? = null
    private var adapter: SelectBankPixAdapter? = null
    private var navigation: CieloNavigation? = null

    private val presenter: PixSelectBankPresenter by inject {
        parametersOf(this)
    }

    private val balance: String? by lazy {
        arguments?.getString(PIX_BALANCE_ARGS)
    }

    private val isTrustedDestination: Boolean by lazy {
        arguments?.getBoolean(PIX_IS_TRUSTED_DESTINATION_ARGS, false) ?: false
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View =
            FragmentPixSelectBankBinding.inflate(
                    inflater, container, false
            ).also {
                binding = it
            }.root

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        presenter.getAllBanks()

        binding?.etBankSearch?.isEnabled = false

        setupNavigation()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setTextToolbar(getString(getTitlePix(isTrustedDestination)))
            navigation?.showHelpButton(false)
            navigation?.showButton(false)
            navigation?.setNavigationListener(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearch()
    }

    override fun setupBankListView(banks: List<PixBank>) {
        adapter = SelectBankPixAdapter(banks, this, requireContext())

        binding?.apply {
            rvBanks.layoutManager = LinearLayoutManager(context)
            rvBanks.adapter = adapter
            etBankSearch.isEnabled = true
        }
    }

    private fun setupSearch() {
        binding?.apply {
            etBankSearch.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_UNSPECIFIED)
                    requireActivity().hideSoftKeyboard()
                false
            }

            etBankSearch.doAfterTextChanged { itEditable ->
                val bankCodeOrName = itEditable.toString()
                presenter.searchBank(bankCodeOrName)

                setupCleanIcon(bankCodeOrName.isNotBlank())
            }

            ivClearSearch.setOnClickListener {
                etBankSearch.text?.clear()

                adapter?.updateList(presenter.fetchAllBanks())
                requireActivity().showSoftKeyboard(etBankSearch)
            }
        }
    }

    override fun onBackButtonClicked(): Boolean {
        requireActivity().hideSoftKeyboard()
        return super.onBackButtonClicked()
    }

    override fun onSelectedBank(bank: PixBank) {
        findNavController().navigate(
            PixSelectBankFragmentDirections.actionPixSelectBankFragmentToPixSelectAccountTypeFragment(
                bank,
                balance ?: DEFAULT_BALANCE,
                isTrustedDestination
            )
        )
    }

    override fun showLoading() {
        binding?.apply {
            pbProgress.visible()
            rvBanks.gone()
        }
    }

    override fun hideLoading() {
        binding?.apply {
            pbProgress.gone()
            rvBanks.visible()
        }
    }

    override fun showFilteredBanks(filteredBanks: MutableList<PixBank>) {
        adapter?.updateList(filteredBanks.toList())

        if (filteredBanks.isEmpty()) setupBanksNotFound()
        else setupBanksFound()
    }

    private fun setupBanksFound() {
        binding?.apply {
            rvBanks.visible()
            containerWithoutBanks.gone()
        }
    }

    private fun setupBanksNotFound() {
        binding?.apply {
            rvBanks.gone()
            containerWithoutBanks.visible()
        }
    }

    private fun setupCleanIcon(shouldShow: Boolean) {
        binding?.ivClearSearch?.visible(shouldShow)
    }

    override fun showError(error: ErrorMessage?) {
        bottomSheetGenericFlui(
            "",
            R.drawable.ic_08,
            getString(R.string.text_list_bank_error_title),
            getString(R.string.text_list_bank_error_subtitle),
            "",
            getString(R.string.text_error_update),
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = false,
            statusBtnFirst = false,
            statusBtnSecond = true,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = false
        ).apply {
            onClick = object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                override fun onBtnSecond(dialog: Dialog) {
                    presenter.getAllBanks()
                    dismiss()
                }

                override fun onSwipeClosed() {
                    dismiss()
                    requireActivity().onBackPressed()
                }

                override fun onCancel() {
                    dismiss()
                    requireActivity().onBackPressed()
                }
            }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}