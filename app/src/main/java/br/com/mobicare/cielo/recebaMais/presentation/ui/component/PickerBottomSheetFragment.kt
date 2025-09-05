package br.com.mobicare.cielo.recebaMais.presentation.ui.component

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE_NEGATIVE
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_picker_bottom_sheet.*

@SuppressLint("ValidFragment")
class PickerBottomSheetFragment : BottomSheetDialogFragment(),
        OnPickerSelectionCallback {

    private var pickerTitle: String = EMPTY
    private var items: List<String>? = null
    private var lockCollapse: Boolean = false
    private var selectedPeriod: String? = null

    var snapPosition: Int? = null
    lateinit var adapter: CalendarAdapterCustom

    var onItemSelectedListener: OnItemSelectedListener? = null

    private val selectedPosition get() = items?.indexOf(selectedPeriod ?: EMPTY) ?: ONE_NEGATIVE

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            if (lockCollapse) {
                behavior.isHideable = false
            } else {
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
            }

            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (lockCollapse) {
                        if(newState > BottomSheetBehavior.STATE_DRAGGING){
                            Handler().postDelayed({
                                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                            }, 100)
                        }
                    } else if (newState >= 4) {
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }

        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_picker_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        positionSnap = 0

        managerButton()
        initRecyclerView()

        textPickerBottomSheetTitle?.text = SpannableStringBuilder.valueOf(pickerTitle)
    }

    /**
     * método para gerenciar o click dos butões
     * */
    private fun managerButton() {
        btn_calendar_close?.setOnClickListener {
            dismiss()
        }

        btn_calendar_confirmar?.setOnClickListener {
            snapPosition?.let {
                onItemSelectedListener?.onSelected(it)
            }
            dismiss()
        }
    }

    /**
     * método que inicia o recyclerview e seta a lista no adapter
     * */
    private fun initRecyclerView() {
        rv_calendar_custom?.layoutManager = LinearLayoutManager(requireContext())
        adapter = CalendarAdapterCustom(items, requireContext())
        rv_calendar_custom?.adapter = adapter

        //animacao na lista
        val startSnapHelper = StartSnapHelper()
        startSnapHelper.attachToRecyclerView(rv_calendar_custom)

        rv_calendar_custom?.apply {
            attachSnapHelperWithListener(startSnapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
                null, this@PickerBottomSheetFragment)
            selectedPosition.let { position ->
                if (position > ONE_NEGATIVE) smoothScrollToPosition(position)
            }
        }
    }

    /**
     * Método que recebe a resposta do callback do componente de calendário
     * */
    override fun onResponseCalendar(snapPosition: Int) {
        this.snapPosition = snapPosition
        adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        positionSnap = null
    }

    interface OnItemSelectedListener {
        fun onSelected(selectedItem: Int)
    }

    companion object {
        var positionSnap: Int? = null

        fun newInstance(
            pickerTitle: String,
            dateList: List<String>?,
            lockCollapse: Boolean = false,
            selectedPeriod: String? = null
        ) = PickerBottomSheetFragment().apply {
            this.pickerTitle = pickerTitle
            this.items = dateList
            this.lockCollapse = lockCollapse
            this.selectedPeriod = selectedPeriod
        }
    }

}