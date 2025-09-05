package br.com.mobicare.cielo.login.firstAccess.presentation.ui.selectOption

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.databinding.FragmentFirstAccessSelectOptionBottomSheetBinding
import br.com.mobicare.cielo.login.analytics.LoginAnalytics
import br.com.mobicare.cielo.login.firstAccess.presentation.FirstAccessNavigationFlowActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.jetbrains.anko.startActivity

class FirstAccessSelectOptionBottomSheetFragment : BottomSheetDialogFragment() {

    private var binding: FragmentFirstAccessSelectOptionBottomSheetBinding? = null
    private val loginAnalytics by lazy { LoginAnalytics() }

    companion object {
        fun onCreate() = FirstAccessSelectOptionBottomSheetFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFirstAccessSelectOptionBottomSheetBinding.inflate(
        inflater,
        container,
        false
    ).also{
        setupBottomSheet()
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun setupBottomSheet() {
        dialog?.setOnShowListener {
            val bottomSheet = dialog?.findViewById<View>(R.id.design_bottom_sheet) as? FrameLayout

            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)

                behavior.disableShapeAnimations()
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = ZERO

                dialog?.setCancelable(false)
                dialog?.setCanceledOnTouchOutside(false)

                behavior.isHideable = false
                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_DRAGGING)
                            behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
        }
    }

    private fun setupListeners(){
        binding?.apply {
            btnBackLogin.setOnClickListener{
                loginAnalytics.logFirstAccessButtonDoLoginGa(true)
                dismiss()
            }
            btnCreateAccess.setOnClickListener{
                loginAnalytics.logFirstAccessButtonDoLoginGa(false)
                activity?.startActivity <FirstAccessNavigationFlowActivity>()
                dismiss()
            }
        }
    }
}