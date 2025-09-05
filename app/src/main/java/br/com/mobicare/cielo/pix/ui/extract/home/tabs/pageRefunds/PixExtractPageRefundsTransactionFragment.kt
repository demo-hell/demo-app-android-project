package br.com.mobicare.cielo.pix.ui.extract.home.tabs.pageRefunds

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.databinding.FragmentPixExtractPageReturnsTransactionBinding
import br.com.mobicare.cielo.extensions.doWhenVisible
import br.com.mobicare.cielo.extensions.safeRun
import br.com.mobicare.cielo.pix.constants.DEFAULT_BALANCE
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.REFRESH_EXTRACT_PAGE
import br.com.mobicare.cielo.pix.domain.PixExtractReceipt
import br.com.mobicare.cielo.pix.domain.PixExtractResponse
import br.com.mobicare.cielo.pix.domain.ReceiptsTab
import br.com.mobicare.cielo.pix.ui.extract.adapter.PixExtractAdapter
import br.com.mobicare.cielo.pix.ui.extract.home.PixExtractFragmentDirections
import br.com.mobicare.cielo.pix.ui.extract.home.tabs.PixExtractTabsContract
import br.com.mobicare.cielo.pix.ui.extract.home.tabs.PixExtractTabsPresenter
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PixExtractPageRefundsTransactionFragment : BaseFragment(),
    PixExtractTabsContract.View {

    private val pixExtractAdapter: PixExtractAdapter by inject { parametersOf(this) }
    private val presenter: PixExtractTabsPresenter by inject { parametersOf(this) }
    private var _balance: String = DEFAULT_BALANCE
    override var balance: String
        get() = _balance
        set(value) {
            _balance = value
        }

    private var _binding: FragmentPixExtractPageReturnsTransactionBinding? = null
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentPixExtractPageReturnsTransactionBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onResume() {
        super.onResume()
        safeRun {
            presenter.run {
                if (alreadyLoaded.not())
                    getExtract(true, ReceiptsTab.REVERSAL)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        safeRun {
            presenter.onDestroyView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        safeRun {
            presenter.onDestroy()
        }
    }

    private fun setupView() {
        safeRun {
            binding?.recyclerRefundsExtract?.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = pixExtractAdapter
            }
        }
    }

    override fun showLoading() {
        safeRun {
            binding?.recyclerRefundsExtract?.gone()
            binding?.pbLoading?.visible()
            binding?.includeRefundsInformation?.root?.gone()
        }
    }

    override fun hideLoading() {
        safeRun {
            binding?.recyclerRefundsExtract?.visible()
            binding?.pbLoading?.gone()
        }
    }

    override fun showError(error: ErrorMessage?, isFirstPage: Boolean) {
        doWhenVisible {
            safeRun {
                bottomSheetGenericFlui(
                    EMPTY,
                    R.drawable.ic_07,
                    getString(R.string.we_found_a_problem_title),
                    getString(R.string.we_found_a_problem_subtitle),
                    EMPTY,
                    nameBtn2Bottom = getString(R.string.txt_btn_error),
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
                    this.onClick =
                        object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {

                            override fun onBtnSecond(dialog: Dialog) {
                                dialog.dismiss()
                                presenter.getExtract(isFirstPage, ReceiptsTab.REVERSAL)
                            }

                            override fun onSwipeClosed() {
                                super.onSwipeClosed()
                                dismiss()
                            }

                            override fun onCancel() {
                                super.onCancel()
                                dismiss()
                            }
                        }
                }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
            }
        }
    }

    override fun showExtract(
        extract: PixExtractResponse?,
        isFirstPage: Boolean,
        isFilter: Boolean
    ) {
        safeRun {
            extract?.let {
                pixExtractAdapter.populateList(extract)
            }
        }
    }

    override fun showDetails(pixExtractReceipt: PixExtractReceipt?) {
        safeRun {
            if (findNavController().currentDestination?.id == R.id.pixExtractFragment)
                findNavController().navigate(
                    PixExtractFragmentDirections.actionPixExtractFragmentToPixExtractDetailFragment(
                        true,
                        pixExtractReceipt,
                        balance
                    )
                )
        }
    }

    override fun showFooter() {
        safeRun {
            binding?.includeRefundsInformation?.root?.visible()
        }
    }

}