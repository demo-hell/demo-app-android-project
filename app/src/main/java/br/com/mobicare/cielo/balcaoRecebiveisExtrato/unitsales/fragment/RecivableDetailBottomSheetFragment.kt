package br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.fragment

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4Constants.SCREEN_VIEW_RECEIVABLES_NEGOTIATION_CIELO_SEE_MORE_OPERATION_DETAILS
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4Constants.SCREEN_VIEW_RECEIVABLES_NEGOTIATION_MARKET_SEE_MORE_OPERATION_DETAILS
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Item
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import br.com.mobicare.cielo.commons.utils.addMaskCPForCNPJ
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.toPtBrRealStringWithoutSymbol
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_recivable_detail_bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_recivable_detail_bottom_sheet_line_item.view.*
import org.koin.android.ext.android.inject

class RecivableDetailBottomSheetFragment : BottomSheetDialogFragment() {

    private var bottomSheet: View? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var negotiationItem: Item? = null
    private val analytics: ReceivablesAnalyticsGA4 by inject()

    companion object {
        private const val NEGOTIATION_ITEM = "NEGOTIATION_ITEM"

        @JvmStatic
        fun newInstance(fragmentManager: FragmentManager,
                        negotiationItem: Item) = RecivableDetailBottomSheetFragment()
                .apply {
                    arguments = Bundle().apply {
                        putParcelable(NEGOTIATION_ITEM, negotiationItem)
                    }
                    this.show(fragmentManager, this::class.java.simpleName)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            negotiationItem = it.getParcelable(NEGOTIATION_ITEM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recivable_detail_bottom_sheet, container, false)
    }

    override fun onResume() {
        super.onResume()
        trackDisplayContent()
        configureBottomSheet()
    }

    private fun trackDisplayContent() {
        val screenName = if (negotiationItem?.operationSourceCode == ONE) {
            SCREEN_VIEW_RECEIVABLES_NEGOTIATION_CIELO_SEE_MORE_OPERATION_DETAILS
        } else {
            SCREEN_VIEW_RECEIVABLES_NEGOTIATION_MARKET_SEE_MORE_OPERATION_DETAILS
        }
        analytics.logDisplayContent(screenName)
    }

    private fun configureBottomSheet() {
        bottomSheet = contentConstraintLayout
        bottomSheetBehavior = BottomSheetBehavior
                .from(view?.parent as View)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val list = ArrayList<Pair<String, String>>()
        val titleList = arrayListOf(resources.getStringArray(R.array.receivable_content_item_title_array))
        val paymentScheduleDate = DataCustomNew()
        negotiationItem?.let {
            paymentScheduleDate.setDateFromAPI(it.paymentScheduleDate)

            list.add(Pair(titleList[0][0], it.operationNumber ?: ""))

            if(it.identificationNumber.isNullOrEmpty().not()){
                if(it.identificationNumber?.length!! > 11) {
                    list.add(Pair(titleList[0][1], it.identificationNumber.addMaskCPForCNPJ(resources.getString(R.string.mask_cnpj_step4))))
                }else {
                    list.add(Pair(titleList[0][1], it.identificationNumber.addMaskCPForCNPJ(resources.getString(R.string.mask_cpf_step4))))
                }
            }

            list.add(Pair(titleList[0][2], paymentScheduleDate.formatBRDate()))
            list.add(Pair(titleList[0][3], "${it.averageTermWorkingDays.toString()} dias"))
            list.add(Pair(titleList[0][4], it.grossAmount?.toPtBrRealString() ?: ""))
            list.add(Pair(titleList[0][5], it.netAmount?.toPtBrRealString() ?: ""))
            list.add(Pair(titleList[0][6],"${it.negotiationFee?.toPtBrRealStringWithoutSymbol()}%"))

            list.forEach { itList ->
                val contentLine = LayoutInflater.from(requireContext())
                    .inflate(R.layout.fragment_recivable_detail_bottom_sheet_line_item, null)

                contentLine.textViewLabel.text = itList.first
                contentLine.textViewValue.text = itList.second
                linearLayoutContent.addView(contentLine)
            }
        }
    }

    private fun configureFullBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior
                .from(view?.parent as View)

        val childLayoutParams = bottomSheet?.getLayoutParams();
        val displayMetrics = DisplayMetrics();

        requireActivity()
                .getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        childLayoutParams?.height = displayMetrics.heightPixels;
        bottomSheet?.setLayoutParams(childLayoutParams)
    }
}
