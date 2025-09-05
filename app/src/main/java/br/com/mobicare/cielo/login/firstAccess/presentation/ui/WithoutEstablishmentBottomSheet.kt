package br.com.mobicare.cielo.login.firstAccess.presentation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.ui.widget.FullScreenDialog
import br.com.mobicare.cielo.databinding.FragmentWithoutEstablishmentBottomSheetBinding
import br.com.mobicare.cielo.main.presentation.ui.MainBottomNavigationContract
import com.google.android.material.bottomsheet.BottomSheetBehavior

class WithoutEstablishmentBottomSheet : FullScreenDialog(), MainBottomNavigationContract.Listener {

    private var binding : FragmentWithoutEstablishmentBottomSheetBinding? = null
    lateinit var listener: MainBottomNavigationContract.Listener

    companion object{
        fun create(
            listener: MainBottomNavigationContract.Listener
        ) = WithoutEstablishmentBottomSheet().apply {
            this.listener = listener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentWithoutEstablishmentBottomSheetBinding.inflate(
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

    private fun setupListeners(){
        binding?.apply {
            ibBack.setOnClickListener {
                callLogout()
            }
            btnConfirm.setOnClickListener {
                callLogout()
            }
        }
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

    override fun callLogout() {
        listener.callLogout()
    }
}