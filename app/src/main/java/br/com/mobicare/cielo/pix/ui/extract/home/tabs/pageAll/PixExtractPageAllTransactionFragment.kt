package br.com.mobicare.cielo.pix.ui.extract.home.tabs.pageAll

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.databinding.FragmentPixExtractPageAllTransactionBinding
import br.com.mobicare.cielo.extensions.doWhenVisible
import br.com.mobicare.cielo.extensions.safeRun
import br.com.mobicare.cielo.pix.constants.DEFAULT_BALANCE
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.domain.FilterExtract
import br.com.mobicare.cielo.pix.domain.PixExtractReceipt
import br.com.mobicare.cielo.pix.domain.PixExtractResponse
import br.com.mobicare.cielo.pix.domain.ReceiptsTab
import br.com.mobicare.cielo.pix.ui.extract.adapter.PixExtractAdapter
import br.com.mobicare.cielo.pix.ui.extract.filter.PixExtractFilterBottomSheetFragment
import br.com.mobicare.cielo.pix.ui.extract.home.PixExtractFragmentDirections
import br.com.mobicare.cielo.pix.ui.extract.home.tabs.PixExtractTabsContract
import br.com.mobicare.cielo.pix.ui.extract.home.tabs.PixExtractTabsPresenter
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PixExtractPageAllTransactionFragment : BaseFragment(),
    PixExtractTabsContract.View {

    private val pixExtractAdapter: PixExtractAdapter by inject { parametersOf(this) }
    private val presenter: PixExtractTabsPresenter by inject { parametersOf(this) }

    private var _binding: FragmentPixExtractPageAllTransactionBinding? = null
    private val binding get() = _binding
    private var filter: FilterExtract? = null

    companion object {
        private const val ARG_PARAM_EXTRACT_FILTER = "ARG_PARAM_EXTRACT_FILTER"
        fun create(context: Context, filter: FilterExtract) {
            context.startActivity(
                Intent(
                    context,
                    PixExtractPageAllTransactionFragment::class.java
                ).apply {
                    putExtra(ARG_PARAM_EXTRACT_FILTER, filter)
                })
        }
    }

    private var _balance: String = DEFAULT_BALANCE

    override var balance: String
        get() = _balance
        set(value) {
            _balance = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPixExtractPageAllTransactionBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()

        binding?.buttonFilter?.setOnClickListener {
            presenter.showMoreFilters()
        }
    }

    override fun onResume() {
        super.onResume()
        safeRun {
            presenter.run {
                if (alreadyLoaded.not())
                    getExtract(true, ReceiptsTab.TRANSFER, filter)
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
            binding?.recyclerExtractPageAll?.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = pixExtractAdapter
            }
        }
    }

    private fun setBackgroundFilter(qtdFilters: Int?) {
        safeRun {
            binding?.apply {
                when (qtdFilters) {
                    ZERO -> imgFilter.setBackgroundResource(R.drawable.ic_filter_blue)
                    ONE -> imgFilter.setBackgroundResource(R.drawable.ic_filter_number_one)
                    TWO -> imgFilter.setBackgroundResource(R.drawable.ic_filter_number_two)
                    THREE -> imgFilter.setBackgroundResource(R.drawable.ic_filter_number_three)
                }
            }
        }
    }

    private fun initializeRecyclerView(extract: PixExtractResponse?, isFilter: Boolean) {
        safeRun {
            extract?.items?.let { itTransactions ->
                if (itTransactions.isEmpty().not()) {
                    binding?.recyclerExtractPageAll?.apply {
                        var oldState = ZERO
                        addOnScrollListener(object : RecyclerView.OnScrollListener() {
                            override fun onScrollStateChanged(
                                recyclerView: RecyclerView,
                                newState: Int
                            ) {
                                binding?.recyclerExtractPageAll?.post {
                                    if (recyclerView.canScrollVertically(ONE)
                                            .not() && newState > oldState
                                    ) {
                                        oldState = newState
                                        showLoadingMore()
                                        presenter.getExtract(
                                            false,
                                            ReceiptsTab.TRANSFER,
                                            filter,
                                            isFilter
                                        )
                                    }
                                }
                            }
                        })
                    }
                }
            }
        }
    }

    override fun hideLoadingMore() {
        safeRun {
            binding?.pbLoadingMore?.gone()
        }
    }

    override fun showLoadingMore() {
        safeRun {
            binding?.pbLoadingMore?.visible()
            binding?.includeInformation?.root?.gone()
        }
    }

    override fun showLoading() {
        safeRun {
            binding?.buttonFilter?.gone()
            binding?.pbLoading?.visible()
            binding?.recyclerExtractPageAll?.gone()
            binding?.txtFilterAllTransactions?.gone()
            binding?.includeInformation?.root?.gone()
            binding?.includeNoDataFilter?.root.gone()
        }
    }

    override fun hideLoading() {
        safeRun {
            binding?.pbLoading?.gone()
            binding?.txtFilterAllTransactions?.visible()
            binding?.buttonFilter?.visible()
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
                                presenter.getExtract(isFirstPage, ReceiptsTab.TRANSFER, filter)
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
                }.show(
                    requireActivity().supportFragmentManager,
                    getString(R.string.bottom_sheet_generic)
                )
            }
        }
    }

    override fun showMoreFilters(filter: FilterExtract?) {
        safeRun {
            PixExtractFilterBottomSheetFragment.create(
                filter,
                getString(R.string.text_pix_extract_filter_title),
                object : PixExtractFilterBottomSheetFragment.OnResultListener {
                    override fun onResult(myFilter: FilterExtract?) {
                        binding?.txtFilterAllTransactions?.gone()
                        binding?.buttonFilter?.gone()
                        setBackgroundFilter(myFilter?.qtdFilters)
                        this@PixExtractPageAllTransactionFragment.filter = myFilter

                        presenter.getExtract(true, ReceiptsTab.TRANSFER, myFilter, true)
                    }
                }
            ).show(
                requireActivity().supportFragmentManager,
                PixExtractPageAllTransactionFragment::class.java.simpleName
            )
        }
    }

    override fun showExtract(
        extract: PixExtractResponse?,
        isFirstPage: Boolean,
        isFilter: Boolean
    ) {
        safeRun {
            binding?.recyclerExtractPageAll?.visible()
            initializeRecyclerView(extract, isFilter)
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
                        false,
                        pixExtractReceipt,
                        balance
                    )
                )
        }
    }

    override fun showFooter() {
        safeRun {
            binding?.includeInformation?.root?.visible()
        }
    }

    override fun showNoDataWithFilter() {
        safeRun {
            binding?.apply {
                includeNoDataFilter.root.visible()

                includeNoDataFilter.cleanFilter.setOnClickListener {
                    imgFilter.setBackgroundResource(R.drawable.ic_filter_blue)

                    if (arguments?.getString(ARG_PARAM_EXTRACT_FILTER) != null)
                        arguments?.remove(ARG_PARAM_EXTRACT_FILTER)

                    filter = null
                    presenter.getExtract(true, ReceiptsTab.TRANSFER, filter, true)
                }

                includeNoDataFilter.changeFilter.setOnClickListener {
                    showMoreFilters(filter)
                }
            }
        }
    }
}