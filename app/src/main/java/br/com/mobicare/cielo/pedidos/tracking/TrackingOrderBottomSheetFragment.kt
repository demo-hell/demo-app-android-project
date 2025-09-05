package br.com.mobicare.cielo.pedidos.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pedidos.tracking.model.Tracking
import br.com.mobicare.cielo.pedidos.tracking.model.TrakingOrderAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_tracking_order_bottom_sheet.*
import kotlinx.android.synthetic.main.layout_status_order_error.*

class TrackingOrderBottomSheetFragment : BottomSheetDialogFragment() {

    private var bottomSheet: View? = null

    companion object {
        const val TRACKING_EXTRA = "TRACKING_EXTRA"

        fun getInstance(fragmentManager: FragmentManager,
                        tracking: Tracking) = TrackingOrderBottomSheetFragment()
                .apply {
                    val extras = Bundle()
                    extras.putParcelable(TRACKING_EXTRA, tracking)
                    arguments = extras
                    this.show(fragmentManager, this::class.java.simpleName)
                }
    }

    @Nullable
    override fun onCreateView(
            inflater: LayoutInflater,
            @Nullable container: ViewGroup?,
            @Nullable savedInstanceState: Bundle?
    ): View? {
        bottomSheet = inflater
                .inflate(R.layout.fragment_tracking_order_bottom_sheet, container, false)
        return bottomSheet
    }

    override fun onResume() {
        super.onResume()
        configureBottomSheet()
        init()
    }

    private fun configureBottomSheet() {
        val bottomSheetBehavior = BottomSheetBehavior
                .from(view?.parent as View)
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun init() {
        val tracking = arguments?.getParcelable<Tracking>(TRACKING_EXTRA)

        val adapter = TrakingOrderAdapter(requireContext(), tracking)
        recyclerViewTrackingOrder.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false)
        recyclerViewTrackingOrder.adapter = adapter
        nestedScrollView.post { nestedScrollView.fullScroll(View.FOCUS_DOWN) }

        showOrNotError(tracking)
    }

    private fun showOrNotError(tracking: Tracking?) {
        if (!tracking?.description.isNullOrEmpty()) {
            textViewStatusOrderError.text = tracking?.description
            linearLayoutOrderStatusError.visible()
        }
    }
}