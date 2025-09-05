package br.com.mobicare.cielo.taxaPlanos.presentation.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.databinding.LayoutRaCancelWhatsappDialogBinding
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4.Companion.CALL_CENTER
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4.Companion.RETENTION
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4.Companion.SCREEN_VIEW_RA_CANCEL_OFFERS
import br.com.mobicare.cielo.newRecebaRapido.analytics.RAGA4.Companion.TALK_TO_SPECIALIST
import org.koin.android.ext.android.inject

typealias DialogCallback = () -> Unit

private const val SCREEN_FILL_RATIO = 0.90

class RACancelWhatsappDialog private constructor() : DialogFragment() {

    private var binding: LayoutRaCancelWhatsappDialogBinding? = null
    private val ga4: RAGA4 by inject()

    private var onShowListener: DialogCallback? = null
    private var whatsappButtonClickListener: DialogCallback? = null
    private var onCloseIconClickListener: DialogCallback? = null
    private var onCancelListener: DialogCallback? = null
    private var whatsappLink: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LayoutRaCancelWhatsappDialogBinding.inflate(
            inflater, container, false
        ).also {
            binding = it
        }.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        whatsappLink = arguments?.getString("whatsappLink")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.background_cielo_dialog)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * SCREEN_FILL_RATIO).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        setupListeners()
        trackScreenView()
    }

    private fun setupListeners() {
        binding?.closeButton?.setOnClickListener {
            onCloseIconClickListener?.invoke()
            dismiss()
        }

        binding?.tvCallCenter?.setOnClickListener {
            trackClickAction(CALL_CENTER)
            dismiss()
            Utils.openLink(requireActivity(), CONTACT_CENTER_LINK)
        }
        binding?.whatsappButton?.setOnClickListener {
            trackClickAction(TALK_TO_SPECIALIST)
            whatsappButtonClickListener?.invoke()
            dismiss()
            Utils.openLink(requireActivity(), whatsappLink ?: RA_CANCEL_WHATSAPP_LINK_DEFAULT)
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
        onShowListener?.invoke()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onCancelListener?.invoke()
    }

    fun setOnWhatsappButtonClickListener(block: DialogCallback): RACancelWhatsappDialog {
        whatsappButtonClickListener = block
        return this
    }

    fun setOnCloseClickListener(block: DialogCallback): RACancelWhatsappDialog {
        onCloseIconClickListener = block
        return this
    }

    fun setOnShowListener(block: DialogCallback): RACancelWhatsappDialog {
        onShowListener = block
        return this
    }

    fun setOnCancelListener(block: DialogCallback): RACancelWhatsappDialog {
        onCancelListener = block
        return this
    }

    private fun trackClickAction(contentName: String) {
        ga4.logClick(
            screenName = SCREEN_VIEW_RA_CANCEL_OFFERS,
            contentName = contentName,
            contentComponent = RETENTION
        )
    }

    private fun trackScreenView() {
        ga4.logScreenView(
            SCREEN_VIEW_RA_CANCEL_OFFERS
        )
    }

    companion object {
        fun newInstance(whatsappLink: String?) = RACancelWhatsappDialog().apply {
            arguments = Bundle().apply {
                putString("whatsappLink", whatsappLink)
            }
        }

        private const val RA_CANCEL_WHATSAPP_LINK_DEFAULT =
            "https://wa.me/551130032818?text=Olá!%20%20Quero%20renegociar%20as%20condições%20do%20meu%20receba%20rápido.%20"
        private const val CONTACT_CENTER_LINK = "https://www.cielo.com.br/atendimento/"
    }
}