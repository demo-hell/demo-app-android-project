package br.com.mobicare.cielo.chargeback.presentation.details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.SCREEN_VIEW_CHARGEBACK_PENDING_ACCEPT
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.databinding.BottomSheetChargebackAcceptConfirmBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject

class ChargebackAcceptConfirmBottomSheet : BottomSheetDialogFragment() {

    private val ga4: ChargebackGA4 by inject()
    private var _binding: BottomSheetChargebackAcceptConfirmBinding? = null
    private val binding get() = _binding!!

    private var onAcceptConfirmTap: (() -> Unit)? = null

    companion object {
        fun create() = ChargebackAcceptConfirmBottomSheet()
    }

    fun setOnAcceptConfirmTapListener(callback: () -> Unit): ChargebackAcceptConfirmBottomSheet {
        onAcceptConfirmTap = callback
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBottomSheet(dialog = dialog, action = { dismiss() })

        return BottomSheetChargebackAcceptConfirmBinding.inflate(
            inflater, container, false
        ).also {
            _binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnConfirm.setOnClickListener(::onAccept)
            btnCancel.setOnClickListener(::onClose)
            ibClose.setOnClickListener(::onClose)
        }
    }

    private fun onClose(v: View) = dismissAllowingStateLoss()

    private fun onAccept(v: View) {
        dismissAllowingStateLoss()
        onAcceptConfirmTap?.invoke()
    }

    override fun onResume() {
        super.onResume()
        ga4.logScreenView(SCREEN_VIEW_CHARGEBACK_PENDING_ACCEPT)
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}